package com.demo.lms.service;

import com.demo.lms.dto.CourseResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.CourseMapper;
import com.demo.lms.model.User;
import com.demo.lms.repository.CourseAssignmentRepository;
import com.demo.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
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
    public List<CourseResponse> getMyAssignedCourses() {
        User currentStudent = getCurrentAuthenticatedUser();

        List<CourseResponse> myCourses = courseAssignmentRepository.findByUserId(currentStudent.getId()).stream()
                .map(assignment -> assignment.getCourse())
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());

        return myCourses;
    }
}
