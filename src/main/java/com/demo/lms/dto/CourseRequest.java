package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {
    @NotBlank(message = "Course Name cannot be left blank.")
    @Size(min = 2, max = 100, message = "Course Name must be between 2 and 100 characters.")
    private String name;

    private String imageUrl;
}