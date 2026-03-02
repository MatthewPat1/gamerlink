package com.gamerlink.identity.controller;

import com.gamerlink.identity.dto.response.MeDTO;
import com.gamerlink.identity.dto.response.SessionDTO;
import com.gamerlink.identity.dto.request.UpdateMeDTO;
import com.gamerlink.identity.service.UserService;
import com.gamerlink.shared.util.SecurityContextHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MeDTO> me() {
        UUID userId = SecurityContextHelper.getCurrentUserId();

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MeDTO> updateMe(@RequestBody UpdateMeDTO updateMeDTO){
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, updateMeDTO));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDTO>> getActiveSessions(HttpServletRequest request) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(userService.getActiveSessions(userId, request));

    }

}

