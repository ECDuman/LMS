package com.demo.lms.controller;

import com.demo.lms.dto.StudentClassroomAssignmentRequest;
import com.demo.lms.dto.StudentClassroomAssignmentResponse;
import com.demo.lms.service.StudentClassroomAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student-classroom-assignments")
@RequiredArgsConstructor
public class StudentClassroomAssignmentController {
    private final StudentClassroomAssignmentService assignmentService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('StudentClassroomAssignment', 'READ')")
    @GetMapping
    public ResponseEntity<List<StudentClassroomAssignmentResponse>> getAll() {
        List<StudentClassroomAssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('StudentClassroomAssignment', 'CREATE')")
    @PostMapping
    public ResponseEntity<StudentClassroomAssignmentResponse> create(@Valid @RequestBody StudentClassroomAssignmentRequest request) {
        StudentClassroomAssignmentResponse assignment = assignmentService.assignStudentToClassroom(request);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('StudentClassroomAssignment', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        assignmentService.unassignStudentFromClassroom(id);
        return ResponseEntity.noContent().build();
    }
}