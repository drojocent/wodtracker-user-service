package com.wodtracker.userservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String password;

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    private Double weight;

    @DecimalMin(value = "0.0", inclusive = false, message = "Height must be greater than 0")
    private Double height;
}
