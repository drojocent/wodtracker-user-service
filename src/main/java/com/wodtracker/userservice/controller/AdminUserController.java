package com.wodtracker.userservice.controller;

import com.wodtracker.userservice.dto.request.AdminUserRequestDTO;
import com.wodtracker.userservice.dto.response.AdminUserResponseDTO;
import com.wodtracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List users for administration", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<AdminUserResponseDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsersForAdmin());
    }

    @PostMapping
    @Operation(summary = "Create a user as administrator", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AdminUserResponseDTO> createUser(@Valid @RequestBody AdminUserRequestDTO requestDTO) {
        AdminUserResponseDTO user = userService.createUserForAdmin(requestDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user as administrator", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        userService.deleteUser(id, parseAuthenticatedUserId(principal));
        return ResponseEntity.noContent().build();
    }

    private Long parseAuthenticatedUserId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
