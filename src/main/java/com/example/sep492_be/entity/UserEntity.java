package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.persistence.Column;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted <> true and (is_active is null or is_active = true)")
public class UserEntity extends BaseEntity{
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "shorted_name")
    private String shortedName;
    @Column(name = "dob")
    private Instant dob;
    @Column(name = "is_active")
    private Boolean isActive;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;
    @Column(name = "department_id", insertable = false, updatable = false)
    private UUID department_id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity departmentEntity;
    @JoinColumn(name = "is_director")
    private Boolean isDirector = false;

    @JoinColumn(name = "gender")
    private Boolean gender = true;

    @Column(name = "collaboration_start_date")
    private Instant collaborationStartDate;
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
