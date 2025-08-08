package com.demo.lms.controller;

import com.demo.lms.dto.ClassroomRequest;
import com.demo.lms.dto.ClassroomResponse;
import com.demo.lms.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomService classroomService;

    // --- READ Operations ---
    @PreAuthorize("hasPermission('Classroom', 'READ')")
    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAllClassrooms() {
        List<ClassroomResponse> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    @PreAuthorize("hasPermission('Classroom', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getClassroomById(@PathVariable UUID id) {
        ClassroomResponse classroom = classroomService.getClassroomById(id);
        return ResponseEntity.ok(classroom);
    }

    // --- CREATE Operation ---
    @PreAuthorize("hasPermission('Classroom', 'CREATE')")
    @PostMapping
    public ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody ClassroomRequest classroomRequest) {
        ClassroomResponse createdClassroom = classroomService.createClassroom(classroomRequest);
        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }

    // --- UPDATE Operation ---
    @PreAuthorize("hasPermission('Classroom', 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponse> updateClassroom(@PathVariable UUID id, @Valid @RequestBody ClassroomRequest classroomRequest) {
        ClassroomResponse updatedClassroom = classroomService.updateClassroom(id, classroomRequest);
        return ResponseEntity.ok(updatedClassroom);
    }

    // --- DELETE Operation ---
    @PreAuthorize("hasPermission('Classroom', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }
}