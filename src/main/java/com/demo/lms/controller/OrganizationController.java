package com.demo.lms.controller;

import com.demo.lms.dto.OrganizationRequest;
import com.demo.lms.dto.OrganizationResponse;
import com.demo.lms.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('Organization', 'READ')")
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @PreAuthorize("hasPermission('Organization', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable UUID id) {
        OrganizationResponse organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('Organization', 'CREATE')")
    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationRequest organizationRequest) {
        OrganizationResponse createdOrganization = organizationService.createOrganization(organizationRequest);
        return new ResponseEntity<>(createdOrganization, HttpStatus.CREATED);
    }

    // --- UPDATE Operation ---
    @PreAuthorize("hasPermission('Organization', 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable UUID id, @Valid @RequestBody OrganizationRequest organizationRequest) {
        OrganizationResponse updatedOrganization = organizationService.updateOrganization(id, organizationRequest);
        return ResponseEntity.ok(updatedOrganization);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('Organization', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}