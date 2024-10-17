package com.example.sep492_be.repository;

import com.example.sep492_be.dto.request.UserStorySearchRequest;
import com.example.sep492_be.entity.ReleaseUserStoryEntity;
import com.example.sep492_be.entity.SprintUserStoryEntity;
import com.example.sep492_be.entity.UserStoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStoryEntity, UUID> {

    @Query("""
            select distinct x from UserStoryEntity x
            left outer join x.project x1 
            left outer join ReleaseUserStoryEntity x2 on x2.userStory.id = x.id
            where (:#{#request.projectId} is null or x1.id = :#{#request.projectId})
            and (:#{#request.searchTerm} is null or x.summary like concat('%', :#{#request.searchTerm}, '%'))
                and (:#{#request.priorityLevel} is null or x.priorityLevel =  :#{#request.priorityLevel})

            and (coalesce(:#{#request.status}, null ) is null or x.status in :#{#request.status})
            and (coalesce(:releaseId, null ) is null or x2.release.id = :releaseId)
            and x.deleted <> true
            order by x.priorityLevel, x.createdAt
            """)
    Page<UserStoryEntity> getListUserStory(UserStorySearchRequest request, UUID releaseId, Pageable pageable);

    @Query("""
    select distinct x from UserStoryEntity x
    left outer join x.project x1 
    left outer join SprintUserStoryEntity x2 on x2.userStory.id = x.id
    where (:#{#request.projectId} is null or x1.id = :#{#request.projectId})
    and (:#{#request.searchTerm} is null or x.summary like concat('%', :#{#request.searchTerm}, '%'))
    and (coalesce(:#{#request.status}, null ) is null or x.status in :#{#request.status})
    and (coalesce(:#{#request.sprintId}, null ) is null or x2.sprint.id = :#{#request.sprintId})
    and x.deleted <> true
    """)
    Page<UserStoryEntity> getListUserStoryBySprint(UserStorySearchRequest request, Pageable pageable);

    @Query("select distinct sp from UserStoryEntity x " +
            " inner join SprintUserStoryEntity sp on sp.userStoryId = x.id" +
            " inner join SprintEntity  s on s.id = sp.sprintId" +
            " where x.status = 'COMPLETED' and s.id in :sprintIds")
    List<SprintUserStoryEntity> getSprintUserStoryEntitiesBySprint(List<UUID> sprintIds);
    @Query("select distinct x from UserStoryEntity x " +
            " inner join SprintUserStoryEntity sp on sp.userStoryId = x.id" +
            " inner join SprintEntity  s on s.id = sp.sprintId" +
            " where x.status = 'COMPLETED' and s.id in :sprintIds")
    List<UserStoryEntity> getUserStoryEntitiesBySprint(List<UUID> sprintIds);

    @Query("select distinct sp from UserStoryEntity x " +
            " inner join ReleaseUserStoryEntity sp on sp.userStoryId = x.id" +
            " inner join ReleaseEntity  s on s.id = sp.releaseId" +
            " where x.status = 'COMPLETED' and s.id in :releaseIds")
    List<ReleaseUserStoryEntity> getReleaseUserStoryEntitiesByRelease(List<UUID> releaseIds);
    @Query("select distinct x from UserStoryEntity x " +
            " inner join ReleaseUserStoryEntity sp on sp.userStoryId = x.id" +
            " inner join ReleaseEntity  s on s.id = sp.releaseId" +
            " where x.status = 'COMPLETED' and s.id in :releaseIds")
    List<UserStoryEntity> getUserStoryEntitiesByRelease(List<UUID> releaseIds);

    @Query("select x from UserStoryEntity x" +
            " inner join x.project x1 " +
            " where x1.id = :projectId and x.createdAt <= :date" +
            " AND x.status = 'COMPLETED'")
    List<UserStoryEntity> getUserStoryEntitiesBy(UUID projectId, Instant date);

    @Query("select max(u.code) from UserStoryEntity u where u.project.id = :projectId")
    Long getMaxIndexByProjectId(UUID projectId);
}
