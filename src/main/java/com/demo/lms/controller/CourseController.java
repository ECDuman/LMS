package com.demo.lms.controller;

import com.demo.lms.dto.CourseRequest;
import com.demo.lms.dto.CourseResponse;
import com.demo.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('Course', 'READ')")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasPermission('Course', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('Course', 'CREATE')")
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse createdCourse = courseService.createCourse(courseRequest);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    // --- UPDATE Operation ---
    @PreAuthorize("hasPermission('Course', 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable UUID id, @Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse updatedCourse = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(updatedCourse);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('Course', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}