package com.gamerlink.identity.controller;

import com.gamerlink.identity.dto.request.*;
import com.gamerlink.identity.dto.response.AuthenticationDTO;
import com.gamerlink.identity.dto.response.MessageResponseDTO;
import com.gamerlink.identity.service.AuthService;
import com.gamerlink.identity.service.ResetPasswordCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final ResetPasswordCookieService resetPasswordCookieService;

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

    @PostMapping("/password/reset/start")
    public ResponseEntity<MessageResponseDTO> startReset(@RequestBody @Valid EmailRequestDTO req) {
        authService.startPasswordReset(req.getEmail());
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("If an account was found we sent an email")
                .build()
        );
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid PasswordResetVerifyRequestDTO req) {
        ResponseCookie cookie =
                authService.verifyResetCode(req.getEmail(), req.getCode());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/password/reset/complete")
    public ResponseEntity<?> completeReset(@CookieValue("gl_reset_session") String resetSession, @Valid @RequestBody PasswordResetCompleteRequestDTO req) {
        authService.completePasswordReset(resetSession, req.getNewPassword());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        resetPasswordCookieService.clear().toString())
                .build();
    }

    @GetMapping("/internal/test/last-reset-code?email={resetEmail}")
    public ResponseEntity<String> getResetCode(@PathVariable("resetEmail") String email){
        return ResponseEntity.ok(authService.getLastResetCode(email));
    }



}
