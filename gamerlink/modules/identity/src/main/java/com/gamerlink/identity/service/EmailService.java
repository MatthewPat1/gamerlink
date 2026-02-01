package com.gamerlink.identity.service;

public interface EmailService {
    void sendResetCode(String toEmail, String code);
}
