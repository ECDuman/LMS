package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LogoutRequest {
    @NotBlank(message = "Token cannot be left blank.")
    private String token;
}