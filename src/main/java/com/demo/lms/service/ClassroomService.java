package com.demo.lms.service;

import com.demo.lms.dto.ClassroomRequest;
import com.demo.lms.dto.ClassroomResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.ClassroomMapper;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.Organization;
import com.demo.lms.repository.ClassroomRepository;
import com.demo.lms.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final AuditLoggerService auditLoggerService;
    private final PermissionCheckerService permissionCheckerService;
    private final OrganizationRepository organizationRepository;

    // --- READ Operations ---
    public List<ClassroomResponse> getAllClassrooms() {
        return classroomMapper.toResponseList(classroomRepository.findAll());
    }

    public ClassroomResponse getClassroomById(UUID id) {
        return classroomMapper.toResponse(classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id)));
    }

    // --- CREATE Operation ---
    @Transactional
    public ClassroomResponse createClassroom(ClassroomRequest classroomRequest) {
        if (!permissionCheckerService.hasPermission("Classroom", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create a classroom.");
        }

        Organization organization = organizationRepository.findById(classroomRequest.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));

        Classroom classroom = classroomMapper.toEntity(classroomRequest, organization);
        Classroom savedClassroom = classroomRepository.save(classroom);
        auditLoggerService.log("CREATE", "Classroom", savedClassroom.getId());
        return classroomMapper.toResponse(savedClassroom);
    }

    // --- UPDATE Operation ---
    @Transactional
    public ClassroomResponse updateClassroom(UUID id, ClassroomRequest classroomRequest) {
        if (!permissionCheckerService.hasPermission("Classroom", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update a classroom.");
        }
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
        classroomMapper.updateEntity(classroomRequest, existingClassroom);
        Classroom updatedClassroom = classroomRepository.save(existingClassroom);
        auditLoggerService.log("UPDATE", "Classroom", updatedClassroom.getId());
        return classroomMapper.toResponse(updatedClassroom);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteClassroom(UUID id) {
        if (!permissionCheckerService.hasPermission("Classroom", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete a classroom.");
        }
        if (!classroomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
        auditLoggerService.log("DELETE", "Classroom", id);
    }
}