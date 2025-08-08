package com.demo.lms.controller;

import com.demo.lms.dto.ClassroomResponse;
import com.demo.lms.dto.CourseResponse;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.service.TeacherService;
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
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final UserService userService;
    private final TeacherService teacherService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('User', 'READ')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllTeachers() {
        List<UserResponse> teachers = userService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @PreAuthorize("hasPermission('User', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getTeacherById(@PathVariable UUID id) {
        UserResponse teacher = userService.getUserById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/my-classes")
    @PreAuthorize("hasPermission('Classroom', 'READ')")
    public ResponseEntity<List<ClassroomResponse>> getMyClasses() {
        List<ClassroomResponse> myClasses = teacherService.getMyAssignedClassrooms();
        return ResponseEntity.ok(myClasses);
    }

    @GetMapping("/my-students")
    @PreAuthorize("hasPermission('User', 'READ')")
    public ResponseEntity<List<UserResponse>> getMyStudents() {
        List<UserResponse> myStudents = teacherService.getStudentsInMyClassrooms();
        return ResponseEntity.ok(myStudents);
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasPermission('Course', 'READ')")
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        List<CourseResponse> myCourses = teacherService.getCoursesInMyClassrooms();
        return ResponseEntity.ok(myCourses);
    }
}
