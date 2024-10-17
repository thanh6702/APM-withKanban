package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.WorkLogRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "work_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkLogEntity extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "day")
    private Instant day;

    @Column(name = "hour")
    private Double hours;
    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    public WorkLogEntity(WorkLogRequest request){
        this.id = UUID.randomUUID();
        this.hours = request.getHours();
        this.description = request.getDescription();
        this.startDate  = request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null;
        this.endDate = request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null;
    }
}
