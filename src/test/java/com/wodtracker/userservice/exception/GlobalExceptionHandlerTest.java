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
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("User not found with id: 1"));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void shouldHandleEmailAlreadyExistsException() throws Exception {
        // Given
        UserRegistrationDTO dto = new UserRegistrationDTO("test@example.com", "password", "Test User");
        when(userService.createUser(any(UserRegistrationDTO.class))).thenThrow(new EmailAlreadyExistsException("Email already in use"));

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"))
                .andExpect(jsonPath("$.message").value("Email already in use"));
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
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.email").value("Email is required"))
                .andExpect(jsonPath("$.validationErrors.password").value("Password is required"))
                .andExpect(jsonPath("$.validationErrors.name").value("Name is required"));
    }

    @Test
    void shouldHandleGeneralException() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
                mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
