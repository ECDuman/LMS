package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token cannot be left blank.")
    private String token;

    @NotBlank(message = "New password cannot be left blank.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
