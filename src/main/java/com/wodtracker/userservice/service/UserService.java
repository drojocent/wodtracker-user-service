package com.wodtracker.userservice.service;

import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;

public interface UserService {

    UserProfileDTO createUser(UserRegistrationDTO registrationDTO);

    UserProfileDTO getUserById(Long id);

    UserProfileDTO updateUser(Long id, UserUpdateDTO updateDTO);

    UserProfileDTO getCurrentUserProfile(String email);

    UserProfileDTO updateCurrentUserProfile(String email, UserUpdateDTO updateDTO);
}
