package de.bredex.backendtest.usedcar.api.auth;

import de.bredex.backendtest.usedcar.UsedCarApplication;
import de.bredex.backendtest.usedcar.api.auth.request.AuthRequest;
import de.bredex.backendtest.usedcar.api.auth.response.AuthResponse;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtToken;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UsedCarApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private JwtTokenRepository jwtTokenRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Transactional
    @Test
    void signUpTest() {
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/auth/signup")
                .port(port)
                .build();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setName("tester99");
        authRequest.setEmail(UUID.randomUUID() + "@bredex.de");
        HttpEntity<AuthRequest> httpEntity = RequestEntity.post(uriComponents.encode().toUriString()).body(authRequest);
        ResponseEntity<AuthResponse> result = executeAndValidate(uriComponents, httpEntity, AuthResponse.class);
        assertThat(result.getBody()).isNotNull();
        String jwt = result.getBody().getToken();
        ApplicationUser newUser = applicationUserRepository
                .findByEmailAndName(authRequest.getEmail(), authRequest.getName()).orElseThrow();
        assertThat(newUser).isNotNull();
        JwtToken storedToken = jwtTokenRepository.findByApplicationUser(newUser).orElseThrow();
        assertThat(storedToken.getToken()).isEqualTo(jwt);
    }

    @Transactional
    @Test
    void loginTest() {
        String jwt = logIn("tester01@gmail.com", "tester01");
        boolean tokenPresent = jwtTokenRepository.existsById(jwt);
        assertThat(jwt).isNotBlank();
        assertThat(jwt).matches("^.+\\..*\\..+$");
        assertThat(tokenPresent).isTrue();
    }

    @Transactional
    @Test
    void logoutTest() {
        String jwt = logIn("tester03@bredex.de", "tester03");
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/auth/logout")
                .port(port)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        HttpEntity<Void> httpEntity = RequestEntity
                .post(uriComponents.encode().toUriString())
                .headers(httpHeaders)
                .build();
        executeAndValidate(uriComponents, httpEntity, Void.class);
        JwtToken token = jwtTokenRepository.findById(jwt).orElseThrow();
        assertThat(token.isBlacklisted()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private String logIn(String email, String name) {
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/auth/login")
                .port(port)
                .build();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setName(name);
        HttpEntity<AuthRequest> authRequestHttpEntity = new HttpEntity<>(authRequest);
        ResponseEntity<AuthResponse> response = executeAndValidate(uriComponents, authRequestHttpEntity, AuthResponse.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        return response.getBody().getToken();
    }
    private <T> ResponseEntity<T> executeAndValidate(UriComponents uriComponents, HttpEntity<?> requestEntity, Class<T> klazz) {
        String urlTemplate = uriComponents
                .encode()
                .toUriString();

        ResponseEntity<T> response = testRestTemplate.exchange(urlTemplate, HttpMethod.POST, requestEntity, klazz);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        return response;
    }
}