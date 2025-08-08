package com.demo.lms.service;

import com.demo.lms.dto.OrganizationResponse;
import com.demo.lms.mapper.OrganizationMapper;
import com.demo.lms.model.Organization;
import com.demo.lms.repository.BrandRepository;
import com.demo.lms.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.demo.lms.dto.OrganizationRequest;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.model.Brand;


@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final AuditLoggerService auditLoggerService;
    private final PermissionCheckerService permissionCheckerService;
    private final BrandRepository brandRepository;

    // --- READ Operations ---
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationMapper.toResponseList(organizationRepository.findAll());
    }

    public OrganizationResponse getOrganizationById(UUID id) {
        return organizationMapper.toResponse(organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id)));
    }

    // --- CREATE Operation ---
    @Transactional
    public OrganizationResponse createOrganization(OrganizationRequest organizationRequest) {
        if (!permissionCheckerService.hasPermission("Organization", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create an organization.");
        }

        Brand brand = brandRepository.findById(organizationRequest.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found."));

        Organization organization = organizationMapper.toEntity(organizationRequest, brand);
        Organization savedOrganization = organizationRepository.save(organization);

        auditLoggerService.log("CREATE", "Organization", savedOrganization.getId());
        return organizationMapper.toResponse(savedOrganization);
    }

    // --- UPDATE Operation ---
    @Transactional
    public OrganizationResponse updateOrganization(UUID id, OrganizationRequest organizationRequest) {
        if (!permissionCheckerService.hasPermission("Organization", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update an organization.");
        }
        Organization existingOrganization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
        organizationMapper.updateEntity(organizationRequest, existingOrganization);
        Organization updatedOrganization = organizationRepository.save(existingOrganization);
        auditLoggerService.log("UPDATE", "Organization", updatedOrganization.getId());
        return organizationMapper.toResponse(updatedOrganization);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteOrganization(UUID id) {
        if (!permissionCheckerService.hasPermission("Organization", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete an organization.");
        }
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
        auditLoggerService.log("DELETE", "Organization", id);
    }
}