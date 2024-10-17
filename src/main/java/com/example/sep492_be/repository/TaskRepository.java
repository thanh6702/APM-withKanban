package com.example.sep492_be.repository;

import com.example.sep492_be.entity.TaskEntity;
import com.example.sep492_be.entity.WorkLogEntity;
import com.example.sep492_be.enums.PriorityLevelEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("select distinct x from TaskEntity x where" +
            " x.columnEntity.id = :id and (:userId is null or x.assignId = :userId) " +
            "order by x.priority asc, x.startDate desc")
    List<TaskEntity> getTasksByColumnEntityId(UUID id, UUID userId);

    @Query("select distinct x from TaskEntity x " +
            " inner join x.project x1 " +
            " where x1.id = :id and x.createdAt <= :date" +
            " order by x.createdAt")
    List<TaskEntity> getTaskByProjectId(UUID id, Instant date);

    @Query("select distinct x from TaskEntity x where x.assignId = :userId and x.columnEntity.id = :columnId and x.project.id = :projectId")
    List<TaskEntity> getTaskByColumnId(UUID userId, UUID columnId, UUID projectId);
    @Query("select distinct x from TaskEntity x " +
            " left join x.project x1 " +
            " left join x.userStory x4" +
            " left join SprintUserStoryEntity  x5  on x5.userStoryId = x4.id" +
            " left join x5.sprint x6 " +
            " left join ReleaseUserStoryEntity x7 on x7.userStoryId = x4.id " +
            " left join x7.release x8 " +
            " left join x.assignTo x2 " +
            " left join x.columnEntity x3 " +
            " where (:projectId is null  or x1.id = :projectId)" +
            " and (:releaseId is null  or x8.id = :releaseId) " +
            " and (:sprintId is null  or x6.id = :sprintId) " +
            " and (:userStoryId is null  or x4.id = :userStoryId) " +
            " and (:priority is null  or x.priority = :priority) " +
            " and (:startDate is null or x.createdAt > :startDate) " +
            " and (:endDate is null or x.createdAt < :endDate) " +
            " and (:statusId is null or x3.id < :statusId) " +
            " and  (:userId is null or x2.id = :userId or x.createdBy =:userId)" +
            " and (:searchTerm is null or x.name like concat('%', :searchTerm, '%') or cast(x.code as string) = :searchTerm )"
    )
    Page<TaskEntity> getTasksByProjectIdAndUserId(
            String searchTerm,
            UUID projectId,
            UUID userId,
            PriorityLevelEnum priority,
            UUID statusId,
            Instant startDate,
            Instant endDate,
            UUID releaseId,
            UUID sprintId,
            UUID userStoryId,
            Pageable pageable);

    @Query("select max(x.code) from TaskEntity x where x.project.id = :projectId")
    Long getMaxCode(UUID projectId);
}
