package com.demo.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequest {

    @NotBlank(message = "Email cannot be left blank.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Password cannot be left blank.")
    private String password;

    @NotBlank(message = "Name cannot be left blank.")
    private String firstName;

    @NotBlank(message = "Surname cannot be left blank.")
    private String lastName;

    @NotNull(message = "Profile type ID cannot be left blank.")
    private Integer profileType;
}