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
public class TeacherClassroomAssignmentResponse {
    private UUID id;
    private UUID teacherId;
    private String teacherFirstName;
    private String teacherLastName;
    private UUID classroomId;
    private String classroomName;
}