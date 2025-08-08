package com.demo.lms.service;

import com.demo.lms.dto.BrandRequest;
import com.demo.lms.dto.BrandResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.BrandMapper;
import com.demo.lms.model.Brand;
import com.demo.lms.repository.BrandRepository;
import com.demo.lms.util.BrandCodeGenerator;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
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
@DisplayName("BrandService Unit Tests")
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    @Mock
    private AuditLoggerService auditLoggerService;

    @Mock
    private PermissionCheckerService permissionCheckerService;

    @Mock
    private BrandCodeGenerator brandCodeGenerator;

    @InjectMocks
    private BrandService brandService;

    private UUID brandId;
    private Brand brand;
    private BrandRequest brandRequest;
    private BrandResponse brandResponse;
    private final String generatedCode = "TBR";

    @BeforeEach
    void setUp() {
        brandId = UUID.randomUUID();
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("Test Brand");
        brand.setCode(generatedCode);

        brandRequest = new BrandRequest();
        brandRequest.setName("Test Brand");
        brandResponse = new BrandResponse(brandId, "Test Brand", generatedCode);
    }


    @Test
    @DisplayName("Should return all brands")
    void shouldReturnAllBrands() {
        // Given
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(brandMapper.toResponseList(any())).thenReturn(List.of(brandResponse));

        // When
        List<BrandResponse> result = brandService.getAllBrands();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Brand", result.get(0).getName());
        verify(brandRepository).findAll();
        verify(brandMapper).toResponseList(any());
    }


    @Nested
    @DisplayName("Get Brand by ID")
    class GetBrandById {
        @Test
        @DisplayName("Should return a brand when ID exists")
        void shouldReturnBrandWhenIdExists() {
            // Given
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);

            // When
            BrandResponse result = brandService.getBrandById(brandId);

            // Then
            assertNotNull(result);
            assertEquals(brandId, result.getId());
            assertEquals("Test Brand", result.getName());
            verify(brandRepository).findById(brandId);
            verify(brandMapper).toResponse(any(Brand.class));
        }

        @Test
        @DisplayName("Should throw exception when ID does not exist")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            // Given
            when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> brandService.getBrandById(UUID.randomUUID()));
            verify(brandRepository).findById(any(UUID.class));
            verifyNoInteractions(brandMapper);
        }
    }


    @Nested
    @DisplayName("Create Brand")
    class CreateBrand {
        @Test
        @DisplayName("Should create a brand successfully with permission")
        void shouldCreateBrandSuccessfully() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "CREATE")).thenReturn(true);
            when(brandCodeGenerator.generateNextCode()).thenReturn(generatedCode);
            when(brandMapper.toEntity(any(BrandRequest.class))).thenReturn(brand);
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);
            when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);

            // When
            BrandResponse result = brandService.createBrand(brandRequest);

            // Then
            assertNotNull(result);
            assertEquals(brandId, result.getId());
            verify(permissionCheckerService).hasPermission("Brand", "CREATE");
            verify(brandCodeGenerator).generateNextCode(); // Kod üretici çağrılmış mı kontrol et
            verify(brandMapper).toEntity(any(BrandRequest.class));
            verify(brandRepository).save(any(Brand.class));
            verify(auditLoggerService).log("CREATE", "Brand", brandId);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException if no permission")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "CREATE")).thenReturn(false);

            // When & Then
            assertThrows(AccessDeniedException.class, () -> brandService.createBrand(brandRequest));
            verify(permissionCheckerService).hasPermission("Brand", "CREATE");
            verifyNoInteractions(brandRepository, brandMapper, auditLoggerService);
        }
    }

    @Nested
    @DisplayName("Update Brand")
    class UpdateBrand {
        @Test
        @DisplayName("Should update a brand successfully with permission")
        void shouldUpdateBrandSuccessfully() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "UPDATE")).thenReturn(true);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);
            when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);

            // When
            BrandResponse result = brandService.updateBrand(brandId, brandRequest);

            // Then
            assertNotNull(result);
            assertEquals(brandId, result.getId());
            verify(permissionCheckerService).hasPermission("Brand", "UPDATE");
            verify(brandRepository).findById(brandId);
            verify(brandMapper).updateEntity(brandRequest, brand);
            verify(brandRepository).save(any(Brand.class));
            verify(auditLoggerService).log("UPDATE", "Brand", brandId);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException if no permission")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "UPDATE")).thenReturn(false);

            // When & Then
            assertThrows(AccessDeniedException.class, () -> brandService.updateBrand(brandId, brandRequest));
            verify(permissionCheckerService).hasPermission("Brand", "UPDATE");
            verifyNoInteractions(brandRepository, brandMapper, auditLoggerService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if brand not found")
        void shouldThrowResourceNotFoundExceptionIfBrandNotFound() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "UPDATE")).thenReturn(true);
            when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> brandService.updateBrand(UUID.randomUUID(), brandRequest));
            verify(permissionCheckerService).hasPermission("Brand", "UPDATE");
            verify(brandRepository).findById(any(UUID.class));
            verifyNoInteractions(brandMapper, auditLoggerService);
        }
    }

    @Nested
    @DisplayName("Delete Brand")
    class DeleteBrand {
        @Test
        @DisplayName("Should delete a brand successfully with permission")
        void shouldDeleteBrandSuccessfully() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "DELETE")).thenReturn(true);
            when(brandRepository.existsById(brandId)).thenReturn(true);

            // When
            brandService.deleteBrand(brandId);

            // Then
            verify(permissionCheckerService).hasPermission("Brand", "DELETE");
            verify(brandRepository).existsById(brandId);
            verify(brandRepository).deleteById(brandId);
            verify(auditLoggerService).log("DELETE", "Brand", brandId);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException if no permission")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "DELETE")).thenReturn(false);

            // When & Then
            assertThrows(AccessDeniedException.class, () -> brandService.deleteBrand(brandId));
            verify(permissionCheckerService).hasPermission("Brand", "DELETE");
            verifyNoInteractions(brandRepository, brandMapper, auditLoggerService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if brand not found")
        void shouldThrowResourceNotFoundExceptionIfBrandNotFound() {
            // Given
            when(permissionCheckerService.hasPermission("Brand", "DELETE")).thenReturn(true);
            when(brandRepository.existsById(any(UUID.class))).thenReturn(false);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> brandService.deleteBrand(UUID.randomUUID()));
            verify(permissionCheckerService).hasPermission("Brand", "DELETE");
            verify(brandRepository).existsById(any(UUID.class));
            verifyNoInteractions(auditLoggerService);
        }
    }
}