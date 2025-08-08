package com.demo.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "classrooms")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Classroom extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    // Assign students to this class (opposite of ManyToOne in User entity)
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> students = new ArrayList<>();

    // Assign teachers to this class (via TeacherClassroomAssignment)
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherClassroomAssignment> teacherAssignments = new ArrayList<>();

    // Assigning courses to this class (via CourseAssignment)
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAssignment> courseAssignments = new ArrayList<>();
}

