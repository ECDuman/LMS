package com.demo.lms.mapper;

import com.demo.lms.dto.CourseAssignmentRequest;
import com.demo.lms.dto.CourseAssignmentResponse;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.Course;
import com.demo.lms.model.CourseAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseAssignmentMapper {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "classroom.id", target = "classroomId")
    @Mapping(source = "classroom.name", target = "classroomName")
    CourseAssignmentResponse toResponse(CourseAssignment courseAssignment);

    @Mapping(target = "id", ignore = true)
    CourseAssignment toEntity(CourseAssignmentRequest request, Course course, Classroom classroom);

    List<CourseAssignmentResponse> toResponseList(List<CourseAssignment> assignments);
}