package com.wodtracker.userservice.dto;

import com.wodtracker.userservice.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequestDTO {

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no es válido")
    @Size(max = 255, message = "El correo electronico no puede superar los 255 caracteres")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotNull(message = "El rol es obligatorio")
    private UserRole role;
}
