package com.demo.lms.controller;

import com.demo.lms.dto.CourseResponse;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.service.StudentService;
import com.demo.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final UserService userService;
    private final StudentService studentService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('User', 'READ')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllStudents() {
        List<UserResponse> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasPermission('User', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getStudentById(@PathVariable UUID id) {
        UserResponse student = userService.getUserById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasPermission('Course', 'READ')")
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        List<CourseResponse> myCourses = studentService.getMyAssignedCourses();
        return ResponseEntity.ok(myCourses);
    }
}
