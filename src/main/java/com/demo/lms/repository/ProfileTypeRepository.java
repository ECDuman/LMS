package com.demo.lms.repository;

import com.demo.lms.model.ProfileType;
import com.demo.lms.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileTypeRepository extends JpaRepository<ProfileType, Integer> {
    Optional<ProfileType> findByName(RoleEnum name);
}
