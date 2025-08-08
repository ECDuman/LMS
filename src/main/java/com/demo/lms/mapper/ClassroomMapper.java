package com.demo.lms.mapper;

import com.demo.lms.dto.ClassroomRequest;
import com.demo.lms.dto.ClassroomResponse;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassroomMapper {

    @Mapping(target = "name", source = "classroom.name")
    @Mapping(target = "organizationName", source = "classroom.organization.name")
    ClassroomResponse toResponse(Classroom classroom);

    List<ClassroomResponse> toResponseList(List<Classroom> classrooms);

    @Mapping(target = "name", source = "classroomRequest.name")
    @Mapping(target = "organization", source = "organization")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherAssignments", ignore = true)
    Classroom toEntity(ClassroomRequest classroomRequest, Organization organization);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "teacherAssignments", ignore = true)
    void updateEntity(ClassroomRequest classroomRequest, @MappingTarget Classroom classroom);
}
