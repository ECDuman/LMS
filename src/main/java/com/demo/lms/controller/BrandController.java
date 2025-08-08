package com.demo.lms.controller;

import com.demo.lms.dto.BrandRequest;
import com.demo.lms.dto.BrandResponse;
import com.demo.lms.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('Brand', 'READ')")
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @PreAuthorize("hasPermission('Brand', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable UUID id) {
        BrandResponse brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('Brand', 'CREATE')")
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest brandRequest) {
        BrandResponse createdBrand = brandService.createBrand(brandRequest);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }

    // --- UPDATE Operation ---
    @PreAuthorize("hasPermission('Brand', 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequest brandRequest) {
        BrandResponse updatedBrand = brandService.updateBrand(id, brandRequest);
        return ResponseEntity.ok(updatedBrand);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('Brand', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}