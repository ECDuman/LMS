package com.demo.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "Email cannot be left blank.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    private String password;

    @NotBlank(message = "Name cannot be left blank.")
    private String firstName;

    @NotBlank(message = "Surname cannot be left blank.")
    private String lastName;
}
