package com.demo.lms.service;

import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.model.ProfileType;
import com.demo.lms.model.RoleEnum;
import com.demo.lms.repository.PermissionRepository;
import com.demo.lms.repository.ProfileTypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionCheckerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCheckerService.class);

    private final PermissionRepository permissionRepository;
    private final ProfileTypeRepository profileTypeRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(String resourceName, String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kimlik doğrulama kontrolü
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("Permission check failed: User not authenticated.");
            return false;
        }

        Optional<String> roleNameWithPrefixOptional = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst();

        if (roleNameWithPrefixOptional.isEmpty()) {
            System.out.println("Permission check failed: User has no roles assigned.");
            return false;
        }

        String roleNameWithPrefix = roleNameWithPrefixOptional.get();
        String roleEnumName = roleNameWithPrefix.replace("ROLE_", ""); // "SUPER_ADMIN", "TEACHER", "STUDENT"

        ProfileType profileType;
        try {
            RoleEnum roleEnum = RoleEnum.valueOf(roleEnumName);
            profileType = profileTypeRepository.findByName(roleEnum)
                    .orElseThrow(() -> new ResourceNotFoundException("ProfileType not found for role: " + roleEnumName));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role name: " + roleEnumName + " - Cannot convert to RoleEnum." + e);
            return false;
        }


        // Check all permissions the user has for ProfileType and resourceName
        switch (action.toUpperCase()) {
            case "CREATE":
                return permissionRepository.existsByRoleAndResourceNameAndCanCreate(profileType, resourceName, true);
            case "READ":
                return permissionRepository.existsByRoleAndResourceNameAndCanRead(profileType, resourceName, true);
            case "UPDATE":
                return permissionRepository.existsByRoleAndResourceNameAndCanUpdate(profileType, resourceName, true);
            case "DELETE":
                return permissionRepository.existsByRoleAndResourceNameAndCanDelete(profileType, resourceName, true);
            default:
                LOGGER.warn("Unknown permission action: {}", action);
                return false;
        }
    }
}
