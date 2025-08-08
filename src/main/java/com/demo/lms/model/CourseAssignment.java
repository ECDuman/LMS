package com.demo.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "classroom_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
}
