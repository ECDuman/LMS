package com.demo.lms.security;

import com.demo.lms.service.PermissionCheckerService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

// This class allows Spring Security to interpret @PreAuthorize("hasPermission(...)") annotations.
// It does not need to be @Component, because it will be defined as a @Bean in the SecurityConfig.
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionCheckerService permissionCheckerService;

    public CustomPermissionEvaluator(PermissionCheckerService permissionCheckerService) {
        this.permissionCheckerService = permissionCheckerService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(targetDomainObject instanceof String)) {
            // Resource name must be String (e.g. "Brand", "Organization")
            return false;
        }
        if (!(permission instanceof String)) {
            // Permission type must be String (e.g. "CREATE", "READ")
            return false;
        }

        String resourceName = (String) targetDomainObject;
        String action = (String) permission;

        // Perform actual permission check using PermissionCheckerService
        return permissionCheckerService.hasPermission(resourceName, action);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
