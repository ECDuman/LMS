package com.demo.lms.mapper;

import com.demo.lms.dto.StudentClassroomAssignmentRequest;
import com.demo.lms.dto.StudentClassroomAssignmentResponse;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.StudentClassroomAssignment;
import com.demo.lms.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentClassroomAssignmentMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.firstName", target = "studentFirstName")
    @Mapping(source = "student.lastName", target = "studentLastName")
    @Mapping(source = "classroom.id", target = "classroomId")
    @Mapping(source = "classroom.name", target = "classroomName")
    StudentClassroomAssignmentResponse toResponse(StudentClassroomAssignment assignment);

    @Mapping(target = "id", ignore = true)
    StudentClassroomAssignment toEntity(StudentClassroomAssignmentRequest request, User student, Classroom classroom);

    List<StudentClassroomAssignmentResponse> toResponseList(List<StudentClassroomAssignment> assignments);
}