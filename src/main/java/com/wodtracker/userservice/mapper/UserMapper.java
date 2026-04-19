package com.wodtracker.userservice.mapper;

import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.entity.User;
import com.wodtracker.userservice.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationDTO registrationDTO, String encodedPassword, String normalizedEmail) {
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(encodedPassword);
        user.setName(registrationDTO.getName().trim());
        user.setRole(UserRole.USER);
        user.setWeight(null);
        user.setHeight(null);
        return user;
    }

    public UserProfileDTO toProfileDTO(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getWeight(),
                user.getHeight()
        );
    }
}
