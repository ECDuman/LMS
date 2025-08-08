package com.demo.lms.service;

import com.demo.lms.dto.TeacherClassroomAssignmentRequest;
import com.demo.lms.dto.TeacherClassroomAssignmentResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.TeacherClassroomAssignmentMapper;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.TeacherClassroomAssignment;
import com.demo.lms.model.User;
import com.demo.lms.repository.ClassroomRepository;
import com.demo.lms.repository.TeacherClassroomAssignmentRepository;
import com.demo.lms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherClassroomAssignmentService {
    private final TeacherClassroomAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final TeacherClassroomAssignmentMapper assignmentMapper;
    private final PermissionCheckerService permissionCheckerService;
    private final AuditLoggerService auditLoggerService;

    // READ Operations
    public List<TeacherClassroomAssignmentResponse> getAllAssignments() {
        return assignmentMapper.toResponseList(assignmentRepository.findAll());
    }

    // CREATE Operation
    @Transactional
    public TeacherClassroomAssignmentResponse assignTeacherToClassroom(TeacherClassroomAssignmentRequest request) {
        if (!permissionCheckerService.hasPermission("TeacherClassroomAssignment", "CREATE")) {
            throw new AccessDeniedException("You do not have the authority to appoint teachers.");
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found."));
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found."));

        // Check that the same teacher cannot be assigned to the same class more than once
        if (assignmentRepository.existsByTeacherIdAndClassroomId(teacher.getId(), classroom.getId())) {
            throw new IllegalStateException("This teacher has already been assigned to this class.");
        }

        TeacherClassroomAssignment assignment = new TeacherClassroomAssignment();
        assignment.setTeacher(teacher);
        assignment.setClassroom(classroom);

        TeacherClassroomAssignment savedAssignment = assignmentRepository.save(assignment);
        auditLoggerService.log("CREATE", "TeacherClassroomAssignment", savedAssignment.getId());
        return assignmentMapper.toResponse(savedAssignment);
    }

    // DELETE Operation
    @Transactional
    public void unassignTeacherFromClassroom(UUID assignmentId) {
        if (!permissionCheckerService.hasPermission("TeacherClassroomAssignment", "DELETE")) {
            throw new AccessDeniedException("You do not have the authority to delete a teacher assignment.");
        }

        assignmentRepository.deleteById(assignmentId);
        auditLoggerService.log("DELETE", "TeacherClassroomAssignment", assignmentId);
    }
}