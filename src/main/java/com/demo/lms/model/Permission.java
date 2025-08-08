package com.demo.lms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"role_id", "resource_name"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER) // EAGER loading may be appropriate because permissions are often accessed frequently.
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileType role;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "can_create")
    private boolean canCreate;

    @Column(name = "can_read")
    private boolean canRead;

    @Column(name = "can_update")
    private boolean canUpdate;

    @Column(name = "can_delete")
    private boolean canDelete;
}
