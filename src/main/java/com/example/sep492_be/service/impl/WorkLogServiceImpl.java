package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.WorkLogRequest;
import com.example.sep492_be.dto.response.WorkLogDetailResponse;
import com.example.sep492_be.dto.response.WorkLogResponse;
import com.example.sep492_be.entity.TaskEntity;
import com.example.sep492_be.entity.WorkLogEntity;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.TaskRepository;
import com.example.sep492_be.repository.WorkLogRepository;
import com.example.sep492_be.service.WorkLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void createWorkLog(WorkLogRequest request) {
        WorkLogEntity workLogEntity = new WorkLogEntity(request);
        TaskEntity taskEntity = taskRepository.findById(request.getTaskId()).orElse(null);
        if(request.getStartDate() == null || request.getEndDate() == null || request.getStartDate().equals(request.getEndDate()))
            throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);

        if(request.getStartDate() > request.getEndDate()) throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);
        if(taskEntity != null && taskEntity.getCreatedAt() != null && request.getStartDate() < taskEntity.getCreatedAt().toEpochMilli())
            throw new ResponseException(InvalidInputError.WORKLOG_STARTDATE_INVALID.getMessage(), InvalidInputError.WORKLOG_STARTDATE_INVALID);
        Instant now = Instant.now();
        if(request.getEndDate() != null && request.getEndDate() > now.toEpochMilli())
            throw new ResponseException(InvalidInputError.WORKLOG_ENDDATE_INVALID.getMessage(), InvalidInputError.WORKLOG_ENDDATE_INVALID);
        List<WorkLogEntity> workLogEntities  = workLogRepository.getWorkLogToValidate(request.getTaskId(),
                Instant.ofEpochMilli(request.getStartDate()),
                Instant.ofEpochMilli(request.getEndDate()) );
        if(!workLogEntities.isEmpty())   throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);

        workLogEntity.setTask(taskEntity);
        workLogRepository.save(workLogEntity);
    }

    @Override
    @Transactional
    public void updateWorkLog(UUID id, WorkLogRequest request) {
        WorkLogEntity workLogEntity = workLogRepository.findById(id).orElse(null);
        if (workLogEntity == null) return;
        if(request.getStartDate() == null || request.getEndDate() == null || request.getStartDate().equals(request.getEndDate()))
            throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);
        TaskEntity taskEntity = workLogEntity.getTask();
        if(request.getStartDate() > request.getEndDate()) throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);
        if(taskEntity != null && taskEntity.getCreatedAt() != null && request.getStartDate() < taskEntity.getCreatedAt().toEpochMilli())
            throw new ResponseException(InvalidInputError.WORKLOG_STARTDATE_INVALID.getMessage(), InvalidInputError.WORKLOG_STARTDATE_INVALID);
        Instant now = Instant.now();
        if(request.getEndDate() != null && request.getEndDate() > now.toEpochMilli())
            throw new ResponseException(InvalidInputError.WORKLOG_ENDDATE_INVALID.getMessage(), InvalidInputError.WORKLOG_ENDDATE_INVALID);
        List<WorkLogEntity> workLogEntities  = workLogRepository.getWorkLogToValidateExclude(taskEntity.getId(),
                Instant.ofEpochMilli(request.getStartDate()),
                Instant.ofEpochMilli(request.getEndDate()),
                workLogEntity.getId());
        if(!workLogEntities.isEmpty())   throw new ResponseException(InvalidInputError.WORKLOG_DATE_INVALID.getMessage(), InvalidInputError.WORKLOG_DATE_INVALID);
        workLogEntity.setStartDate(request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null);
        workLogEntity.setEndDate(request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null);
        workLogEntity.setDescription(request.getDescription());
        workLogRepository.save(workLogEntity);
    }

    @Override
    public void deleteWorkLog(UUID id) {
    workLogRepository.deleteById(id);
    }

    @Override
    public WorkLogResponse findWorkLogs(UUID taskId) {
        List<WorkLogEntity> workLogEntities = workLogRepository.findAllByTaskId(taskId);
        List<WorkLogDetailResponse> workLogDetailResponses = workLogEntities.stream().map(WorkLogDetailResponse::new).toList();
        double totalHours = workLogDetailResponses.stream().mapToDouble(WorkLogDetailResponse::getHours).sum();
        totalHours = Math.round((totalHours) * 100.0) / 100.0; // Round to 2 decimal places
        WorkLogResponse workLogResponse = new WorkLogResponse();
        workLogResponse.setTotalHours(totalHours);
        workLogResponse.setWorkLogDetails(workLogDetailResponses);
        return workLogResponse;
    }
}
