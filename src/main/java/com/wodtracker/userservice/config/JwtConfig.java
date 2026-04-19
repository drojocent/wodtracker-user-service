package com.wodtracker.userservice.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.SecureRandom;

@Configuration
public class JwtConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtConfig.class);
    private static final int JWT_KEY_SIZE_BYTES = 32;

    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret:}") String secret) {
        byte[] decodedKey = resolveJwtKey(secret);
        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private byte[] resolveJwtKey(String secret) {
        if (secret == null || secret.isBlank()) {
            LOGGER.warn("JWT_SECRET is not configured. Generating an ephemeral JWT key for local runtime only.");
            byte[] generatedKey = new byte[JWT_KEY_SIZE_BYTES];
            new SecureRandom().nextBytes(generatedKey);
            return generatedKey;
        }

        return Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
    }
}
