package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AssignedToUserValidator implements JwtValidator {

    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public JwtValidationResult validateToken(String token, ApplicationUser applicationUser) {
        boolean tokenValid = jwtTokenRepository.existsByTokenAndApplicationUser(token, applicationUser);
        return new JwtValidationResult(AssignedToUserValidator.class, tokenValid);
    }
}
