package com.example.sep492_be.repository;

import com.example.sep492_be.dto.request.ReleaseSearchRequest;
import com.example.sep492_be.entity.ReleaseEntity;
import com.example.sep492_be.enums.ReleaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReleaseRepository extends JpaRepository<ReleaseEntity, UUID> {
    @Query("select  x from ReleaseEntity x where x.project.id = :projectId and x.status = :status order by x.createdAt asc")
    List<ReleaseEntity> findByProjectAndStatus(UUID projectId, ReleaseStatus status);

    @Query("select  x from ReleaseEntity x where" +
            "(:#{#request.projectId} is null or  x.project.id = :#{#request.projectId})" +
            " and (:#{#request.name} is null or x.name like  concat('%', :#{#request.name}, '%') or  x.title like  concat('%', :#{#request.name}, '%'))" +
            " and (:#{#request.status} is null or x.status = :#{#request.status})" +
            " and (:#{#request.statuses} is null or x.status in :#{#request.statuses})" +

            " order by x.createdAt asc"
            )
    Page<ReleaseEntity> getListRelease(ReleaseSearchRequest request, Pageable pageable);

    @Query("select max (x.code) from ReleaseEntity x where x.project.id = :projectId ")
    Long getMaxIndex(UUID projectId);
}
