package com.demo.lms.controller;

import com.demo.lms.dto.*;
import com.demo.lms.model.User;
import com.demo.lms.security.JwtUtil;
import com.demo.lms.service.JwtBlacklistService;
import com.demo.lms.service.LoginAttemptService;
import com.demo.lms.service.AuditLoggerService;

import com.demo.lms.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final JwtBlacklistService jwtBlacklistService;
    private final LoginAttemptService loginAttemptService;
    private final AuditLoggerService auditLoggerService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Login attempt limitation control
        if (loginAttemptService.isBlocked(request.getEmail())) {
            auditLoggerService.log("LOGIN_BLOCKED", "User", null);
            throw new RuntimeException("Too many failed attempts. Please try again in 5 minutes.\n");
        }
        try {
            // authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Find the user after authentication
            User user = userService.getUserByEmail(request.getEmail());

            // create Access ve Refresh token
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // Reset attempt counter on successful login
            loginAttemptService.loginSucceeded(request.getEmail());

            auditLoggerService.log("LOGIN", "User", user.getId());

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            // Increment the attempt counter on unsuccessful login
            loginAttemptService.loginFailed(request.getEmail());
            auditLoggerService.log("LOGIN_FAILED", "User", null);
            throw new RuntimeException("Invalid email or password.");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Check the validity of the refresh token
        // The validateToken method in JwtUtil now accepts a String email instead of UserDetails
        String userEmail = jwtUtil.getClaimsFromToken(refreshToken).getSubject();
        if (!jwtUtil.validateToken(refreshToken, userEmail)) {
            throw new RuntimeException("Invalid or expired refresh token.");
        }

        // Get the user email from the refresh token and find the User object
        User user = userService.getUserByEmail(userEmail);

        // Create a new access token
        String newAccessToken = jwtUtil.generateAccessToken(user);

        auditLoggerService.log("TOKEN_REFRESH", "User", user.getId());

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }

    @PreAuthorize("isAuthenticated()") // Only authenticated users can log out
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        String token = request.getToken();
        Claims claims = jwtUtil.getClaimsFromToken(token);
        Date expiration = claims.getExpiration();

        // Blacklist token
        jwtBlacklistService.blacklistToken(token, expiration);

        // Audit logging (get userId from claims, may be null if not in token)
        UUID userId = null;
        if (claims.containsKey("userId")) {
            userId = UUID.fromString(claims.get("userId", String.class));
        }
        auditLoggerService.log("LOGOUT", "User", userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}