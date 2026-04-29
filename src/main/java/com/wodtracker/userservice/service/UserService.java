package com.wodtracker.userservice.service;

import com.wodtracker.userservice.dto.request.AdminUserRequestDTO;
import com.wodtracker.userservice.dto.request.UserRegistrationDTO;
import com.wodtracker.userservice.dto.request.UserUpdateDTO;
import com.wodtracker.userservice.dto.response.AdminUserResponseDTO;
import com.wodtracker.userservice.dto.response.UserProfileDTO;

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
