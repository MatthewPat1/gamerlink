package com.gamerlink.identity.service;

import com.gamerlink.identity.dto.request.LoginDTO;
import com.gamerlink.identity.dto.request.RegisterDTO;
import com.gamerlink.identity.dto.response.AuthenticationDTO;
import com.gamerlink.identity.exception.InvalidCodeException;
import com.gamerlink.identity.exception.InvalidCredentialsException;
import com.gamerlink.identity.model.*;
import com.gamerlink.identity.repository.*;
import com.gamerlink.identity.util.PasswordUtils;
import com.gamerlink.identity.util.RequestResponseHelper;
import com.gamerlink.identity.util.SecureTokenGenerator;
import com.gamerlink.shared.id.IdGenerator;
import com.gamerlink.shared.redis.RateLimitExceededException;
import com.gamerlink.shared.redis.RateLimiterService;
import com.gamerlink.shared.redis.TokenBlacklistService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserRoleRepository roleRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordResetChallengeRepository resetChallengeRepo;
    private final PasswordResetSessionRepository resetSessionRepo;
    private final ResetPasswordCookieService resetPasswordCookieService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;
    private final RateLimiterService rateLimiter;
    private final SmtpEmailService smtpEmailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashingService tokenHashingService;
    private final RefreshCookieService refreshCookieService;
    private final SecureTokenGenerator secureTokenGenerator;
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

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                String jti = jwtService.extractJti(accessToken);
                Date expiry = jwtService.extractExpiration(accessToken);
                tokenBlacklistService.blacklist(jti, expiry);
                userService.evictUserCache(jwtService.extractUserId(accessToken));
            } catch (Exception e) {
                // Malformed token — still complete logout normally
                log.warn("Could not blacklist access token during logout", e);
            }
        }

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
        String refreshValue = secureTokenGenerator.generate(64);
        String hashed = tokenHashingService.sha256Base64(refreshValue);
        long refreshTtlSeconds = refreshCookieService.getRefreshTtlSeconds();

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = RequestResponseHelper.getClientIpAddress(request);
        String deviceName = RequestResponseHelper.parseDeviceName(userAgent);

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

    @Transactional
    public void startPasswordReset(String email) {
        Instant now = Instant.now();
        String rateLimitKey = emailRateLimitKey("password_reset:start", email);

        if (!rateLimiter.isAllowed(rateLimitKey, 3, Duration.ofHours(1))) {
            Duration retryAfter = rateLimiter.getTimeUntilReset(rateLimitKey);
            throw new RateLimitExceededException(
                    "Too many password reset requests. Please try again later.",
                    rateLimitKey,
                    retryAfter
            );
        }

        Optional<User> userOpt = userRepo.findByEmailIgnoreCase(email);

        // Generate code regardless of user existence (timing attack prevention)
        String code = secureTokenGenerator.numericCode(6);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Invalidate old challenges
            resetChallengeRepo.invalidateActiveForUser(user.getId(), now);

            String codeHash = tokenHashingService.sha256Base64(code);
            PasswordResetChallenge challenge = new PasswordResetChallenge(
                    IdGenerator.newId(),
                    user.getId(),
                    codeHash,
                    Instant.now().plus(Duration.ofMinutes(10))
            );

            resetChallengeRepo.save(challenge);
            smtpEmailService.sendResetCode(user.getEmail(), code);
        }
    }


    @Transactional
    public ResponseCookie verifyResetCode(String email, String code) {
        Instant now = Instant.now();

        String verifyKey = emailRateLimitKey("password_reset:verify", email);
        if (!rateLimiter.isAllowed(verifyKey, 5, Duration.ofMinutes(10))) {
            Duration retryAfter = rateLimiter.getTimeUntilReset(verifyKey);
            throw new RateLimitExceededException(
                    "Too many verification attempts. Please try again later.",
                    verifyKey,
                    retryAfter
            );
        }

        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(InvalidCodeException::new);

        PasswordResetChallenge challenge =
                resetChallengeRepo
                        .findFirstByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(user.getId())
                        .orElseThrow(InvalidCodeException::new);

        // Wrong code → atomic attempt increment
        if (!tokenHashingService.matches(code, challenge.getCodeHash())) {
            int updated =
                    resetChallengeRepo.incrementAttemptsIfValid(challenge.getId(), now);

            if (updated == 0) {
                throw new InvalidCodeException();
            }

            throw new InvalidCodeException();
        }

        // Correct code → atomic verification
        int verified =
                resetChallengeRepo.markVerifiedIfValid(challenge.getId(), now);

        if (verified == 0) {
            throw new InvalidCodeException();
        }

        String rawSession = secureTokenGenerator.generate(64);
        String sessionHash = tokenHashingService.sha256Base64(rawSession);

        PasswordResetSession session =
                new PasswordResetSession(
                        IdGenerator.newId(),
                        user.getId(),
                        sessionHash,
                        now.plus(Duration.ofMinutes(15))
                );

        resetSessionRepo.save(session);
        return resetPasswordCookieService.create(rawSession);
    }

    @Transactional
    public void completePasswordReset(String rawResetToken, String newPassword) {
        Instant now = Instant.now();
        String hash = tokenHashingService.sha256Base64(rawResetToken);

        PasswordResetSession session =
                resetSessionRepo.findBySessionHash(hash)
                        .orElseThrow(InvalidCredentialsException::new);

        if (session.isExpired(now) || session.isUsed()) {
            throw new InvalidCredentialsException();
        }

        int updated = resetSessionRepo.markUsedIfUnused(session.getId(), now);
        if (updated == 0) {
            throw new InvalidCredentialsException();
        }

        User user = userRepo.findById(session.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        PasswordUtils.validatePassword(newPassword);
        user.changePassword(passwordEncoder.encode(newPassword));

        // Make the password change explicit in persistence
        userRepo.save(user);

        // Revoke existing refresh tokens immediately
        refreshRepo.revokeAllActiveForUser(user.getId(), now);
        resetSessionRepo.deleteAllForUser(user.getId());
    }


    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String emailRateLimitKey(String prefix, String email) {
        // Hash the normalized email so Redis keys + logs never contain raw PII.
        String normalized = normalizeEmail(email);
        String emailHash = tokenHashingService.sha256Base64(normalized);
        return prefix + ":" + emailHash;
    }

    public String getLastResetCode(String email) {
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(InvalidCodeException::new);

        PasswordResetChallenge challenge =
                resetChallengeRepo
                        .findFirstByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(user.getId())
                        .orElseThrow(InvalidCodeException::new);

        return challenge.getCodeHash();
    }
}
