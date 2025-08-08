package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ClassroomRequest {

    @NotBlank(message = "Name cannot be left blank.")
    private String name;

    @NotNull(message = "Organization ID cannot be left blank.")
    private UUID organizationId;
}