package com.demo.lms.mapper;

import com.demo.lms.dto.CourseRequest;
import com.demo.lms.dto.CourseResponse;
import com.demo.lms.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    CourseResponse toResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);

    Course toEntity(CourseRequest courseRequest);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CourseRequest courseRequest, @MappingTarget Course course);
}