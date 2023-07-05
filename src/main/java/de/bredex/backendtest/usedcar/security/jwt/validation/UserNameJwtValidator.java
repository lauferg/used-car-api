package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ValidationResult;

@Component
@RequiredArgsConstructor
public class UserNameJwtValidator implements JwtValidator {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public JwtValidationResult validateToken(String token, ApplicationUser applicationUser) {
        final String tokenUserName = jwtTokenUtil.extractTokenUserName(token);
        boolean tokenValid = tokenUserName.equals(applicationUser.getName());
        return new JwtValidationResult(UserNameJwtValidator.class, tokenValid);
    }
}
