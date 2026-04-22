package com.wodtracker.userservice.service;

import com.wodtracker.userservice.dto.AdminUserRequestDTO;
import com.wodtracker.userservice.dto.AdminUserResponseDTO;
import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserProfileDTO createUser(UserRegistrationDTO registrationDTO);

    List<AdminUserResponseDTO> getAllUsersForAdmin();

    AdminUserResponseDTO createUserForAdmin(AdminUserRequestDTO requestDTO);

    void deleteUser(Long id, Long authenticatedUserId);

    UserProfileDTO getUserById(Long id);

    UserProfileDTO updateUser(Long id, UserUpdateDTO updateDTO);

    UserProfileDTO getCurrentUserProfile(Long userId);

    UserProfileDTO updateCurrentUserProfile(Long userId, UserUpdateDTO updateDTO);
}
