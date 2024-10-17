package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Table(name = "release_user_story")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseUserStoryEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "release_id")
    private ReleaseEntity release;

    @ManyToOne
    @JoinColumn(name = "user_story_id")
    private UserStoryEntity userStory;

    @Column(name = "user_story_id", insertable = false, updatable = false)
    private UUID userStoryId;
    @Column(name = "release_id", insertable = false, updatable = false)
    private UUID releaseId;
}
