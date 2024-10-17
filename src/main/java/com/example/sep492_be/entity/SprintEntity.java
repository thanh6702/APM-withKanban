package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.SprintRequest;
import com.example.sep492_be.enums.SprintStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "sprint")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class SprintEntity extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "name", columnDefinition = "LONGTEXT")
    private String name;
    @Column(name = "code")
    private Long code;
    @Column(name = "goal", columnDefinition = "LONGTEXT")
    private String goal;
    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SprintStatus status;

    @ManyToOne
    @JoinColumn(name = "release_id")
    private ReleaseEntity release;

    public SprintEntity(SprintRequest request){
        this.name = request.getName();
        this.goal = request.getGoal();
        this.description = request.getDescription();
        this.status = SprintStatus.New;
        this.startDate = request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null;
//        this.endDate = request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null;
    }

}
