package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtToken;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BlacklistedValidator implements JwtValidator {

    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public JwtValidationResult validateToken(String token, ApplicationUser applicationUser) {
        Optional<JwtToken> jwtTokenOptional = jwtTokenRepository.findByApplicationUserAndToken(applicationUser, token);
        boolean tokenValid = jwtTokenOptional.isPresent() && !jwtTokenOptional.get().isBlacklisted();
        return new JwtValidationResult(BlacklistedValidator.class, tokenValid);
    }
}
