package com.demo.lms.controller;

import com.demo.lms.dto.CourseAssignmentRequest;
import com.demo.lms.dto.CourseAssignmentResponse;
import com.demo.lms.service.CourseAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-assignments")
@RequiredArgsConstructor
public class CourseAssignmentController {
    private final CourseAssignmentService courseAssignmentService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('CourseAssignment', 'READ')")
    @GetMapping
    public ResponseEntity<List<CourseAssignmentResponse>> getAll() {
        List<CourseAssignmentResponse> assignments = courseAssignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('CourseAssignment', 'CREATE')")
    @PostMapping
    public ResponseEntity<CourseAssignmentResponse> create(@Valid @RequestBody CourseAssignmentRequest request) {
        CourseAssignmentResponse assignment = courseAssignmentService.assignUserToCourse(request);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('CourseAssignment', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseAssignmentService.unassignUserFromCourse(id);
        return ResponseEntity.noContent().build();
    }
}