package com.demo.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignmentResponse {
    private UUID id;
    private UUID courseId;
    private String courseName;
    private UUID classroomId;
    private String classroomName;
}