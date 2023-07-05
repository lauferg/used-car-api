package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenOwnerMatchValidator implements JwtValidator {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public JwtValidationResult validateToken(String token, ApplicationUser applicationUser) {
        final String tokenOwnerId = jwtTokenUtil.extractTokenOwnerId(token);
        boolean tokenValid = tokenOwnerId.equals(applicationUser.getEmail());
        return new JwtValidationResult(TokenOwnerMatchValidator.class, tokenValid);
    }
}
