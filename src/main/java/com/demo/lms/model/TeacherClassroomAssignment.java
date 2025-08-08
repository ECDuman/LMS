package com.demo.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher_classroom_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"teacher_id", "classroom_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassroomAssignment extends BaseEntity {

    @ManyToOne(optional = false) // Specifies that the relationship must always have a teacher
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;
}
