package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrganizationRequest {
    @NotBlank(message = "Organization name is required")
    private String name;

    @NotNull(message = "Brand ID is required")
    private UUID brandId;
}
