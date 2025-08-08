package com.demo.lms.service;

import com.demo.lms.dto.CreateUserRequest;
import com.demo.lms.dto.UpdateUserRequest;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.UserMapper;
import com.demo.lms.model.*;
import com.demo.lms.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLoggerService auditLoggerService;
    private final PermissionCheckerService permissionCheckerService;
    private final ProfileTypeRepository profileTypeRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PermissionRepository permissionRepository;

    // --- READ Operations ---
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }
    public List<UserResponse> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getProfileType() != null && user.getProfileType().getName().equals(RoleEnum.TEACHER))
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
    // NEW METHOD
    public List<UserResponse> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getProfileType() != null && user.getProfileType().getName().equals(RoleEnum.STUDENT))
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
    public UserResponse getUserById(UUID id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Optional<User> getUserByEmailOptional(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with this email address."));

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        tokenRepository.save(resetToken);

        System.out.println("Eposta gönderildi: " + email);

        auditLoggerService.log("FORGOT_PASSWORD_INITIATED", "User", user.getId());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset link."));

        if (resetToken.isExpired()) {
            throw new RuntimeException("The token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        auditLoggerService.log("RESET_PASSWORD", "User", user.getId());

        tokenRepository.delete(resetToken);
    }


    // --- CREATE Operation ---
    @Transactional
    public UserResponse createUser(CreateUserRequest userRequest) {
        if (!permissionCheckerService.hasPermission("User", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create a user.");
        }

        ProfileType profileType = profileTypeRepository.findById(userRequest.getProfileType())
                .orElseThrow(() -> new ResourceNotFoundException("ProfileType not found."));

        Organization organization = organizationRepository.findById(userRequest.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));

        User user = userMapper.toEntity(userRequest, profileType, organization);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);

        List<Permission> defaultPermissions = permissionRepository.findByRole(profileType);
        if (!defaultPermissions.isEmpty()) {
            savedUser.getPermissions().addAll(defaultPermissions);
            userRepository.save(savedUser);
            System.out.println("The permission for the profile type was assigned to the user.");
        } else {
            System.out.println("No default permission found for profile type.");
        }

        auditLoggerService.log("CREATE", "User", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    // --- UPDATE Operation ---
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest userRequest) {
        if (!permissionCheckerService.hasPermission("User", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update a user.");
        }
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userMapper.updateEntity(userRequest, existingUser);
        if (userRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        User updatedUser = userRepository.save(existingUser);
        auditLoggerService.log("UPDATE", "User", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteUser(UUID id) {
        if (!permissionCheckerService.hasPermission("User", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete a user.");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        auditLoggerService.log("DELETE", "User", id);
    }
}