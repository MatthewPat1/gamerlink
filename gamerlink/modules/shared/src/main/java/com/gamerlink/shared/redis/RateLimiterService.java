package com.gamerlink.shared.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);
    private static final String KEY_PREFIX = "ratelimit:";

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitConfig config;

    // IMPORTANT: this must be the String-based template (rateLimitRedisTemplate)
    public RateLimiterService(
            @Qualifier("rateLimitRedisTemplate") RedisTemplate<String, String> rateLimitRedisTemplate,
            RateLimitConfig config) {
        this.redisTemplate = rateLimitRedisTemplate;
        this.config = config;
    }

    /**
     * Convenience overload using global defaults.
     */
    public boolean isAllowed(String key) {
        return isAllowed(
                key,
                config.getGlobal().getDefaultMaxAttempts(),
                Duration.ofSeconds(config.getGlobal().getDefaultWindowSeconds())
        );
    }

    /**
     * Check if action is allowed under rate limit
     * @param key - identifier (should NOT contain raw PII; hash it at call site)
     * @param maxAttempts - maximum attempts allowed
     * @param window - time window duration
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowed(String key, int maxAttempts, Duration window) {
        String redisKey = KEY_PREFIX + key;

        try {
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);

            if (currentCount == null) {
                log.warn("Redis increment returned null for redisKey: {}", redisKey);
                return handleRedisFailure();
            }

            // TTL safety: ensure a TTL always exists (prevents "forever" keys if EXPIRE is skipped due to crash)
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl == null || ttl < 0) {
                redisTemplate.expire(redisKey, window.getSeconds(), TimeUnit.SECONDS);
            }

            boolean allowed = currentCount <= maxAttempts;

            if (!allowed) {
                // Do not log raw PII - caller should pass hashed key anyway
                log.warn("Rate limit exceeded for redisKey: {} (count: {}/{})",
                        redisKey, currentCount, maxAttempts);
            }

            return allowed;

        } catch (Exception e) {
            log.error("Redis error in rate limiter for redisKey: {}", redisKey, e);
            return handleRedisFailure();
        }
    }

    /**
     * Get remaining attempts for a key
     */
    public int getRemainingAttempts(String key, int maxAttempts) {
        String redisKey = KEY_PREFIX + key;

        try {
            String value = redisTemplate.opsForValue().get(redisKey);
            if (value == null) {
                return maxAttempts;
            }

            int currentCount = Integer.parseInt(value);
            return Math.max(0, maxAttempts - currentCount);

        } catch (Exception e) {
            log.error("Error getting remaining attempts for redisKey: {}", redisKey, e);
            return 0;
        }
    }

    /**
     * Get time until rate limit resets
     */
    public Duration getTimeUntilReset(String key) {
        String redisKey = KEY_PREFIX + key;

        try {
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl == null || ttl < 0) {
                return Duration.ZERO;
            }
            return Duration.ofSeconds(ttl);

        } catch (Exception e) {
            log.error("Error getting TTL for redisKey: {}", redisKey, e);
            return Duration.ZERO;
        }
    }

    /**
     * Manually reset rate limit for a key
     */
    public void reset(String key) {
        String redisKey = KEY_PREFIX + key;
        try {
            redisTemplate.delete(redisKey);
            log.info("Reset rate limit for redisKey: {}", redisKey);
        } catch (Exception e) {
            log.error("Error resetting rate limit for redisKey: {}", redisKey, e);
        }
    }

    /**
     * Increment counter without checking limit (for metrics)
     */
    public void recordAttempt(String key, Duration window) {
        String redisKey = KEY_PREFIX + key;

        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == null) return;

            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl == null || ttl < 0) {
                redisTemplate.expire(redisKey, window.getSeconds(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("Error recording attempt for redisKey: {}", redisKey, e);
        }
    }

    /**
     * Handle Redis failure based on configuration
     */
    private boolean handleRedisFailure() {
        return config.isFailOpen();
    }
}
