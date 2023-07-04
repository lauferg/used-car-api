package de.bredex.backendtest.usedcar.api.auth;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import de.bredex.backendtest.usedcar.security.jwt.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenManager tokenManager;
    private final AuthenticationManager authenticationManager;
    private final ApplicationUserRepository applicationUserRepository;

    public AuthResponse login(AuthRequest authRequest) {
        final String userEmail = authRequest.getEmail();
        final String userName = authRequest.getName();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userName,
                userEmail,
                Collections.emptyList()
        );

        authenticationManager.authenticate(authToken);

        ApplicationUser applicationUser = applicationUserRepository.findByEmailAndName(userEmail, userName).orElseThrow();
        final String jwt = tokenManager.issueToken(applicationUser, Collections.emptyMap());
        return AuthResponse
                .builder()
                .token(jwt)
                .build();
    }

    public AuthResponse register(AuthRequest authRequest) {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setEmail(authRequest.getEmail());
        newUser.setName(authRequest.getName());
        applicationUserRepository.save(newUser);
        final String jwt = tokenManager.issueToken(newUser, Collections.emptyMap());
        return AuthResponse
                .builder()
                .token(jwt)
                .build();
    }
}
