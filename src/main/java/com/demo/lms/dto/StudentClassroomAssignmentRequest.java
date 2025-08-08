package com.demo.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class StudentClassroomAssignmentRequest {
    @NotNull(message = "Student ID cannot be left blank.")
    private UUID studentId;

    @NotNull(message = "Class ID cannot be left blank.")
    private UUID classroomId;
}