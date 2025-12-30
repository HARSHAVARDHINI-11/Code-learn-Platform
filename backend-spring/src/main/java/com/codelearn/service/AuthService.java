package com.codelearn.service;

import com.codelearn.dto.request.LoginRequest;
import com.codelearn.dto.request.RegisterRequest;
import com.codelearn.dto.response.AuthResponse;
import com.codelearn.dto.response.UserResponse;
import com.codelearn.exception.BadRequestException;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.model.User;
import com.codelearn.repository.UserRepository;
import com.codelearn.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new BadRequestException("User already exists");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .college(request.getCollege().trim())
                .department(request.getDepartment())
                .year(request.getYear())
                .codingScore(0)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getId());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Find user
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        log.info("User logged in: {}", user.getEmail());

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getId());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserResponse(user);
    }

    public UserResponse updateProfile(String userId, UserResponse updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getBio() != null) user.setBio(updates.getBio());
        if (updates.getAvatar() != null) user.setAvatar(updates.getAvatar());
        if (updates.getSkills() != null) user.setSkills(updates.getSkills());
        if (updates.getDepartment() != null) user.setDepartment(updates.getDepartment());
        if (updates.getYear() != null) user.setYear(updates.getYear());

        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .college(user.getCollege())
                .department(user.getDepartment())
                .year(user.getYear())
                .codingScore(user.getCodingScore())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .skills(user.getSkills())
                .groups(user.getGroups())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
