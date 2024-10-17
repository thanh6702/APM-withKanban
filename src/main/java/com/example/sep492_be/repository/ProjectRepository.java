package com.example.sep492_be.repository;

import com.example.sep492_be.dto.request.ProjectSearchRequest;
import com.example.sep492_be.entity.ColumnEntity;
import com.example.sep492_be.entity.ProjectEntity;
import com.example.sep492_be.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    @Query("SELECT p FROM ProjectEntity p " +
            "LEFT JOIN ProjectMemberEntity pm ON pm.projectEntity.id = p.id " +
            "WHERE (:searchTerm IS NULL OR p.name LIKE concat('%', :searchTerm, '%')  OR p.shortedName LIKE concat('%', :searchTerm, '%') ) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR p.createdAt BETWEEN :startDate AND :endDate) " +
            "AND (" +
            "       (:tab = 'IS_MINE' AND pm.userEntity.id = :userId) " +
            "       OR (:tab = 'IS_MANAGED' AND (p.projectManager.id  = :userId or pm.userEntity.id = :userId) ) " +
            "       OR (:tab = 'IS_DIRECTOR' AND p.department_id = :departmentId or pm.userEntity.id = :userId or p.projectManager.id  = :userId) " +
            "       OR (:tab = 'IS_ADMIN')" +
            ") " +
            " and (coalesce(:status, null ) is null or p.status in :status)" +
            " order by p.createdAt")
    Page<ProjectEntity> search(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("tab") String tab,
            @Param("userId") UUID userId,
            @Param("departmentId") UUID departmentId,
            List<ProjectStatus> status,
            Pageable pageable);

    @Query("""
    select x from ColumnEntity  x 
    where x.order = (select min(x2.order) from ColumnEntity x2 where x2.project.id = :projectId)
    and x.project.id = :projectId
  
    """)
    ColumnEntity findMinColumn(UUID projectId);

    @Query(" SELECT x from ProjectEntity x where lower(x.shortedName) = :shortedName or lower(x.name) = lower(:name)")
    ProjectEntity findByShortedName(String shortedName, String name);


}
