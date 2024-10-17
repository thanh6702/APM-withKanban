package com.example.sep492_be.repository;

import com.example.sep492_be.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.yaml.snakeyaml.events.CommentEvent;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("select x from Comment x where x.task.id = :taskId")
    List<Comment> findAllByTaskId(UUID taskId);

    @Query("select x from Comment x where x.task.id in  :taskIds")
    List<Comment> findAllByListTaskId(List<UUID> taskIds);
}
