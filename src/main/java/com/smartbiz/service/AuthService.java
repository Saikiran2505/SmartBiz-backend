package com.smartbiz.service;

import com.smartbiz.dto.LoginRequestDto;
import com.smartbiz.dto.LoginResponseDto;
import com.smartbiz.dto.RegisterRequestDto;
import com.smartbiz.dto.RegisterResponseDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.User;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       BusinessRepository businessRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public RegisterResponseDto registerOwnerAndBusiness(RegisterRequestDto request) {
        // 1. Validation of required fields
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (request.getBusinessName() == null || request.getBusinessName().trim().isEmpty()) {
            throw new IllegalArgumentException("Business name is required.");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm password is required.");
        }

        // 2. Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }

        // 3. Validate phone number format if provided
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            String phone = request.getPhoneNumber().trim();
            if (!phone.matches("^[0-9]{10}$")) {
                throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
            }
        }

        // 4. Validate uniqueness
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        // 4. Hash password securely
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 5. Create and save Owner (User)
        User owner = new User(
            request.getName().trim(),
            request.getUsername().trim(),
            request.getEmail().trim(),
            request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null,
            encodedPassword
        );
        User savedOwner = userRepository.save(owner);

        // 6. Create and save Business linked to Owner
        Business business = new Business(request.getBusinessName().trim(), savedOwner);
        Business savedBusiness = businessRepository.save(business);

        // 7. Return success response
        return new RegisterResponseDto(
            "SUCCESS",
            "Owner and Business registered successfully.",
            savedOwner.getId(),
            savedBusiness.getId(),
            savedOwner.getUsername(),
            savedBusiness.getName()
        );
    }

    public LoginResponseDto login(LoginRequestDto request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        Business business = businessRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Business record not found for user."));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId(), business.getId());

        return new LoginResponseDto(
                "SUCCESS",
                token,
                user.getId(),
                user.getUsername(),
                business.getId(),
                business.getName()
        );
    }
}
