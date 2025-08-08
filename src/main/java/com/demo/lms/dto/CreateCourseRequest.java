package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
