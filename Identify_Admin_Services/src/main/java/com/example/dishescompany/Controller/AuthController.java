// src/main/java/com/example/dishescompany/controller/AuthController.java
package com.example.dishescompany.Controller;

import com.example.dishescompany.DTO.AuthResponse;
import com.example.dishescompany.DTO.LoginRequest;
import com.example.dishescompany.DTO.RegisterRequest;
import com.example.dishescompany.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:63342")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new customer.
     * Always creates a CUSTOMER with the supplied address.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse("Registration successful"));
    }

    /**
     * Login with username & password.
     * Returns the user’s role for client-side routing.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req) {
        String role = authService.login(req);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role",    role
        ));
    }

    /**
     * Fallback for exceptions to return JSON error messages.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<AuthResponse> handleError(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new AuthResponse(ex.getReason()));
    }
}
