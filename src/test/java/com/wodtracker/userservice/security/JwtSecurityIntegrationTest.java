package com.wodtracker.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.service.JwtService;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldAllowPublicUserRegistrationWithoutJwt() throws Exception {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("test@example.com", "password123", "Test User");
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "test@example.com", "Test User", null, null);

        when(userService.createUser(any(UserRegistrationDTO.class))).thenReturn(profileDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldRejectProtectedEndpointWithoutJwt() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowProtectedEndpointWithValidJwt() throws Exception {
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "athlete@example.com", "Athlete", 72.0, 180.0);
        when(userService.getCurrentUserProfile("athlete@example.com")).thenReturn(profileDTO);

        String token = jwtService.generateToken(
                new User("athlete@example.com", "encoded-password", java.util.List.of())
        );

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("athlete@example.com"))
                .andExpect(jsonPath("$.name").value("Athlete"));
    }
}
