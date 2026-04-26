package com.wodtracker.userservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.controller.UserController;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandleUserNotFoundException() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("No se ha encontrado el usuario solicitado"));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.message").value("No se ha encontrado el usuario solicitado"));
    }

    @Test
    void shouldHandleEmailAlreadyExistsException() throws Exception {
        // Given
        UserRegistrationDTO dto = new UserRegistrationDTO("test@example.com", "password", "Test User");
        when(userService.createUser(any(UserRegistrationDTO.class))).thenThrow(new EmailAlreadyExistsException("El correo electronico ya está en uso"));

        // When & Then
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflicto"))
                .andExpect(jsonPath("$.message").value("El correo electronico ya está en uso"));
    }

    @Test
    void shouldHandleValidationExceptions() throws Exception {
        // Given
        UserRegistrationDTO invalidDto = new UserRegistrationDTO("", "", "");

        // When & Then
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solicitud no válida"))
                .andExpect(jsonPath("$.message").value("La solicitud contiene datos no válidos"))
                .andExpect(jsonPath("$.validationErrors.email").value("El correo electronico es obligatorio"))
                .andExpect(jsonPath("$.validationErrors.password").value("La contraseña es obligatoria"))
                .andExpect(jsonPath("$.validationErrors.name").value("El nombre es obligatorio"));
    }

    @Test
    void shouldHandleGeneralException() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno del servidor"))
                .andExpect(jsonPath("$.message").value("Se ha producido un error interno"));
    }

    @Test
    void shouldHandleEmailDeliveryException() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO("test@example.com", "password", "Test User");
        when(userService.createUser(any(UserRegistrationDTO.class)))
                .thenThrow(new EmailDeliveryException("No se pudo completar la operación porque no fue posible enviar la notificacion", new RuntimeException("smtp down")));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Servicio no disponible"))
                .andExpect(jsonPath("$.message").value("No se pudo completar la operación porque no fue posible enviar la notificacion"));
    }
}
