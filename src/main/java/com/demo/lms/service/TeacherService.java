package com.demo.lms.service;

import com.demo.lms.dto.ClassroomResponse;
import com.demo.lms.dto.CourseResponse;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.ClassroomMapper;
import com.demo.lms.mapper.CourseMapper;
import com.demo.lms.mapper.UserMapper;
import com.demo.lms.model.Classroom;
import com.demo.lms.model.Course;
import com.demo.lms.model.RoleEnum;
import com.demo.lms.model.TeacherClassroomAssignment;
import com.demo.lms.model.User;
import com.demo.lms.repository.CourseAssignmentRepository;
import com.demo.lms.repository.TeacherClassroomAssignmentRepository;
import com.demo.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final TeacherClassroomAssignmentRepository teacherClassroomAssignmentRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final ClassroomMapper classroomMapper;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database for email: " + email));
        }
        throw new ResourceNotFoundException("No authenticated user found in security context.");
    }

    @Transactional(readOnly = true)
    public List<ClassroomResponse> getMyAssignedClassrooms() {
        User currentTeacher = getCurrentAuthenticatedUser();

        List<TeacherClassroomAssignment> assignments = teacherClassroomAssignmentRepository.findByTeacherId(currentTeacher.getId());

        return assignments.stream()
                .map(TeacherClassroomAssignment::getClassroom)
                .map(classroomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getStudentsInMyClassrooms() {
        User currentTeacher = getCurrentAuthenticatedUser();

        List<UUID> classroomIds = teacherClassroomAssignmentRepository.findByTeacherId(currentTeacher.getId())
                .stream()
                .map(TeacherClassroomAssignment::getClassroom)
                .map(Classroom::getId)
                .collect(Collectors.toList());

        if (classroomIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userRepository.findAll().stream()
                .filter(user -> user.getClassroom() != null &&
                        classroomIds.contains(user.getClassroom().getId()) &&
                        user.getProfileType() != null &&
                        user.getProfileType().getName().equals(RoleEnum.STUDENT))
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesInMyClassrooms() {
        User currentTeacher = getCurrentAuthenticatedUser();

        List<UUID> classroomIds = teacherClassroomAssignmentRepository.findByTeacherId(currentTeacher.getId())
                .stream()
                .map(TeacherClassroomAssignment::getClassroom)
                .map(Classroom::getId)
                .collect(Collectors.toList());

        if (classroomIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Course> uniqueCourses = courseAssignmentRepository.findByClassroomIdIn(classroomIds).stream()
                .map(assignment -> assignment.getCourse())
                .collect(Collectors.toSet());

        return uniqueCourses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }
}
