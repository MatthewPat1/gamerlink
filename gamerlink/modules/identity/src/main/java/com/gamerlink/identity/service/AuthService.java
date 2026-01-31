package com.gamerlink.identity.service;

import com.gamerlink.identity.dto.request.LoginDTO;
import com.gamerlink.identity.dto.request.RegisterDTO;
import com.gamerlink.identity.dto.response.AuthenticationDTO;
import com.gamerlink.identity.exception.InvalidCredentialsException;
import com.gamerlink.identity.model.RefreshToken;
import com.gamerlink.identity.model.User;
import com.gamerlink.identity.model.UserRole;
import com.gamerlink.identity.repository.RefreshTokenRepository;
import com.gamerlink.identity.repository.UserRepository;
import com.gamerlink.identity.repository.UserRoleRepository;
import com.gamerlink.identity.util.PasswordUtils;
import com.gamerlink.shared.id.IdGenerator;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserRoleRepository roleRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashingService tokenHashingService;
    private final RefreshCookieService refreshCookieService;
    private final AuthenticationManager authenticationManager;
    private final EntityManager entityManager;

    @Transactional
    public AuthenticationDTO register(RegisterDTO registerDTO, HttpServletRequest request, HttpServletResponse response) {
        PasswordUtils.validatePassword(registerDTO.getPassword(), registerDTO.getConfirmPassword());

        if (userRepo.existsByEmailIgnoreCase(registerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        UUID userId = IdGenerator.newId();
        User user = User.builder()
                .id(userId)
                .email(registerDTO.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(registerDTO.getPassword()))
                .build();
        userRepo.save(user);

        UserRole role = UserRole.builder()
                .user(user)
                .role("USER")
                .build();
        roleRepo.save(role);

        return issueTokensAndSetCookie(userId, request, response);
    }

    public AuthenticationDTO login(@Valid LoginDTO loginDTO, HttpServletRequest request,HttpServletResponse response) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        return issueTokensAndSetCookie(user.getId(), request, response);
    }

    @Transactional
    public AuthenticationDTO refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefresh = refreshCookieService.readRefreshCookie(request);

        if (rawRefresh == null || rawRefresh.isBlank()) {
            throw new IllegalArgumentException("No refresh token provided");
        }

        // hash and lookup
        String tokenHash = tokenHashingService.sha256Base64(rawRefresh);
        RefreshToken stored = refreshRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        UUID userId = stored.getUser().getId();
        if (stored.isRevoked()) {
            log.error("SECURITY ALERT: Attempted reuse of revoked refresh token for user: {}", userId);

            // Token reuse detected - possible attack
            // Revoke ALL refresh tokens for this user as a safety measure
            int revokedCount = refreshRepo.revokeAllActiveForUser(userId);
            log.warn("Revoked {} active tokens for user {} due to token reuse", revokedCount, userId);

            throw new SecurityException("Token reuse detected. All sessions invalidated for security.");
        }

        // 4. Check expiration
        if (stored.isExpired()) {
            log.info("Refresh token expired for user: {}", userId);
            throw new IllegalArgumentException("Refresh token expired. Please login again.");
        }

        // revoke old refresh token
        stored.revoke();
        refreshRepo.save(stored);

        // issue fresh tokens
        return issueTokensAndSetCookie(userId, request, response);
    }
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String rawRefresh = refreshCookieService.readRefreshCookie(request);
        if (rawRefresh != null && !rawRefresh.isBlank()) {
            String hash = tokenHashingService.sha256Base64(rawRefresh);
            refreshRepo.findByTokenHash(hash)
                    .ifPresent(refreshRepo::delete);
        }
        refreshCookieService.clearRefreshCookie(response);
    }

    @Transactional
    public void logoutAllDevices(UUID userId) {
        int count = refreshRepo.deleteByUserId(userId);
        log.info("User {} logged out from {} devices", userId, count);
    }

    @Transactional
    private AuthenticationDTO issueTokensAndSetCookie(UUID userId, HttpServletRequest request, HttpServletResponse response) {
        // roles for access token
        List<String> roles = roleRepo.findAllByUserId(userId).stream()
                .map(UserRole::getRole)
                .toList();

        // generate access token
        String accessToken = jwtService.generateToken(userId, roles);

        // generate a new refresh token
        String refreshValue = refreshCookieService.generateRefreshTokenValue();
        String hashed = tokenHashingService.sha256Base64(refreshValue);
        long refreshTtlSeconds = refreshCookieService.getRefreshTtlSeconds();

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String deviceName = parseDeviceName(userAgent);

        // save refresh in DB
        User user = entityManager.getReference(User.class, userId);
        RefreshToken rt = RefreshToken.builder()
                .id(IdGenerator.newId())
                .user(user)
                .tokenHash(hashed)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .deviceName(deviceName)
                .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
                .build();

        refreshRepo.save(rt);

        // set refresh cookie (HttpOnly)
        refreshCookieService.setRefreshCookie(response, refreshValue, refreshTtlSeconds);

        return AuthenticationDTO.builder()
                .accessToken(accessToken)
                .expiresIn(jwtService.getAccessTtlSeconds())
                .build();
    }

    // Helper to get client IP (handles proxies)
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    // Optional: Parse user agent to friendly name
    private String parseDeviceName(String userAgent) {
        if (userAgent == null) return "Unknown Device";

        // Simple parsing (use library like UADetector for better results)
        if (userAgent.contains("iPhone")) return "iPhone";
        if (userAgent.contains("iPad")) return "iPad";
        if (userAgent.contains("Android")) return "Android Device";
        if (userAgent.contains("Chrome")) return "Chrome Browser";
        if (userAgent.contains("Safari")) return "Safari Browser";
        if (userAgent.contains("Firefox")) return "Firefox Browser";

        return "Unknown Device";
    }
}
