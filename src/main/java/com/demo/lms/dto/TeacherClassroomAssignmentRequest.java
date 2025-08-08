package com.demo.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class TeacherClassroomAssignmentRequest {
    @NotNull(message = "Teacher ID cannot be left blank.")
    private UUID teacherId;

    @NotNull(message = "Class ID cannot be left blank.")
    private UUID classroomId;
}