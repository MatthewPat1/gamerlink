package com.gamerlink.identity.controller;

import com.gamerlink.identity.dto.request.LoginDTO;
import com.gamerlink.identity.dto.request.RegisterDTO;
import com.gamerlink.identity.dto.response.AuthenticationDTO;
import com.gamerlink.identity.model.UserPrincipal;
import com.gamerlink.identity.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationDTO> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerDTO, request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDTO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok(authService.login(loginDTO, request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }


}
