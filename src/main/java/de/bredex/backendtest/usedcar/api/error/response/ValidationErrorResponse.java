package de.bredex.backendtest.usedcar.api.error.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ValidationErrorResponse {

    private final List<String> errorMessages = new ArrayList<>();
}
