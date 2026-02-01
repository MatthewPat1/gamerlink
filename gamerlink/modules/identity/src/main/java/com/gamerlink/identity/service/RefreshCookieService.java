package com.gamerlink.identity.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Data
public class RefreshCookieService {


    private final String cookieName;
    private final boolean secure;
    private final String sameSite;
    private final String path;
    private final long refreshTtlSeconds;


    public RefreshCookieService(
            @Value("${identity.app.security.refresh-cookie.name}") String cookieName,
            @Value("${identity.app.security.refresh-cookie.secure}") boolean secure,
            @Value("${identity.app.security.refresh-cookie.same-site}") String sameSite,
            @Value("${identity.app.security.refresh-cookie.path}") String path,
            @Value("${identity.app.security.refresh-cookie.refresh-ttl-days}") long refreshTtlDays
    ){
        this.cookieName = cookieName;
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
        this.refreshTtlSeconds = refreshTtlDays * 24 * 60 * 60;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .sameSite(sameSite)
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .sameSite(sameSite)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
