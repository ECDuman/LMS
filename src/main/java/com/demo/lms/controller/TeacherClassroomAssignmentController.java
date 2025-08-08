package com.demo.lms.controller;

import com.demo.lms.dto.TeacherClassroomAssignmentRequest;
import com.demo.lms.dto.TeacherClassroomAssignmentResponse;
import com.demo.lms.service.TeacherClassroomAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher-classroom-assignments")
@RequiredArgsConstructor
public class TeacherClassroomAssignmentController {
    private final TeacherClassroomAssignmentService assignmentService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('TeacherClassroomAssignment', 'READ')")
    @GetMapping
    public ResponseEntity<List<TeacherClassroomAssignmentResponse>> getAll() {
        List<TeacherClassroomAssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('TeacherClassroomAssignment', 'CREATE')")
    @PostMapping
    public ResponseEntity<TeacherClassroomAssignmentResponse> create(@Valid @RequestBody TeacherClassroomAssignmentRequest request) {
        TeacherClassroomAssignmentResponse assignment = assignmentService.assignTeacherToClassroom(request);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('TeacherClassroomAssignment', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        assignmentService.unassignTeacherFromClassroom(id);
        return ResponseEntity.noContent().build();
    }
}