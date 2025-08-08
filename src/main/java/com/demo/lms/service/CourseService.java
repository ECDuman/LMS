package com.demo.lms.service;

import com.demo.lms.dto.CourseRequest;
import com.demo.lms.dto.CourseResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.CourseMapper;
import com.demo.lms.model.Course;
import com.demo.lms.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final AuditLoggerService auditLoggerService;
    private final PermissionCheckerService permissionCheckerService;

    // --- READ Operations ---
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toResponseList(courseRepository.findAll());
    }

    public CourseResponse getCourseById(UUID id) {
        return courseMapper.toResponse(courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id)));
    }

    // --- CREATE Operation ---
    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) {
        if (!permissionCheckerService.hasPermission("Course", "CREATE")) {
            throw new AccessDeniedException("You do not have permission to create a course.");
        }
        Course course = courseMapper.toEntity(courseRequest);
        Course savedCourse = courseRepository.save(course);
        auditLoggerService.log("CREATE", "Course", savedCourse.getId());
        return courseMapper.toResponse(savedCourse);
    }

    // --- UPDATE Operation ---
    @Transactional
    public CourseResponse updateCourse(UUID id, CourseRequest courseRequest) {
        if (!permissionCheckerService.hasPermission("Course", "UPDATE")) {
            throw new AccessDeniedException("You do not have permission to update a course.");
        }
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        courseMapper.updateEntity(courseRequest, existingCourse);

        Course updatedCourse = courseRepository.save(existingCourse);
        auditLoggerService.log("UPDATE", "Course", updatedCourse.getId());
        return courseMapper.toResponse(updatedCourse);
    }

    // --- DELETE Operation ---
    @Transactional
    public void deleteCourse(UUID id) {
        if (!permissionCheckerService.hasPermission("Course", "DELETE")) {
            throw new AccessDeniedException("You do not have permission to delete a course.");
        }
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        auditLoggerService.log("DELETE", "Course", id);
    }
}