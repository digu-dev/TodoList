package com.digu.dev.TodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digu.dev.TodoList.dto.LoginRequest;
import com.digu.dev.TodoList.dto.LoginResponse;
import com.digu.dev.TodoList.exceptions.ResponseError;
import com.digu.dev.TodoList.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            String token = jwtService.generateToken(authentication);
            LoginResponse response = new LoginResponse(
                    token,
                    "Bearer",
                    86400L,
                    "Welcome, " + authentication.getName() + "!");

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            ResponseError error = ResponseError.patternResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

}
