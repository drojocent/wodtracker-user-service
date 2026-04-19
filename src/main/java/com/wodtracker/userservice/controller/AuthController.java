package com.wodtracker.userservice.controller;

import com.wodtracker.userservice.dto.LoginRequestDTO;
import com.wodtracker.userservice.dto.LoginResponseDTO;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate an athlete with email and password")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        UserProfileDTO user = userService.getCurrentUserProfile(loginRequestDTO.getEmail());
        return ResponseEntity.ok(new LoginResponseDTO("Authentication successful", user));
    }
}
