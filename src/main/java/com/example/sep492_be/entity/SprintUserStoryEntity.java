package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Getter
@Setter
@Table(name = "sprint_user_story")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintUserStoryEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private SprintEntity sprint;

    @ManyToOne
    @JoinColumn(name = "user_story_id")
    private UserStoryEntity userStory;

    @Column(name = "sprint_id", insertable = false, updatable = false)
    private UUID sprintId;

    @Column(name = "user_story_id", insertable = false , updatable = false)
    private UUID userStoryId;

}
