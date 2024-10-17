package com.example.sep492_be.repository;

import com.example.sep492_be.dto.request.SprintSearchRequest;
import com.example.sep492_be.entity.SprintEntity;
import com.example.sep492_be.enums.SprintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SprintRepository extends JpaRepository<SprintEntity, UUID> {
    @Query("select distinct  x from SprintEntity x" +
            " left outer join x.release x1 " +
            " left outer join x1.project x2 " +
            " where " +
            " (:#{#request.name} is null  or x.name like concat('%', :#{#request.name}, '%')) " +
            " and (:#{#request.status} is null  or x.status  = :#{#request.status}) " +
            " and (:#{#request.projectId} is null  or x2.id  = :#{#request.projectId})" +
            " and (:#{#request.releaseId} is null  or x1.id  = :#{#request.releaseId})" +
            " order by x.createdAt asc")
    Page<SprintEntity> searchSprint(SprintSearchRequest request, Pageable pageable);

    @Query("select distinct x from SprintEntity x " +
            " where x.release.project.id = :projectId " +
            " and x.status = 'In_Progress' order by x.createdAt asc ")
    List<SprintEntity> findByProjectId(UUID projectId);

    @Query("select distinct x from SprintEntity x " +
            " where x.release.project.id = :projectId " +
            " and x.status = 'In_Progress' order by x.createdAt asc ")
    List<SprintEntity> findByProjectIdNotInProgress(UUID projectId);

    @Query("select distinct x from SprintEntity x " +
            " where x.release.project.id = :projectId " +
            " order by x.createdAt asc ")
    List<SprintEntity> findByProjectUpdateSprint(UUID projectId);

    @Query("select distinct x from SprintEntity x where x.release.id = :releaseId order by x.createdAt asc")
    List<SprintEntity> findByReleaseId(UUID releaseId);

    @Query("select max(x.endDate) from SprintEntity x where x.release.project.id = :projectId ")
    Instant findByDate(UUID projectId);

    @Query("select max(x.code) from SprintEntity x where x.release.project.id = :projectId")
    Long getMaxIndex(UUID projectId);
}
