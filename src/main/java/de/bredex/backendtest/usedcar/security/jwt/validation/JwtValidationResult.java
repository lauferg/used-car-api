package de.bredex.backendtest.usedcar.security.jwt.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JwtValidationResult {

    private final Class<? extends JwtValidator> validatorClass;
    private final boolean tokenValid;
}
