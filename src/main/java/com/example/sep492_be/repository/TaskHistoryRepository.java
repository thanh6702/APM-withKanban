package com.example.sep492_be.repository;

import com.example.sep492_be.entity.TaskHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistoryEntity, UUID> {
    @Query("select x from TaskHistoryEntity x where x.taskId = :taskId order by x.createdAt asc")
    List<TaskHistoryEntity> findByTaskId(UUID taskId);
}
