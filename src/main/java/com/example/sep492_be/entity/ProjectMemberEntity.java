package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Column;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "project_member")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @ManyToOne()
    @JoinColumn(name = "project_id", insertable = false, nullable = false, updatable = false)
    private ProjectEntity projectEntity;
    @ManyToOne()
    @JoinColumn(name = "user_id",  insertable = false, nullable = false, updatable = false)
    private UserEntity userEntity;
    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "assgined_date")
    private Instant assignedDate = Instant.now();

}
