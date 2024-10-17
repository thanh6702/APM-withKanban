package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.TaskEntity;
import com.example.sep492_be.enums.PriorityLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.config.Task;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private UUID id;
    private String name;
    private String description;
    private String status;
    private String priority;
    private Long startDate;
    private Long createdDate;
    private Long code;
    private Long endDate;
    private Long estimatedEndDate;
    private UserResponse userResponse;
    private UUID createdById;
    private UserResponse createdBy;

    private List<CommentResponse> comments;
    public TaskResponse(TaskEntity task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getColumnEntity() != null ? task.getColumnEntity().getName() : null;
        this.createdDate = task.getCreatedAt() != null ? task.getCreatedAt().toEpochMilli() : null;
        this.code = task.getCode();
        this.startDate = task.getStartDate() != null ? task.getStartDate().toEpochMilli() : null;
        this.endDate = task.getEndDate() != null ? task.getEndDate().toEpochMilli() : null;
        this.estimatedEndDate = task.getEstimatedEndDate() != null ? task.getEstimatedEndDate().toEpochMilli() : null;
        this.userResponse = new UserResponse(task.getAssignTo());
        this.createdById = task.getCreatedBy();
        this.priority = task.getPriority() != null ? task.getPriority().getName() : null;
    }
}
