package com.demo.lms.service;

import com.demo.lms.dto.BrandRequest;
import com.demo.lms.dto.OrganizationRequest;
import com.demo.lms.dto.OrganizationResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.OrganizationMapper;
import com.demo.lms.model.Brand;
import com.demo.lms.model.Organization;
import com.demo.lms.repository.BrandRepository;
import com.demo.lms.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationService Unit Tests")
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private AuditLoggerService auditLoggerService;

    @Mock
    private PermissionCheckerService permissionCheckerService;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private UUID organizationId;
    private UUID brandId;
    private Organization organization;
    private OrganizationRequest organizationRequest;
    private OrganizationResponse organizationResponse;
    private Brand brand;

    @BeforeEach
    void setUp() {
        organizationId = UUID.randomUUID();
        brandId = UUID.randomUUID();

        brand = new Brand();
        brand.setId(brandId);
        brand.setName("Test Brand");

        organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Test Organization");
        organization.setBrand(brand);

        organizationRequest = new OrganizationRequest();
        organizationRequest.setName("Updated Test Organization");
        organizationRequest.setBrandId(brandId);

        organizationResponse = new OrganizationResponse(organizationId, "Test Organization", brandId, "Test Brand");
    }


    @Test
    @DisplayName("All organizations must return successfully")
    void shouldReturnAllOrganizations() {
        when(organizationRepository.findAll()).thenReturn(List.of(organization));
        when(organizationMapper.toResponseList(any())).thenReturn(List.of(organizationResponse));

        List<OrganizationResponse> result = organizationService.getAllOrganizations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Organization", result.get(0).getName());
        verify(organizationRepository).findAll();
        verify(organizationMapper).toResponseList(any());
    }


    @Nested
    @DisplayName("Finding an Organization by ID")
    class GetOrganizationById {
        @Test
        @DisplayName("If ID is present, the organization should return")
        void shouldReturnOrganizationWhenIdExists() {
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationMapper.toResponse(any(Organization.class))).thenReturn(organizationResponse);

            OrganizationResponse result = organizationService.getOrganizationById(organizationId);

            assertNotNull(result);
            assertEquals(organizationId, result.getId());
            verify(organizationRepository).findById(organizationId);
        }

        @Test
        @DisplayName("If the ID does not exist, throw ResourceNotFoundException")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            when(organizationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> organizationService.getOrganizationById(UUID.randomUUID()));
            verify(organizationRepository).findById(any(UUID.class));
            verifyNoInteractions(organizationMapper);
        }
    }


    @Nested
    @DisplayName("Organizasyon Oluşturma")
    class CreateOrganization {
        @Test
        @DisplayName("Yetki varsa organizasyon başarıyla oluşturulmalı")
        void shouldCreateOrganizationSuccessfullyWithPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "CREATE")).thenReturn(true);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(organizationMapper.toEntity(any(OrganizationRequest.class), any(Brand.class))).thenReturn(organization);
            when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
            when(organizationMapper.toResponse(any(Organization.class))).thenReturn(organizationResponse);

            // İşlem
            OrganizationResponse result = organizationService.createOrganization(organizationRequest);

            // Kontrol
            assertNotNull(result);
            assertEquals(organizationId, result.getId());
            verify(permissionCheckerService).hasPermission("Organization", "CREATE");
            verify(brandRepository).findById(brandId);
            verify(organizationRepository).save(any(Organization.class));
            verify(auditLoggerService).log("CREATE", "Organization", organizationId);
        }

        @Test
        @DisplayName("Marka bulunamazsa ResourceNotFoundException fırlatmalı")
        void shouldThrowExceptionWhenBrandNotFound() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "CREATE")).thenReturn(true);
            when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // İşlem & Kontrol
            assertThrows(ResourceNotFoundException.class, () -> organizationService.createOrganization(organizationRequest));
            verify(permissionCheckerService).hasPermission("Organization", "CREATE");
            verify(brandRepository).findById(any(UUID.class));
            verifyNoInteractions(organizationMapper, organizationRepository, auditLoggerService);
        }

        @Test
        @DisplayName("Yetki yoksa AccessDeniedException fırlatmalı")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "CREATE")).thenReturn(false);

            // İşlem & Kontrol
            assertThrows(AccessDeniedException.class, () -> organizationService.createOrganization(organizationRequest));
            verify(permissionCheckerService).hasPermission("Organization", "CREATE");
            verifyNoInteractions(brandRepository, organizationRepository, organizationMapper, auditLoggerService);
        }
    }


    @Nested
    @DisplayName("Organizasyon Güncelleme")
    class UpdateOrganization {
        @Test
        @DisplayName("Yetki varsa organizasyon başarıyla güncellenmeli")
        void shouldUpdateOrganizationSuccessfullyWithPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "UPDATE")).thenReturn(true);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
            when(organizationMapper.toResponse(any(Organization.class))).thenReturn(organizationResponse);

            // İşlem
            OrganizationResponse result = organizationService.updateOrganization(organizationId, organizationRequest);

            // Kontrol
            assertNotNull(result);
            assertEquals(organizationId, result.getId());
            verify(permissionCheckerService).hasPermission("Organization", "UPDATE");
            verify(organizationRepository).findById(organizationId);
            verify(organizationMapper).updateEntity(organizationRequest, organization);
            verify(organizationRepository).save(any(Organization.class));
            verify(auditLoggerService).log("UPDATE", "Organization", organizationId);
        }

        @Test
        @DisplayName("Organizasyon bulunamazsa ResourceNotFoundException fırlatmalı")
        void shouldThrowResourceNotFoundExceptionIfOrganizationNotFound() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "UPDATE")).thenReturn(true);
            when(organizationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // İşlem & Kontrol
            assertThrows(ResourceNotFoundException.class, () -> organizationService.updateOrganization(UUID.randomUUID(), organizationRequest));
            verify(permissionCheckerService).hasPermission("Organization", "UPDATE");
            verify(organizationRepository).findById(any(UUID.class));
            verifyNoInteractions(organizationMapper, auditLoggerService);
        }

        @Test
        @DisplayName("Yetki yoksa AccessDeniedException fırlatmalı")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "UPDATE")).thenReturn(false);

            // İşlem & Kontrol
            assertThrows(AccessDeniedException.class, () -> organizationService.updateOrganization(organizationId, organizationRequest));
            verify(permissionCheckerService).hasPermission("Organization", "UPDATE");
            verifyNoInteractions(organizationRepository, organizationMapper, auditLoggerService);
        }
    }


    @Nested
    @DisplayName("Organizasyon Silme")
    class DeleteOrganization {
        @Test
        @DisplayName("Yetki varsa organizasyon başarıyla silinmeli")
        void shouldDeleteOrganizationSuccessfullyWithPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "DELETE")).thenReturn(true);
            when(organizationRepository.existsById(organizationId)).thenReturn(true);

            // İşlem
            organizationService.deleteOrganization(organizationId);

            // Kontrol
            verify(permissionCheckerService).hasPermission("Organization", "DELETE");
            verify(organizationRepository).existsById(organizationId);
            verify(organizationRepository).deleteById(organizationId);
            verify(auditLoggerService).log("DELETE", "Organization", organizationId);
        }

        @Test
        @DisplayName("Organizasyon mevcut değilse ResourceNotFoundException fırlatmalı")
        void shouldThrowResourceNotFoundExceptionIfOrganizationNotFound() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "DELETE")).thenReturn(true);
            when(organizationRepository.existsById(any(UUID.class))).thenReturn(false);

            // İşlem & Kontrol
            assertThrows(ResourceNotFoundException.class, () -> organizationService.deleteOrganization(UUID.randomUUID()));
            verify(permissionCheckerService).hasPermission("Organization", "DELETE");
            verify(organizationRepository).existsById(any(UUID.class));
            verifyNoInteractions(auditLoggerService);
        }

        @Test
        @DisplayName("Yetki yoksa AccessDeniedException fırlatmalı")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Hazırlık
            when(permissionCheckerService.hasPermission("Organization", "DELETE")).thenReturn(false);

            // İşlem & Kontrol
            assertThrows(AccessDeniedException.class, () -> organizationService.deleteOrganization(organizationId));
            verify(permissionCheckerService).hasPermission("Organization", "DELETE");
            verifyNoInteractions(organizationRepository, auditLoggerService);
        }
    }
}