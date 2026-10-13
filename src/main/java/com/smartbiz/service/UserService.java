package com.smartbiz.service;

import com.smartbiz.dto.ChangePasswordDto;
import com.smartbiz.dto.UpdateProfileDto;
import com.smartbiz.dto.UserProfileDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.User;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BusinessRepository businessRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDto getUserProfile(Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String businessName = "";
        Business business = businessRepository.findByOwnerId(user.getId()).orElse(null);
        if (business != null) {
            businessName = business.getName();
        }

        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                businessName,
                user.getRole()
        );
    }

    @Transactional
    public UserProfileDto updateUserProfile(Long ownerId, UpdateProfileDto dto) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String trimmedUsername = dto.getUsername().trim();
        String trimmedEmail = dto.getEmail().trim();

        // Check username uniqueness if changed
        if (!user.getUsername().equalsIgnoreCase(trimmedUsername)) {
            if (userRepository.existsByUsername(trimmedUsername)) {
                throw new IllegalArgumentException("Username '" + trimmedUsername + "' is already taken.");
            }
        }

        // Check email uniqueness if changed
        if (!user.getEmail().equalsIgnoreCase(trimmedEmail)) {
            if (userRepository.existsByEmail(trimmedEmail)) {
                throw new IllegalArgumentException("Email '" + trimmedEmail + "' is already registered.");
            }
        }

        // Validate phone number format if provided
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            String phone = dto.getPhoneNumber().trim();
            if (!phone.matches("^[0-9]{10}$")) {
                throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
            }
        }

        user.setName(dto.getName().trim());
        user.setUsername(trimmedUsername);
        user.setEmail(trimmedEmail);
        user.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : null);

        User updatedUser = userRepository.save(user);

        String businessName = "";
        Business business = businessRepository.findByOwnerId(updatedUser.getId()).orElse(null);
        if (business != null) {
            businessName = business.getName();
        }

        return new UserProfileDto(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(),
                businessName,
                updatedUser.getRole()
        );
    }

    @Transactional
    public void changePassword(Long ownerId, ChangePasswordDto dto) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Current password is required.");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required.");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm new password is required.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
