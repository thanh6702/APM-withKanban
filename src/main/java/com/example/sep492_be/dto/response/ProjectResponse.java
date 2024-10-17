package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.ProjectEntity;
import com.example.sep492_be.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String name;
    private String shortedName;
    private String description;
    private Long startDate;
    private Long endDate;
    private String departmentName;
    private List<ProjectMemberResponse> members;
    private List<ColumnResponse> columnResponses;
    private ProjectStatus status;
    private String statusName;
    private String manager;
    private UUID managerId;
    private Long sprintTime;

    public ProjectResponse(ProjectEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.shortedName = entity.getShortedName();
        this.description = entity.getDescription();
        this.startDate = entity.getStartDate() != null ? entity.getStartDate().toEpochMilli() : null;
        this.endDate = entity.getEndDate() != null ? entity.getEndDate().toEpochMilli() : null ;
        this.departmentName = entity.getDepartmentEntity() != null ? entity.getDepartmentEntity().getName() : null;
        this.status = entity.getStatus();
        this.statusName = entity.getStatus() != null ? entity.getStatus().getValue() : null;
        this.manager = entity.getProjectManager() != null ? entity.getProjectManager().getFirstName() + " " + entity.getProjectManager().getLastName() : null;
        this.managerId = entity.getProjectManager() != null ? entity.getProjectManager().getId() : null;
        this.sprintTime = entity.getSprintTime();
    }
}
