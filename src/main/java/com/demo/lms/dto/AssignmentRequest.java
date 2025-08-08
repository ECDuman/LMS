package com.demo.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
public class AssignmentRequest {

    @NotNull
    private UUID courseId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotNull
    private LocalDateTime dueDate;
}