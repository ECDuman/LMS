package com.demo.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Email cannot be left blank.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Password cannot be left blank.")
    @Size(min = 6, message = "The password must be at least 6 characters long.")
    private String password;

    @NotBlank(message = "Name cannot be left blank.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Surname cannot be left blank.")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters.")
    private String lastName;

    @NotNull(message = "Profile type cannot be left blank.")
    private int profileType;

    @NotNull(message = "Organization ID cannot be left blank.")
    private UUID organizationId;
}
