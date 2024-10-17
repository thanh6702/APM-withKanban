package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.response.TaskHistoryResponse;
import com.example.sep492_be.dto.response.TaskResponse;
import com.example.sep492_be.entity.TaskEntity;
import com.example.sep492_be.entity.TaskHistoryEntity;
import com.example.sep492_be.entity.UserEntity;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.TaskHistoryRepository;
import com.example.sep492_be.repository.UserRepository;
import com.example.sep492_be.repository.UserStoryRepository;
import com.example.sep492_be.service.TaskHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskHistoryServiceImpl implements TaskHistoryService {
    private final TaskHistoryRepository taskHistoryRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void createTaskHistory(String content, TaskEntity taskEntity, UUID userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if(optionalUserEntity.isEmpty()) return;
        TaskHistoryEntity taskHistoryEntity = TaskHistoryEntity.builder()
                .content(content)
                .taskEntity(taskEntity)
                .user(optionalUserEntity.get())
                .createdAt(Instant.now())
                .build();
        taskHistoryRepository.save(taskHistoryEntity);
    }

    @Override
    public List<TaskHistoryResponse> getListTaskHistory(UUID taskId) {
        List<TaskHistoryEntity > taskHistoryEntities = taskHistoryRepository.findByTaskId(taskId);
        return taskHistoryEntities.stream().map(TaskHistoryResponse::new).toList();
    }
}
