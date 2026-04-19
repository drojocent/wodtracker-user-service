package com.wodtracker.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;
import com.wodtracker.userservice.exception.EmailAlreadyExistsException;
import com.wodtracker.userservice.exception.UserNotFoundException;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("test@example.com", "password", "Test User");
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "test@example.com", "Test User", null, null);

        when(userService.createUser(any(UserRegistrationDTO.class))).thenReturn(profileDTO);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.weight").isEmpty())
                .andExpect(jsonPath("$.height").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given
        UserRegistrationDTO invalidDTO = new UserRegistrationDTO("", "", "");

        // When & Then
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.email").value("Email is required"))
                .andExpect(jsonPath("$.validationErrors.password").value("Password is required"))
                .andExpect(jsonPath("$.validationErrors.name").value("Name is required"));
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "test@example.com", "Test User", 70.0, 175.0);
        when(userService.getUserById(1L)).thenReturn(profileDTO);

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFound() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("User not found"));

        // When & Then
                mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", null, 75.0, 180.0);
        UserProfileDTO updatedProfile = new UserProfileDTO(1L, "test@example.com", "Updated Name", 75.0, 180.0);

        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.height").value(180.0));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", null, 75.0, 180.0);
        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("existing@example.com", "password", "Test User");
        when(userService.createUser(any(UserRegistrationDTO.class))).thenThrow(new EmailAlreadyExistsException("Email already in use"));

        // When & Then
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    @Test
    void shouldGetCurrentUserSuccessfully() throws Exception {
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "test@example.com", "Test User", 70.0, 175.0);
        when(userService.getCurrentUserProfile("test@example.com")).thenReturn(profileDTO);

        mockMvc.perform(get("/users/me")
                        .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void shouldUpdateCurrentUserSuccessfully() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", null, 75.0, 180.0);
        UserProfileDTO updatedProfile = new UserProfileDTO(1L, "test@example.com", "Updated Name", 75.0, 180.0);

        when(userService.updateCurrentUserProfile(eq("test@example.com"), any(UserUpdateDTO.class))).thenReturn(updatedProfile);

        mockMvc.perform(put("/users/me")
                        .principal(() -> "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }
}
