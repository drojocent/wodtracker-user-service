package com.wodtracker.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.request.LoginRequestDTO;
import com.wodtracker.userservice.dto.response.UserProfileDTO;
import com.wodtracker.userservice.security.UserPrincipal;
import com.wodtracker.userservice.service.JwtService;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password123");
        UserProfileDTO profileDTO = new UserProfileDTO(1L, "test@example.com", "Test User", 70.0, 175.0);
        UserPrincipal authenticatedUser = new UserPrincipal(
                1L,
                "test@example.com",
                "encoded-password",
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any())).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated(authenticatedUser, null, authenticatedUser.getAuthorities())
        );
        when(userService.getCurrentUserProfile(1L)).thenReturn(profileDTO);
        when(jwtService.generateToken(authenticatedUser)).thenReturn("jwt-token");
        when(jwtService.getExpirationMinutes()).thenReturn(60L);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInMinutes").value(60))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password123");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("No autorizado"))
                .andExpect(jsonPath("$.message").value("Credenciales no válidas"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginValidationFails() throws Exception {
        LoginRequestDTO invalidRequest = new LoginRequestDTO("", "");

                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solicitud no válida"))
                .andExpect(jsonPath("$.validationErrors.email").value("El correo electronico es obligatorio"))
                .andExpect(jsonPath("$.validationErrors.password").value("La contraseña es obligatoria"));
    }
}
