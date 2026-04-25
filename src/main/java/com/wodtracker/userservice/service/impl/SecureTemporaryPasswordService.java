package com.wodtracker.userservice.service.impl;

import com.wodtracker.userservice.service.TemporaryPasswordService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class SecureTemporaryPasswordService implements TemporaryPasswordService {

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateTemporaryPassword() {
        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(ALLOWED_CHARACTERS.length());
            builder.append(ALLOWED_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }
}
