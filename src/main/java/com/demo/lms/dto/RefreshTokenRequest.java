package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token cannot be left blank.")
    private String refreshToken;
}