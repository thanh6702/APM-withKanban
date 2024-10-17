package com.example.sep492_be.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.scheduling.config.Task;

import java.util.UUID;

@Getter
@Setter
@Table(name = "comment")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class Comment extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id = UUID.randomUUID();
    @Column(name = "content",columnDefinition = "LONGTEXT")
    private String content;
    @Column(name = "task_id", insertable = false, updatable = false)
    private UUID taskId;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
//
//    @Column(name = "task_id", insertable = false, updatable  = false)
//    private UUID taskId;


}
