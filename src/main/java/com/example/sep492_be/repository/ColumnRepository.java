package com.example.sep492_be.repository;

import com.example.sep492_be.entity.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, UUID> {
    @Query("select c from ColumnEntity  c " +
            "inner join c.project p " +
            " where p.id = :id  ")
    List<ColumnEntity> findByProjectId(UUID id);

    @Query("select max(c.order) from ColumnEntity c where c.project.id = :id")
    Long findMaxOrderById(UUID id);

    @Query("select c from ColumnEntity c where c.order >= :order and c.project.id = :projectId")
    List<ColumnEntity> findColumnOrderGreaterByAndProjectId(Long order, UUID projectId);
}
