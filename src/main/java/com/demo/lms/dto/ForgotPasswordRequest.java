package com.demo.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Email address cannot be left blank.")
    @Email(message = "Please enter a valid email address.")
    private String email;
}
