package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    private UUID id;
    private String name;
    private String shortedName;
    private String description;
    private Long startDate;
    private Long endDate;
    private ProjectStatus status;
    private UUID departmentId;
    private List<ProjectMemberRequest> members;
    private Long sprintTime;
}