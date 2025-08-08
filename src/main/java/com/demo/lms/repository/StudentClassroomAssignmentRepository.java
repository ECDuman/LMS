package com.demo.lms.repository;

import com.demo.lms.model.StudentClassroomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface StudentClassroomAssignmentRepository extends JpaRepository<StudentClassroomAssignment, UUID> {
    boolean existsByStudentIdAndClassroomId(UUID studentId, UUID classroomId);
}