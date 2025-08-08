package com.demo.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignCourseRequest {
    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Classroom ID is required")
    private UUID classroomId;
}
