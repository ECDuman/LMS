package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganizationRequest {

    @NotBlank(message = "Name cannot be left blank.")
    private String name;

    @NotNull(message = "Brand ID cannot be left blank.")
    private UUID brandId;
}