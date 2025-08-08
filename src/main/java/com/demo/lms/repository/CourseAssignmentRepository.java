package com.demo.lms.repository;

import com.demo.lms.model.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, UUID> {
    List<CourseAssignment> findByClassroomIdIn(Collection<UUID> classroomIds);
    List<CourseAssignment> findByUserId(UUID userId);
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
    boolean existsByUserIdAndCourseIdAndClassroomId(UUID userId, UUID courseId, UUID classroomId);
}
