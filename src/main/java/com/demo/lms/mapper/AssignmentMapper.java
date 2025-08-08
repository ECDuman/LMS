package com.demo.lms.mapper;

import com.demo.lms.dto.AssignmentRequest;
import com.demo.lms.dto.AssignmentResponse;
import com.demo.lms.model.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "course.id", source = "courseId")
    Assignment toEntity(AssignmentRequest request);

    AssignmentResponse toResponse(Assignment assignment);

    List<AssignmentResponse> toResponseList(List<Assignment> assignments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "course.id", source = "courseId")
    void updateEntity(AssignmentRequest request, @MappingTarget Assignment assignment);
}