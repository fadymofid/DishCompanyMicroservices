package com.example.dishescompany.Controller;

import com.example.dishescompany.DTO.AuthResponse;
import com.example.dishescompany.DTO.LoginRequest;
import com.example.dishescompany.DTO.RegisterRequest;
import com.example.dishescompany.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(new AuthResponse("Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authService.login(req);
        return ResponseEntity.ok(new AuthResponse("Login successful"));
    }
}


