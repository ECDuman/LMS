package com.demo.lms.model;

import com.demo.lms.util.BrandCodeGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "code", updatable = false, unique = true)
    private String code;

    // When a brand is deleted, any affiliated organizations are also deleted.
    // The rule in the PDF, "Before deleting a brand, a check must be made for any underlying organizations," can be implemented with a manual check at the service layer.
    // This code provides database-level cleanup.
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Organization> organizations = new ArrayList<>();
}
