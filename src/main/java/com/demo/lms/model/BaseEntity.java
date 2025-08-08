package com.demo.lms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @GenericGenerator(
                name = "UUID",
                strategy = "org.hibernate.id.UUIDGenerator"
        )
        @Column(name = "id", updatable = false, nullable = false)
        private UUID id;
}
