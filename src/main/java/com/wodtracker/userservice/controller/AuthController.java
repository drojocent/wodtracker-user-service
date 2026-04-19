package com.wodtracker.userservice.controller;

import com.wodtracker.userservice.dto.LoginRequestDTO;
import com.wodtracker.userservice.dto.LoginResponseDTO;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.security.UserPrincipal;
import com.wodtracker.userservice.service.JwtService;
import com.wodtracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate an athlete with email and password")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(userPrincipal);
        UserProfileDTO user = userService.getCurrentUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", jwtService.getExpirationMinutes(), user));
    }
}
