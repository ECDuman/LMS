package com.demo.lms.service;

import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.model.ProfileType;
import com.demo.lms.model.RoleEnum;
import com.demo.lms.repository.ProfileTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileTypeService {
    private final ProfileTypeRepository profileTypeRepository;

    public List<ProfileType> getAllProfileTypes() {
        return profileTypeRepository.findAll();
    }

    public ProfileType getProfileTypeById(int id) {
        return profileTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile type not found."));
    }

    public ProfileType getProfileTypeByName(RoleEnum name) {
        return profileTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Profile type not found."));
    }
}