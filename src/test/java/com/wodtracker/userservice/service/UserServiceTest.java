package com.wodtracker.userservice.service;

import com.wodtracker.userservice.dto.AdminUserRequestDTO;
import com.wodtracker.userservice.dto.AdminUserResponseDTO;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;
import com.wodtracker.userservice.entity.User;
import com.wodtracker.userservice.entity.UserRole;
import com.wodtracker.userservice.exception.CannotDeleteCurrentUserException;
import com.wodtracker.userservice.exception.EmailAlreadyExistsException;
import com.wodtracker.userservice.exception.UserNotFoundException;
import com.wodtracker.userservice.mapper.UserMapper;
import com.wodtracker.userservice.repository.UserRepository;
import com.wodtracker.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        UserRegistrationDTO dto = new UserRegistrationDTO("test@example.com", "password", "Test User");
        User savedUser = new User(1L, "test@example.com", "encoded-password", "Test User", UserRole.USER, null, null);

        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserProfileDTO result = userService.createUser(dto);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getWeight()).isNull();
        assertThat(result.getHeight()).isNull();
        verify(userRepository).existsByEmailIgnoreCase("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldListUsersForAdmin() {
        User admin = new User(1L, "admin@example.com", "password", "Admin", UserRole.ADMIN, null, null);
        User athlete = new User(2L, "user@example.com", "password", "User", UserRole.USER, null, null);
        when(userRepository.findAll()).thenReturn(List.of(admin, athlete));

        List<AdminUserResponseDTO> result = userService.getAllUsersForAdmin();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(result.get(1).getEmail()).isEqualTo("user@example.com");
        verify(userRepository).findAll();
    }

    @Test
    void shouldCreateAdminUserWithSelectedRole() {
        AdminUserRequestDTO dto = new AdminUserRequestDTO("admin@example.com", "password", "Admin User", UserRole.ADMIN);
        User savedUser = new User(3L, "admin@example.com", "encoded-password", "Admin User", UserRole.ADMIN, null, null);

        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AdminUserResponseDTO result = userService.createUserForAdmin(dto);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).existsByEmailIgnoreCase("admin@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        UserRegistrationDTO dto = new UserRegistrationDTO("test@example.com", "password", "Test User");
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already in use: test@example.com");
        verify(userRepository).existsByEmailIgnoreCase("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(2L, "user@example.com", "password", "User", UserRole.USER, null, null);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deleteUser(2L, 1L);

        verify(userRepository).findById(2L);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldRejectDeletingCurrentUser() {
        assertThatThrownBy(() -> userService.deleteUser(1L, 1L))
                .isInstanceOf(CannotDeleteCurrentUserException.class)
                .hasMessage("Administrators cannot delete their own account");

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        User user = new User(1L, "test@example.com", "password", "Test User", UserRole.USER, 70.0, 175.0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserProfileDTO result = userService.getUserById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getWeight()).isEqualTo(70.0);
        assertThat(result.getHeight()).isEqualTo(175.0);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        User existingUser = new User(1L, "test@example.com", "password", "Old Name", UserRole.USER, 60.0, 170.0);
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", null, 75.0, 180.0);
        User updatedUser = new User(1L, "test@example.com", "password", "New Name", UserRole.USER, 75.0, 180.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserProfileDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getWeight()).isEqualTo(75.0);
        assertThat(result.getHeight()).isEqualTo(180.0);
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", null, 75.0, 180.0);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetCurrentUserProfileSuccessfully() {
        User user = new User(1L, "test@example.com", "encoded-password", "Test User", UserRole.USER, 70.0, 175.0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileDTO result = userService.getCurrentUserProfile(1L);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateCurrentUserProfileSuccessfully() {
        User existingUser = new User(1L, "test@example.com", "encoded-password", "Old Name", UserRole.USER, 60.0, 170.0);
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", null, 75.0, 180.0);
        User updatedUser = new User(1L, "test@example.com", "encoded-password", "New Name", UserRole.USER, 75.0, 180.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        UserProfileDTO result = userService.updateCurrentUserProfile(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getWeight()).isEqualTo(75.0);
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldEncodePasswordWhenUpdatingUser() {
        User existingUser = new User(1L, "test@example.com", "old-encoded-password", "Old Name", UserRole.USER, 60.0, 170.0);
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", "newPassword123", 75.0, 180.0);
        User updatedUser = new User(1L, "test@example.com", "new-encoded-password", "New Name", UserRole.USER, 75.0, 180.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("new-encoded-password");
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        UserProfileDTO result = userService.updateUser(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(existingUser);
        assertThat(existingUser.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    void shouldEncodePasswordWhenUpdatingCurrentUserProfile() {
        User existingUser = new User(1L, "test@example.com", "old-encoded-password", "Old Name", UserRole.USER, 60.0, 170.0);
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", "newPassword123", 75.0, 180.0);
        User updatedUser = new User(1L, "test@example.com", "new-encoded-password", "New Name", UserRole.USER, 75.0, 180.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("new-encoded-password");
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        UserProfileDTO result = userService.updateCurrentUserProfile(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(existingUser);
        assertThat(existingUser.getPassword()).isEqualTo("new-encoded-password");
    }
}
