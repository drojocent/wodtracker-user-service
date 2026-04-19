package com.wodtracker.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.security.UserPrincipal;
import com.wodtracker.userservice.service.JwtService;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
        when(userService.getCurrentUserProfile(1L)).thenReturn(profileDTO);

        String token = jwtService.generateToken(
                new UserPrincipal(
                        1L,
                        "athlete@example.com",
                        "encoded-password",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("athlete@example.com"))
                .andExpect(jsonPath("$.name").value("Athlete"));
    }

    @Test
    void shouldRejectAdminEndpointForRegularUserRole() throws Exception {
        String token = jwtService.generateToken(
                new UserPrincipal(
                        1L,
                        "athlete@example.com",
                        "encoded-password",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminEndpointForAdminRole() throws Exception {
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "athlete@example.com", "Athlete", 72.0, 180.0);
        when(userService.getUserById(1L)).thenReturn(profileDTO);

        String token = jwtService.generateToken(
                new UserPrincipal(
                        999L,
                        "admin@example.com",
                        "encoded-password",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("athlete@example.com"));
    }
}
