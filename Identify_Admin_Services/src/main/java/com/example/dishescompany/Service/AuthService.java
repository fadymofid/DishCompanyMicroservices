package com.example.dishescompany.Service;

import com.example.dishescompany.DTO.LoginRequest;
import com.example.dishescompany.DTO.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    String login(LoginRequest request);
}