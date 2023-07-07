package de.bredex.backendtest.usedcar.api.ad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bredex.backendtest.usedcar.UsedCarApplication;
import de.bredex.backendtest.usedcar.api.ad.dto.AdDto;
import de.bredex.backendtest.usedcar.api.ad.response.AdSearchResponse;
import de.bredex.backendtest.usedcar.api.ad.response.NewAdResponse;
import de.bredex.backendtest.usedcar.api.auth.request.AuthRequest;
import de.bredex.backendtest.usedcar.api.auth.response.AuthResponse;
import de.bredex.backendtest.usedcar.data.ad.Ad;
import de.bredex.backendtest.usedcar.data.ad.AdRepository;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(classes = UsedCarApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdControllerIT {

    @Autowired
    private AdRepository adRepository;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Transactional
    public void searchEndpointTest() throws JSONException, JsonProcessingException {
        String makeKeyword = "renault";
        String typeKeyword = "9";
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/ad/search")
                .port(port)
                .queryParam("makeKeyword", makeKeyword)
                .queryParam("typeKeyword", typeKeyword)
                .build();

        RequestEntity<Void> requestEntity = RequestEntity.get(uriComponents.encode().toUriString()).build();
        ResponseEntity<AdSearchResponse> response = executeAndValidate(uriComponents, GET, requestEntity, AdSearchResponse.class);
        String actual = objectMapper.writeValueAsString(response.getBody());
        String expected = "{\"adUrls\":[\"http://localhost:" + port + "/ad/2\",\"http://localhost:" + port + "/ad/5\"]}";
        JSONAssert.assertEquals(
                expected,
                actual,
                true);
    }

    @Test
    @Transactional
    public void getEndpointTest() throws JsonProcessingException, JSONException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/ad/")
                .port(port)
                .pathSegment("5")
                .build();

        ResponseEntity<AdDto> response = executeAndValidate
                (uriComponents, GET, RequestEntity.get(uriComponents.encode().toUriString()).build(), AdDto.class);
        String actual = objectMapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(
                "{\"make\":\"Renault\",\"type\":\"R 19 Chamade\",\"description\":\"Please take this off of my hand. I'm giving it away for free. Could be used as a chicken coop or to transport cement in the trunk. Every single nut and bolt was replaced at some point in time.\",\"price\":0}",
                actual,
                true);
    }

    @Test
    @Transactional
    public void postEndpointTest() throws JsonProcessingException, JSONException {
        final String jwt = logIn();
        final String testMakeLiteral = "Test Make";
        final String testTypeLiteral = "Test Type";
        final String testDescriptionLiteral = "Test Description";
        long testPrice = 9999999999L;
        AdDto adDto = new AdDto();
        adDto.setType(testTypeLiteral);
        adDto.setMake(testMakeLiteral);
        adDto.setDescription(testDescriptionLiteral);
        adDto.setPrice(testPrice);
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/ad/")
                .port(port)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + jwt);
        HttpEntity<AdDto> httpEntity = RequestEntity
                .post(uriComponents.encode().toUriString())
                .headers(httpHeaders)
                .body(adDto);
        ResponseEntity<NewAdResponse> result = executeAndValidate(uriComponents, POST, httpEntity, NewAdResponse.class);
        String actualResponseJson = objectMapper.writeValueAsString(result.getBody());
        String expectedResponse = "{\"url\": \"http://localhost:" + port + "/ad/6\"}";
        Ad actualAdEntity = adRepository.findById(6L).orElseThrow();
        Ad expectedAdEntity = new Ad();
        expectedAdEntity.setMake(testMakeLiteral);
        expectedAdEntity.setType(testTypeLiteral);
        expectedAdEntity.setDescription(testDescriptionLiteral);
        expectedAdEntity.setPrice(testPrice);
        expectedAdEntity.setApplicationUser(applicationUserRepository.findById("tester01@gmail.com").orElseThrow());
        JSONAssert.assertEquals(expectedResponse, actualResponseJson, true);
        assertThat(actualAdEntity)
                .usingRecursiveComparison(RecursiveComparisonConfiguration
                        .builder()
                        .withIgnoredFields("id")
                        .build())
                .isEqualTo(expectedAdEntity);
    }

    @Test
    @Transactional
    void deleteEndpointTest() {
        final String jwt = logIn();
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/ad")
                .pathSegment("1")
                .port(port)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        HttpEntity<Void> httpEntity = RequestEntity
                .delete(uriComponents.encode()
                        .toUriString())
                .headers(httpHeaders)
                .build();
        ResponseEntity<Void> response = executeAndValidate(uriComponents, DELETE, httpEntity, Void.class);
    }

    private String logIn() {
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/auth/login")
                .port(port)
                .build();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("tester01@gmail.com");
        authRequest.setName("tester01");
        HttpEntity<AuthRequest> authRequestHttpEntity = new HttpEntity<>(authRequest);
        ResponseEntity<AuthResponse> response = executeAndValidate(uriComponents, POST, authRequestHttpEntity, AuthResponse.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        return response.getBody().getToken();
    }

    private <T> ResponseEntity<T> executeAndValidate(UriComponents uriComponents, HttpMethod httpMethod, HttpEntity<?> requestEntity, Class<T> klazz) {
        String urlTemplate = uriComponents
                .encode()
                .toUriString();

        ResponseEntity<T> response = testRestTemplate.exchange(urlTemplate, httpMethod, requestEntity, klazz);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        return response;
    }
}