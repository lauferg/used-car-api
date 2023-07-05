package de.bredex.backendtest.usedcar.security.jwt.validation;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;

public interface JwtValidator {

    JwtValidationResult validateToken(String token, ApplicationUser applicationUser);
}
