package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.UserStoryRequest;
import com.example.sep492_be.enums.UserStoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_story")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class UserStoryEntity extends BaseEntity  {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "summary", columnDefinition = "LONGTEXT")
    private String summary;

    @Column(name = "priority_level")
    private Long priorityLevel;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStoryStatus status = UserStoryStatus.New;

    @Column(name = "code")
    private Long code;

    @Column(name = "version")
    private String version;

    @Column(name = "user_story_point")
    private Long userStoryPoint;

    @Column(name = "acceptance_criteria")
    private String acceptanceCriteria;

    @Column(name = "completed_at")
    private Instant completedAt;

    public UserStoryEntity(UserStoryRequest request){
        this.summary = request.getSummary();
        this.priorityLevel = request.getPriorityLevel();
        this.description = request.getDescription();
        this.version = request.getVersion();
        this.userStoryPoint = request.getUserStoryPoint();
        this.acceptanceCriteria = request.getAcceptanceCriteria();
    }
}
