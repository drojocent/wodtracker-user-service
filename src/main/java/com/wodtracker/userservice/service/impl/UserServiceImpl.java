package com.wodtracker.userservice.service.impl;

import com.wodtracker.userservice.dto.UserProfileDTO;
import com.wodtracker.userservice.dto.UserRegistrationDTO;
import com.wodtracker.userservice.dto.UserUpdateDTO;
import com.wodtracker.userservice.entity.User;
import com.wodtracker.userservice.exception.EmailAlreadyExistsException;
import com.wodtracker.userservice.exception.UserNotFoundException;
import com.wodtracker.userservice.mapper.UserMapper;
import com.wodtracker.userservice.repository.UserRepository;
import com.wodtracker.userservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserProfileDTO createUser(UserRegistrationDTO registrationDTO) {
        String normalizedEmail = normalizeEmail(registrationDTO.getEmail());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already in use: " + normalizedEmail);
        }

        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        User user = userMapper.toEntity(registrationDTO, encodedPassword, normalizedEmail);
        User savedUser = userRepository.save(user);
        return userMapper.toProfileDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserById(Long id) {
        return userMapper.toProfileDTO(findUserById(id));
    }

    @Override
    public UserProfileDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = findUserById(id);
        applyUpdates(user, updateDTO);
        return userMapper.toProfileDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String email) {
        return userMapper.toProfileDTO(findUserByEmail(email));
    }

    @Override
    public UserProfileDTO updateCurrentUserProfile(String email, UserUpdateDTO updateDTO) {
        User user = findUserByEmail(email);
        applyUpdates(user, updateDTO);
        return userMapper.toProfileDTO(userRepository.save(user));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    private User findUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + normalizedEmail));
    }

    private void applyUpdates(User user, UserUpdateDTO updateDTO) {
        if (updateDTO.getName() != null && !updateDTO.getName().trim().isEmpty()) {
            user.setName(updateDTO.getName().trim());
        }
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword().trim()));
        }
        if (updateDTO.getWeight() != null) {
            user.setWeight(updateDTO.getWeight());
        }
        if (updateDTO.getHeight() != null) {
            user.setHeight(updateDTO.getHeight());
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
