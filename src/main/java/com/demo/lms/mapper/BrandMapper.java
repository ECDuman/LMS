package com.demo.lms.mapper;

import com.demo.lms.dto.BrandRequest;
import com.demo.lms.dto.BrandResponse;
import com.demo.lms.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    BrandResponse toResponse(Brand brand);

    List<BrandResponse> toResponseList(List<Brand> brands);

    Brand toEntity(BrandRequest brandRequest);

    @Mapping(target = "id", ignore = true)
    void updateEntity(BrandRequest brandRequest, @MappingTarget Brand brand);
}