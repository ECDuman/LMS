package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequest {
    @NotBlank(message = "Brand name cannot be left blank.")
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters.")
    private String name;
}
