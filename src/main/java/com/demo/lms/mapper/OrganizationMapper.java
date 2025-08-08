package com.demo.lms.mapper;

import com.demo.lms.dto.OrganizationRequest;
import com.demo.lms.dto.OrganizationResponse;
import com.demo.lms.model.Brand;
import com.demo.lms.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy; // NullValuePropertyMappingStrategy için import

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Null handling
public interface OrganizationMapper {

    @Mapping(target = "name", source = "organization.name")
    @Mapping(target = "brandName", source = "organization.brand.name")
    OrganizationResponse toResponse(Organization organization);

    List<OrganizationResponse> toResponseList(List<Organization> organizations);

    @Mapping(target = "name", source = "organizationRequest.name")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "classrooms", ignore = true)
    Organization toEntity(OrganizationRequest organizationRequest, Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "classrooms", ignore = true)
    void updateEntity(OrganizationRequest organizationRequest, @MappingTarget Organization organization);
}
