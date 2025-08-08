package com.demo.lms.repository;

import com.demo.lms.model.TeacherClassroomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherClassroomAssignmentRepository extends JpaRepository<TeacherClassroomAssignment, UUID> {
    boolean existsByTeacherId(UUID teacherId);
    boolean existsByClassroomId(UUID classroomId);
    boolean existsByTeacherIdAndClassroomId(UUID teacherId, UUID classroomId);
    List<TeacherClassroomAssignment> findByTeacherId(UUID teacherId);
}
