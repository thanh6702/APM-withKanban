package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "task_history")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private UUID userId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "task_id", nullable = false, insertable = false, updatable = false)
    private UUID taskId;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity taskEntity;

    @Column(name = "created_at")
    private Instant createdAt;
}
