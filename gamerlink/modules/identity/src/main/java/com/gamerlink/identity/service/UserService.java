package com.gamerlink.identity.service;

import com.gamerlink.identity.dto.request.UpdateMeDTO;
import com.gamerlink.identity.dto.response.MeDTO;
import com.gamerlink.identity.dto.response.SessionDTO;
import com.gamerlink.identity.exception.UpdateUserException;
import com.gamerlink.identity.model.RefreshToken;
import com.gamerlink.identity.model.User;
import com.gamerlink.identity.model.UserRole;
import com.gamerlink.identity.repository.RefreshTokenRepository;
import com.gamerlink.identity.repository.UserRepository;
import com.gamerlink.identity.repository.UserRoleRepository;
import com.gamerlink.identity.util.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserRoleRepository roleRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshCookieService refreshCookieService;
    private final TokenHashingService tokenHashingService;


    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId")
    public MeDTO getUserById(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return toDTO(user);
    }

    @Transactional
    @CachePut(value = "users", key = "#userId")
    public MeDTO updateUser(UUID userId, UpdateMeDTO updateMeDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (updateMeDTO.getEmail() != null) {
            user.changeEmail(updateMeDTO.getEmail());
        }
        if (updateMeDTO.getStatus() != null &&
                !updateMeDTO.getStatus().equals("ACTIVE") &&
                !updateMeDTO.getStatus().equals(user.getStatus())) {
            user.changeStatus(updateMeDTO.getStatus());
        }
        if (updateMeDTO.getNewPassword() != null
                && updateMeDTO.getConfirmPassword() != null
                && updateMeDTO.getOldPassword() != null) {

            if (user.getPasswordHash()
                    .equals(passwordEncoder.encode(updateMeDTO.getOldPassword()))) {
                PasswordUtils.validatePassword(updateMeDTO.getNewPassword(), updateMeDTO.getConfirmPassword());
            }else{
                throw new UpdateUserException("Old Password isn't correct");
            }
        }

        return toDTO(userRepo.save(user));
    }

    @Transactional(readOnly = true)
    private MeDTO toDTO(User user) {
        List<UserRole> userRoles = roleRepo.findAllByUserId(user.getId());
        return MeDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .roles(userRoles.stream()
                        .map(UserRole::getRole)
                        .toList())
                .status(user.getStatus())
                .build();
    }

    public List<SessionDTO> getActiveSessions(UUID userId, HttpServletRequest request) {
        List<RefreshToken> tokens = refreshRepo.findActiveByUserId(userId);

        List<SessionDTO> sessions = tokens.stream()
                .map(token -> SessionDTO.builder()
                        .deviceName(token.getDeviceName())
                        .ipAddress(token.getIpAddress())
                        .lastActive(token.getCreatedAt())
                        .isCurrent(isCurrentSession(token, request))
                        .tokenId(token.getId())
                        .build())
                .toList();
        return sessions;
    }

    @CacheEvict(value = "userProfile", key = "#userId")
    public void evictUserCache(UUID userId) {
        // no-op — annotation does the work
    }

    // Helper to identify current session
    private boolean isCurrentSession(RefreshToken token, HttpServletRequest request) {
        String currentRefresh = refreshCookieService.readRefreshCookie(request);
        if (currentRefresh == null) return false;

        String currentHash = tokenHashingService.sha256Base64(currentRefresh);
        return token.getTokenHash().equals(currentHash);
    }
}
