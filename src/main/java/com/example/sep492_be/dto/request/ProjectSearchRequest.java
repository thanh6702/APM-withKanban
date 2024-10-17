package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.ProjectStatus;
import com.example.sep492_be.enums.ProjectTab;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSearchRequest {
    private String searchTerm;
    private Long startDate;
    private Long endDate;
    private ProjectTab tab;
    private UUID departmentId;
    private UUID userId;
    private List<ProjectStatus> statuses;

}
