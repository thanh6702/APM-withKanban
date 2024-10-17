package com.example.sep492_be.repository;

import com.example.sep492_be.entity.WorkLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLogEntity, UUID> {

    @Query("select x from WorkLogEntity x inner join x.task x1 where x1.id = :taskId")
    List<WorkLogEntity> findAllByTaskId(UUID taskId);

    @Query("SELECT x FROM WorkLogEntity x " +
            "INNER JOIN x.task x1 " +
            "WHERE x1.id = :taskId " +
            "AND ((:startDate > x.startDate AND :startDate < x.endDate) " +
            "OR (:endDate > x.startDate AND :endDate < x.endDate) " +
            "OR (:startDate <= x.startDate AND :endDate >= x.endDate))")
    List<WorkLogEntity> getWorkLogToValidate(UUID taskId, Instant startDate, Instant endDate);

    @Query("SELECT x FROM WorkLogEntity x " +
            "INNER JOIN x.task x1 " +
            "WHERE x1.id = :taskId " +
            "AND ((:startDate > x.startDate AND :startDate < x.endDate) " +
            "OR (:endDate > x.startDate AND :endDate < x.endDate) " +
            "OR (:startDate <= x.startDate AND :endDate >= x.endDate))" +
            "and x.id != :id")
    List<WorkLogEntity> getWorkLogToValidateExclude(UUID taskId, Instant startDate, Instant endDate, UUID id);

    @Query("select x from WorkLogEntity x" +
            " inner join x.task x1" +
            " inner join x1.project x2 " +
            " where x2.id = :projectId and x1.createdAt <= :date")
    List<WorkLogEntity> findAllByTaskIdInAndDate(UUID projectId, Instant date);
}
