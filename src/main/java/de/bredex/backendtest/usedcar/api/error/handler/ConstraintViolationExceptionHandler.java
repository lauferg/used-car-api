package de.bredex.backendtest.usedcar.api.error.handler;

import de.bredex.backendtest.usedcar.api.error.response.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ConstraintViolationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorResponse> handle(MethodArgumentNotValidException exception) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        List<String> errorMessages = exception
                .getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        response.getErrorMessages().addAll(errorMessages);
        return ResponseEntity.badRequest().body(response);
    }
}
