package com.demo.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignTeacherClassroomRequest {
    @NotNull(message = "Teacher ID is required")
    private UUID teacherId;

    @NotNull(message = "Classroom ID is required")
    private UUID classroomId;
}
