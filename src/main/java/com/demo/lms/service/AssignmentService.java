package com.demo.lms.service;

import com.demo.lms.dto.AssignmentRequest;
import com.demo.lms.dto.AssignmentResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.AssignmentMapper;
import com.demo.lms.model.Assignment;
import com.demo.lms.repository.AssignmentRepository;
import com.demo.lms.repository.CourseAssignmentRepository;
import com.demo.lms.repository.CourseRepository;
import com.demo.lms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;
    private final PermissionCheckerService permissionCheckerService;
    private final AuditLoggerService auditLoggerService;

    // --- READ Operations ---
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentMapper.toResponseList(assignmentRepository.findAll());
    }

    public AssignmentResponse getAssignmentById(UUID id) {
        return assignmentMapper.toResponse(assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id)));
    }

    // --- CREATE Operation ---
    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        if (!permissionCheckerService.hasPermission("Assignment", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create an assignment.");
        }
        Assignment assignment = assignmentMapper.toEntity(request);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        auditLoggerService.log("CREATE", "Assignment", savedAssignment.getId());
        return assignmentMapper.toResponse(savedAssignment);
    }

    // --- UPDATE Operation ---
    @Transactional
    public AssignmentResponse updateAssignment(UUID id, AssignmentRequest request) {
        if (!permissionCheckerService.hasPermission("Assignment", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update an assignment.");
        }
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignmentMapper.updateEntity(request, existingAssignment);
        Assignment updatedAssignment = assignmentRepository.save(existingAssignment);
        auditLoggerService.log("UPDATE", "Assignment", updatedAssignment.getId());
        return assignmentMapper.toResponse(updatedAssignment);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteAssignment(UUID id) {
        if (!permissionCheckerService.hasPermission("Assignment", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete an assignment.");
        }
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
        auditLoggerService.log("DELETE", "Assignment", id);
    }
}