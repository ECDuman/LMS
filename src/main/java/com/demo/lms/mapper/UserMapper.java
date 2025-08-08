package com.demo.lms.mapper;

import com.demo.lms.dto.CreateUserRequest;
import com.demo.lms.dto.UpdateUserRequest;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.model.Organization;
import com.demo.lms.model.ProfileType;
import com.demo.lms.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "profileType", source = "profileType")
    @Mapping(target = "organization", source = "organization")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "assignedClassrooms", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    User toEntity(CreateUserRequest userRequest, ProfileType profileType, Organization organization);

    @Mapping(target = "profileTypeName", source = "profileType.name")
    @Mapping(target = "organizationName", source = "organization.name")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileType", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "assignedClassrooms", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    void updateEntity(UpdateUserRequest userRequest, @MappingTarget User user);

    List<UserResponse> toResponseList(List<User> users);
}
