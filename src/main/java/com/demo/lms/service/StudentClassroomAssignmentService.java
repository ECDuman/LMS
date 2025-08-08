package com.demo.lms.service;

import com.demo.lms.dto.StudentClassroomAssignmentRequest;
import com.demo.lms.dto.StudentClassroomAssignmentResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.StudentClassroomAssignmentMapper;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.StudentClassroomAssignment;
import com.demo.lms.model.User;
import com.demo.lms.repository.ClassroomRepository;
import com.demo.lms.repository.StudentClassroomAssignmentRepository;
import com.demo.lms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentClassroomAssignmentService {
    private final StudentClassroomAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentClassroomAssignmentMapper assignmentMapper;
    private final PermissionCheckerService permissionCheckerService;
    private final AuditLoggerService auditLoggerService;

    // --- READ Operations ---
    public List<StudentClassroomAssignmentResponse> getAllAssignments() {
        return assignmentMapper.toResponseList(assignmentRepository.findAll());
    }

    // --- CREATE Operation ---
    @Transactional
    public StudentClassroomAssignmentResponse assignStudentToClassroom(StudentClassroomAssignmentRequest request) {
        if (!permissionCheckerService.hasPermission("StudentClassroomAssignment", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to assign a student to a classroom.");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found."));


        // Check that the same student cannot be assigned to the same class more than once
        if (assignmentRepository.existsByStudentIdAndClassroomId(student.getId(), classroom.getId())) {
            throw new IllegalStateException("This student is already assigned to this classroom.");
        }

        StudentClassroomAssignment assignment = new StudentClassroomAssignment();
        assignment.setStudent(student);
        assignment.setClassroom(classroom);

        StudentClassroomAssignment savedAssignment = assignmentRepository.save(assignment);
        auditLoggerService.log("CREATE", "StudentClassroomAssignment", savedAssignment.getId());
        return assignmentMapper.toResponse(savedAssignment);
    }

    // --- DELETE Operation ---
    @Transactional
    public void unassignStudentFromClassroom(UUID assignmentId) {
        if (!permissionCheckerService.hasPermission("StudentClassroomAssignment", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to unassign a student from a classroom.");
        }
        assignmentRepository.deleteById(assignmentId);
        auditLoggerService.log("DELETE", "StudentClassroomAssignment", assignmentId);
    }
}