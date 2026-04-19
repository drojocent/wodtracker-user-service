package com.wodtracker.userservice.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    Jwt decodeToken(String token);

    long getExpirationMinutes();

    default String buildScope(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }
}
