package com.demo.lms.service;

import com.demo.lms.dto.CourseAssignmentRequest;
import com.demo.lms.dto.CourseAssignmentResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.CourseAssignmentMapper;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.Course;
import com.demo.lms.model.CourseAssignment;
import com.demo.lms.model.User;
import com.demo.lms.repository.ClassroomRepository;
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
public class CourseAssignmentService {
    private final CourseAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseAssignmentMapper assignmentMapper;
    private final PermissionCheckerService permissionCheckerService;
    private final AuditLoggerService auditLoggerService;
    private final ClassroomRepository classroomRepository;

    // --- READ Operations ---
    public List<CourseAssignmentResponse> getAllAssignments() {
        return assignmentMapper.toResponseList(assignmentRepository.findAll());
    }

    // --- CREATE Operation ---
    @Transactional
    public CourseAssignmentResponse assignUserToCourse(CourseAssignmentRequest request) {
        if (!permissionCheckerService.hasPermission("CourseAssignment", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to assign a user to a course.");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with ID: " + request.getClassroomId()));

        // Check that the same user cannot be assigned to the same course more than once.
        if (assignmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new IllegalStateException("This user is already assigned to this course.");
        }
        if (assignmentRepository.existsByUserIdAndCourseIdAndClassroomId(user.getId(), course.getId(), classroom.getId())) {
            throw new IllegalStateException("This user has already been assigned to this class for this course.");
        }

        CourseAssignment assignment = new CourseAssignment();
        assignment.setUser(user);
        assignment.setCourse(course);
        assignment.setClassroom(classroom);

        CourseAssignment savedAssignment = assignmentRepository.save(assignment);
        auditLoggerService.log("CREATE", "CourseAssignment", savedAssignment.getId());
        return assignmentMapper.toResponse(savedAssignment);
    }

    // --- DELETE Operation ---
    @Transactional
    public void unassignUserFromCourse(UUID assignmentId) {
        if (!permissionCheckerService.hasPermission("CourseAssignment", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to unassign a user from a course.");
        }
        assignmentRepository.deleteById(assignmentId);
        auditLoggerService.log("DELETE", "CourseAssignment", assignmentId);
    }
}