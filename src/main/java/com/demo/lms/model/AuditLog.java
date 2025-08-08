package com.demo.lms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@Table(name = "audit_logs")
@NoArgsConstructor @AllArgsConstructor
public class AuditLog extends BaseEntity {

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // Oluşturulduktan sonra güncellenmemeli
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT

    @Column(nullable = false)
    private String entityType; // User, Classroom, Course etc.

    // The ID of the entity on which the operation was performed (may be nullable, e.g. for login/logout, the entityId may not be present)
    private UUID entityId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String actorEmail;
}