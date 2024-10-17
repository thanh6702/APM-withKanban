package com.example.sep492_be.service;

import com.example.sep492_be.dto.response.TaskHistoryResponse;
import com.example.sep492_be.entity.TaskEntity;
import org.springframework.scheduling.config.Task;

import java.util.List;
import java.util.UUID;

public interface TaskHistoryService {
    void createTaskHistory(String content, TaskEntity taskEntity, UUID userId);
    List<TaskHistoryResponse> getListTaskHistory(UUID taskId);
}
