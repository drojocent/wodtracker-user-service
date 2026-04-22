package com.wodtracker.userservice.dto;

import com.wodtracker.userservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
