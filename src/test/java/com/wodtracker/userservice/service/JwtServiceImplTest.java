package com.wodtracker.userservice.service;

import com.wodtracker.userservice.config.JwtConfig;
import com.wodtracker.userservice.security.UserPrincipal;
import com.wodtracker.userservice.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {JwtConfig.class, JwtServiceImpl.class},
        properties = {
                "security.jwt.secret=VGVzdFdPRFRyYWNrZXJVc2VyU2VydmljZUp3dFNlY3JldEtleTEyMzQ1Njc4OTA=",
                "security.jwt.expiration-minutes=30"
        }
)
class JwtServiceImplTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateAndDecodeJwtSuccessfully() {
        UserPrincipal userPrincipal = new UserPrincipal(
                42L,
                "athlete@example.com",
                "encoded-password",
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = jwtService.generateToken(userPrincipal);
        Jwt decodedJwt = jwtService.decodeToken(token);

        assertThat(token).isNotBlank();
        assertThat(decodedJwt.getSubject()).isEqualTo("42");
        assertThat(decodedJwt.getClaimAsString("iss")).isEqualTo("wodtracker-user-service");
        assertThat(decodedJwt.getClaimAsString("email")).isEqualTo("athlete@example.com");
        assertThat(decodedJwt.getClaimAsStringList("roles")).containsExactly("ADMIN");
        assertThat(decodedJwt.getIssuedAt()).isNotNull();
        assertThat(decodedJwt.getExpiresAt()).isNotNull();
        assertThat(decodedJwt.getExpiresAt()).isAfter(decodedJwt.getIssuedAt());
    }

    @Test
    void shouldExposeConfiguredExpirationMinutes() {
        assertThat(jwtService.getExpirationMinutes()).isEqualTo(30L);
    }
}
