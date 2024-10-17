package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.ReleaseRequest;
import com.example.sep492_be.enums.ReleaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "release_entity")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class ReleaseEntity extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private Long code;
    @Column(name = "title")
    private String title;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReleaseStatus status;

    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;

    public ReleaseEntity(ReleaseRequest request){
        this.name = request.getName();
        this.description = request.getDescription();
        this.startDate = request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null;
        this.endDate = request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null;
        this.status = ReleaseStatus.New;
    }
}
