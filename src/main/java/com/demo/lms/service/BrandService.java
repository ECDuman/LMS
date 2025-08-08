package com.demo.lms.service;

import com.demo.lms.model.Brand;
import com.demo.lms.mapper.BrandMapper;
import com.demo.lms.dto.BrandResponse;
import com.demo.lms.repository.BrandRepository;
import com.demo.lms.util.BrandCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import com.demo.lms.dto.BrandRequest;
import com.demo.lms.exception_handling.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final AuditLoggerService auditLoggerService;
    private final PermissionCheckerService permissionCheckerService;
    private final BrandCodeGenerator brandCodeGenerator;

    // --- READ Operations ---
    public List<BrandResponse> getAllBrands() {
        return brandMapper.toResponseList(brandRepository.findAll());
    }

    public BrandResponse getBrandById(UUID id) {
        return brandMapper.toResponse(brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id)));
    }

    // --- CREATE Operation ---
    @Transactional
    public BrandResponse createBrand(BrandRequest brandRequest) {
        if (!permissionCheckerService.hasPermission("Brand", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create a brand.");
        }
        if (brandRepository.existsByNameIgnoreCase(brandRequest.getName())) {
            throw new IllegalStateException("A brand with this name already exists: " + brandRequest.getName());
        }
        Brand brand = brandMapper.toEntity(brandRequest);

        if (brand.getCode() == null || brand.getCode().trim().isEmpty()) {
            String generatedCode = brandCodeGenerator.generateNextCode();
            brand.setCode(generatedCode);
            System.out.println("Automatic code generated for the brand: " + generatedCode);
        } else {
            if (brandRepository.existsByCodeIgnoreCase(brand.getCode())) {
                throw new IllegalStateException("A brand with this code already exists: " + brand.getCode());
            }
            System.out.println("The code from the request was used for the brand: " + brand.getCode());
        }

        Brand savedBrand = brandRepository.save(brand);
        auditLoggerService.log("CREATE", "Brand", savedBrand.getId());
        return brandMapper.toResponse(savedBrand);
    }

    // --- UPDATE Operation ---
    @Transactional
    public BrandResponse updateBrand(UUID id, BrandRequest brandRequest) {
        if (!permissionCheckerService.hasPermission("Brand", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update a brand.");
        }
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brandMapper.updateEntity(brandRequest, existingBrand);

        Brand updatedBrand = brandRepository.save(existingBrand);
        auditLoggerService.log("UPDATE", "Brand", updatedBrand.getId());
        return brandMapper.toResponse(updatedBrand);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteBrand(UUID id) {
        if (!permissionCheckerService.hasPermission("Brand", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete a brand.");
        }
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
        auditLoggerService.log("DELETE", "Brand", id);
    }
}