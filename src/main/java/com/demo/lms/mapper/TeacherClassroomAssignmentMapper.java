package com.demo.lms.mapper;

import com.demo.lms.dto.TeacherClassroomAssignmentRequest;
import com.demo.lms.dto.TeacherClassroomAssignmentResponse;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.TeacherClassroomAssignment;
import com.demo.lms.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherClassroomAssignmentMapper {

    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.firstName", target = "teacherFirstName")
    @Mapping(source = "teacher.lastName", target = "teacherLastName")
    @Mapping(source = "classroom.id", target = "classroomId")
    @Mapping(source = "classroom.name", target = "classroomName")
    TeacherClassroomAssignmentResponse toResponse(TeacherClassroomAssignment assignment);

    @Mapping(target = "id", ignore = true)
    TeacherClassroomAssignment toEntity(TeacherClassroomAssignmentRequest request, User teacher, Classroom classroom);

    List<TeacherClassroomAssignmentResponse> toResponseList(List<TeacherClassroomAssignment> assignments);
}