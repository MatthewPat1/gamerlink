package com.gamerlink.app.exception;

import com.gamerlink.app.dto.ApiErrorDTO;
import com.gamerlink.identity.exception.AccountDisabledException;
import com.gamerlink.identity.exception.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorDTO.builder()
                        .code("INVALID_CREDENTIALS")
                        .error(ex.getMessage())
                        .timestamp(Instant.now())
                        .status(HttpStatus.UNAUTHORIZED.toString())
                        .build());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ApiErrorDTO> handleAccountDisabled() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiErrorDTO.builder()
                        .code("ACCOUNT_DISABLED")
                        .status(HttpStatus.FORBIDDEN.toString())
                        .timestamp(Instant.now())
                        .error("Account is not active")
                        .build());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorDTO.builder()
                        .timestamp(Instant.now())
                        .code("RESOURCE_NOT_FOUND")
                        .status(HttpStatus.NOT_FOUND.toString())
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorDTO.builder()
                        .code("BAD_CREDENTIALS")
                        .error("User not found")
                        .timestamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleUnexpected(Exception ex) {
        // log ex internally

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDTO.builder()
                        .code("INTERNAL_ERROR")
                        .timestamp(Instant.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .error("Unexpected error will be logged and looked into")
                        .build());
    }
}

