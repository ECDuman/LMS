package com.demo.lms.repository;

import com.demo.lms.model.Permission;
import com.demo.lms.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    List<Permission> findByRole(ProfileType role);

    boolean existsByRoleAndResourceNameAndCanCreate(ProfileType role, String resourceName, boolean canCreate);
    boolean existsByRoleAndResourceNameAndCanRead(ProfileType role, String resourceName, boolean canRead);
    boolean existsByRoleAndResourceNameAndCanUpdate(ProfileType role, String resourceName, boolean canUpdate);
    boolean existsByRoleAndResourceNameAndCanDelete(ProfileType role, String resourceName, boolean canDelete);
}