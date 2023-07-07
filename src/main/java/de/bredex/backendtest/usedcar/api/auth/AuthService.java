package de.bredex.backendtest.usedcar.api.auth;

import de.bredex.backendtest.usedcar.api.auth.request.AuthRequest;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import de.bredex.backendtest.usedcar.security.jwt.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;
    private final ApplicationUserRepository applicationUserRepository;

    public String login(AuthRequest authRequest) {
        final String userEmail = authRequest.getEmail();
        final String userName = authRequest.getName();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userEmail,
                userName,
                Collections.emptyList()
        );

        authenticationManager.authenticate(authToken);
        ApplicationUser applicationUser = applicationUserRepository.findByEmailAndName(userEmail, userName).orElseThrow();
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return jwtTokenManager.issueToken(applicationUser, Collections.emptyMap());
    }

    public String register(AuthRequest authRequest) {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setEmail(authRequest.getEmail());
        newUser.setName(authRequest.getName());
        applicationUserRepository.save(newUser);
        return jwtTokenManager.issueToken(newUser, Collections.emptyMap());
    }

    public void logout(Authentication authentication) {
        final String userEmail = (String) authentication.getPrincipal();
        final String userName = (String) authentication.getCredentials();
        ApplicationUser applicationUser = applicationUserRepository
                .findByEmailAndName(userEmail, userName)
                .orElseThrow();
        jwtTokenManager.blacklistToken(applicationUser);
        SecurityContextHolder.clearContext();
    }
}
