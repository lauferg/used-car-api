package de.bredex.backendtest.usedcar.api.auth.request;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Validated
public class AuthRequest {

    @Size(min = 1, max = 50, message = "Name has to be between 1 and 50 characters in length.")
    private String name;
    @Pattern(regexp = "^\\w+@\\w+\\.\\w+", message = "Email should be of a plausible email format.")
    private String email;
}
