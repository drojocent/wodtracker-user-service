package com.wodtracker.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String accessToken;
    private String tokenType;
    private long expiresInMinutes;
    private UserProfileDTO user;
}
