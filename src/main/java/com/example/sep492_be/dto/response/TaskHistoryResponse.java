package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.TaskHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryResponse {
    private UUID id;
    private String content;
    private UserResponse user;
    private Long createdAt;
    private UUID userId;
    public TaskHistoryResponse(TaskHistoryEntity taskHistoryEntity) {

        this.id = taskHistoryEntity.getId();
        this.content = taskHistoryEntity.getContent();
        this.user = new UserResponse(taskHistoryEntity.getUser());
        this.createdAt = taskHistoryEntity.getCreatedAt() != null ? taskHistoryEntity.getCreatedAt().toEpochMilli() : null;
    }
}
