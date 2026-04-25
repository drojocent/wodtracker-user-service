package com.wodtracker.userservice.service.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecureTemporaryPasswordServiceTest {

    private final SecureTemporaryPasswordService service = new SecureTemporaryPasswordService();

    @Test
    void shouldGeneratePasswordWithExpectedLength() {
        String password = service.generateTemporaryPassword();

        assertThat(password).hasSize(12);
    }

    @Test
    void shouldGeneratePasswordUsingAllowedCharacters() {
        String password = service.generateTemporaryPassword();

        assertThat(password).matches("[ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%]{12}");
    }
}
