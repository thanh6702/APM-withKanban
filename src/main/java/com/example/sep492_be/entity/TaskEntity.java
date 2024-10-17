package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.TaskRequest;
import jakarta.persistence.Column;
import com.example.sep492_be.enums.PriorityLevelEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "task")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class TaskEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private Long code;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "assign_id")
    private UserEntity assignTo;
    @Column(name = "assign_id", insertable = false, updatable = false)
    private UUID assignId;
//    @ManyToOne()
//    @JoinColumn(name = "reporter_id")
//    private UserEntity reporter;
    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private PriorityLevelEnum priority;
    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "estimated_end_date")
    private Instant estimatedEndDate;
    @Column(name = "end_date")
    private Instant endDate;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
    @ManyToOne
    @JoinColumn(name = "user_story_id")
    private UserStoryEntity userStory;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private ColumnEntity columnEntity;

    public TaskEntity(TaskRequest taskRequest){
        this.name = taskRequest.getName();
        this.description = taskRequest.getDescription();
        this.setCreatedAt( Instant.now());
        this.priority =taskRequest.getPriorityLevel();
        this.setCreatedAt(Instant.now());
        this.startDate = taskRequest.getStartDate() != null ? Instant.ofEpochMilli(taskRequest.getStartDate()) : null;
//        this.endDate = taskRequest.getEndDate() != null ? Instant.ofEpochMilli(taskRequest.getEndDate()) : null;
        this.estimatedEndDate = taskRequest.getEstimatedEndDate() != null ? Instant.ofEpochMilli(taskRequest.getEstimatedEndDate()) : null;

    }



}
