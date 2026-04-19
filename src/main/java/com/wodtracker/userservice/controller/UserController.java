package com.wodtracker.userservice.controller;

import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;
import com.wodtracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Register a new athlete")
    public ResponseEntity<UserProfileDTO> createUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserProfileDTO user = userService.createUser(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated athlete profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileDTO> getCurrentUser(Principal principal) {
        UserProfileDTO user = userService.getCurrentUserProfile(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update the authenticated athlete profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileDTO> updateCurrentUser(
            Principal principal,
            @Valid @RequestBody UserUpdateDTO updateDTO
    ) {
        UserProfileDTO user = userService.updateCurrentUserProfile(principal.getName(), updateDTO);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user profile by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileDTO> getUser(@PathVariable Long id) {
        UserProfileDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user profile by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserProfileDTO user = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(user);
    }
}
