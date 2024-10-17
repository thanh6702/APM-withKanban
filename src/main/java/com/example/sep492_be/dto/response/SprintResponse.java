package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.SprintEntity;
import com.example.sep492_be.enums.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintResponse {
    private UUID id;
    private Long code;
    private String name;
    private String description;
    private String goal;
    private Long startDate;
    private Long endDate;
    private SprintStatus status;
    private Long totalPoint;
    private List<UserStoryResponse> userStories;

    public SprintResponse(SprintEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.goal = entity.getGoal();
        this.startDate = entity.getStartDate() != null ? entity.getStartDate().toEpochMilli() : null;
        this.endDate = entity.getEndDate() != null ? entity.getEndDate().toEpochMilli() : null;
        this.status = entity.getStatus();
//        this.totalPoint = 33L;
    }
}
