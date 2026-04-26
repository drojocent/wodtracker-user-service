package com.wodtracker.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no es válido")
    @Size(max = 255, message = "El correo electronico no puede superar los 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
