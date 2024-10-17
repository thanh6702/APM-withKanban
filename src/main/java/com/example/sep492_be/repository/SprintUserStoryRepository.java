package com.example.sep492_be.repository;

import com.example.sep492_be.entity.SprintUserStoryEntity;
import com.example.sep492_be.entity.UserStoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SprintUserStoryRepository extends JpaRepository<SprintUserStoryEntity, UUID> {
    @Query("select distinct  x from SprintUserStoryEntity  x" +
            " inner join x.sprint x1" +
            " where x1.id = :sprintId")
    List<SprintUserStoryEntity> getList(UUID sprintId);
    @Query("select distinct  x2 from SprintUserStoryEntity  x" +
            " inner join x.sprint x1" +
            " inner join x.userStory x2" +
            " where x1.id = :sprintId")
    List<UserStoryEntity> getListUserStory(UUID sprintId);
    @Query("select distinct  x from SprintUserStoryEntity  x" +
            " inner join x.sprint x1" +
            " where x1.id in :sprintId")
    List<SprintUserStoryEntity> getListByListSprintIds(List<UUID> sprintId);
    @Query("select distinct  x2 from SprintUserStoryEntity  x" +
            " inner join x.userStory x2" +
            " inner join x.sprint x1" +
            " where x1.id in :sprintId")
    List<UserStoryEntity> getListUserStoryByListSprintIds(List<UUID> sprintId);
    @Modifying
    @Query(" delete  from SprintUserStoryEntity x  where x.userStory.id = :userStoryId ")
    void deleteByUserStoryId(UUID userStoryId);

    @Modifying
    @Query(" delete  from SprintUserStoryEntity x  where x.sprintId = :sprintId ")
    void deleteBySprintId(UUID sprintId);
}
