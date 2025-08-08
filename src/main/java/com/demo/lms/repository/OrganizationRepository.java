package com.demo.lms.repository;

import com.demo.lms.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//@Repository
//public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
//    List<Organization> findByBrandId(UUID brandId);
//    boolean existsByBrandId(UUID brandId);
//    boolean existsByNameIgnoreCaseAndBrandId(String name, UUID brandId);
//}

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
}