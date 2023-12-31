package de.bredex.backendtest.usedcar.api.auth;

import de.bredex.backendtest.usedcar.api.auth.request.AuthRequest;
import de.bredex.backendtest.usedcar.api.auth.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User registration.")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid AuthRequest authRequest) {
        final String jwt = authService.register(authRequest);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "User login.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        final String jwt = authService.login(authRequest);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logs out a user that is currently logged in.", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/logout")
    public void logout(Authentication authentication) {
        authService.logout(authentication);
    }
}
