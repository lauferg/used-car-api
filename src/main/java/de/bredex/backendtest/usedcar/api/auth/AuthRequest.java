package de.bredex.backendtest.usedcar.api.auth;

import lombok.Data;

@Data
public class AuthRequest {

    private String name;
    private String email;
}
