package com.wodtracker.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.AdminUserRequestDTO;
import com.wodtracker.userservice.dto.AdminUserResponseDTO;
import com.wodtracker.userservice.entity.UserRole;
import com.wodtracker.userservice.exception.CannotDeleteCurrentUserException;
import com.wodtracker.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListUsersForAdmin() throws Exception {
        when(userService.getAllUsersForAdmin()).thenReturn(List.of(
                new AdminUserResponseDTO(1L, "Admin", "admin@example.com", UserRole.ADMIN),
                new AdminUserResponseDTO(2L, "User", "user@example.com", UserRole.USER)
        ));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Admin"))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void shouldCreateUserForAdmin() throws Exception {
        AdminUserRequestDTO requestDTO = new AdminUserRequestDTO(
                "new@example.com",
                "New User",
                UserRole.USER
        );
        AdminUserResponseDTO responseDTO = new AdminUserResponseDTO(3L, "New User", "new@example.com", UserRole.USER);

        when(userService.createUserForAdmin(any(AdminUserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldDeleteUserForAdmin() throws Exception {
        mockMvc.perform(delete("/admin/users/2")
                        .principal(() -> "1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(2L, 1L);
    }

    @Test
    void shouldRejectDeletingCurrentAdmin() throws Exception {
        doThrow(new CannotDeleteCurrentUserException("Administrators cannot delete their own account"))
                .when(userService).deleteUser(eq(1L), eq(1L));

        mockMvc.perform(delete("/admin/users/1")
                        .principal(() -> "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid user deletion"));
    }
}
