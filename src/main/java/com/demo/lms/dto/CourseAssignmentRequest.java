package com.demo.lms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CourseAssignmentRequest {

    @NotNull(message = "User ID cannot be left blank..")
    private UUID userId;

    @NotNull(message = "Course ID cannot be left blank.")
    private UUID courseId;

    @NotNull(message = "Classroom ID cannot be left blank.")
    private UUID classroomId;
}