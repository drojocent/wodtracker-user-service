package com.wodtracker.userservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    private String password;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor que 0")
    private Double weight;

    @DecimalMin(value = "0.0", inclusive = false, message = "La altura debe ser mayor que 0")
    private Double height;
}
