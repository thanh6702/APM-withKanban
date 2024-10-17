package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.UserStoryEntity;
import com.example.sep492_be.enums.UserStoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryResponse {
    private UUID id;
    private String summary;
    private Long code;
    private String secondClause;
    private Long priorityLevel;
    private ProjectResponse projectResponse;
    private String description;
    private UserStoryStatus status;
    private String version;
    private String acceptanceCriteria;
    private Long userStoryPoint;

    public UserStoryResponse(UserStoryEntity entity){
        this.summary = entity.getSummary();
        this.code = entity.getCode();
        this.priorityLevel = entity.getPriorityLevel();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
        this.projectResponse = entity.getProject() != null ?  new ProjectResponse(entity.getProject()): null;
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.acceptanceCriteria = entity.getAcceptanceCriteria();
        this.userStoryPoint = entity.getUserStoryPoint() != null ? entity.getUserStoryPoint() : 23L;
    }
}
