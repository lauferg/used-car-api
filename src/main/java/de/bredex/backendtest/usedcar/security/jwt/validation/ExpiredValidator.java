package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class ExpiredValidator implements JwtValidator {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public JwtValidationResult validateToken(String token, ApplicationUser applicationUser) {
        final Date expirationDate = jwtTokenUtil.extractClaim(token, claims -> claims.get("expiration", Date.class));
        final Date currentDate = new Date(System.currentTimeMillis());
        boolean tokenValid = expirationDate.after(currentDate);
        return new JwtValidationResult(ExpiredValidator.class, tokenValid);
    }
}
