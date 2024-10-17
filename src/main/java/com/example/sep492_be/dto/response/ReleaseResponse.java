package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.ReleaseEntity;
import com.example.sep492_be.enums.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseResponse {
    private UUID id;
    private Long code;
    private String title;
    private String description;
    private String name;
    private ReleaseStatus status;
    private Long startDate;
    private Long endDate;
    private BaseResponse project;
    private Long totalPoint;
    private List<UserStoryResponse> userStories;
    public ReleaseResponse(ReleaseEntity entity){
        this.id = entity.getId();
        this.code = entity.getCode();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.name = entity.getName();
        this.startDate = entity.getStartDate() != null ? entity.getStartDate().toEpochMilli() : null;
        this.endDate = entity.getEndDate() != null ? entity.getEndDate().toEpochMilli() : null;
        BaseResponse project = new BaseResponse();
        if(entity.getProject() != null) {
            project.setId(entity.getProject().getId());
            project.setName(entity.getProject().getName());
        }
        this.project = project;
        this.status = entity.getStatus();
        this.totalPoint = 33L;
    }
}
