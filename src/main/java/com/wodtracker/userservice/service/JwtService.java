package com.wodtracker.userservice.service;

import com.wodtracker.userservice.security.UserPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface JwtService {

    String generateToken(UserPrincipal userPrincipal);

    Jwt decodeToken(String token);

    long getExpirationMinutes();

    List<String> buildRoles(UserPrincipal userPrincipal);
}
