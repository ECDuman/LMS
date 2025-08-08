package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBrandRequest {
    @NotBlank(message = "Brand name must not be blank")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Brand code is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]{3,20}$", message = "Brand code must be alphanumeric with 3-20 characters")
    private String code;
}
