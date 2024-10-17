package com.example.sep492_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectReportResponse {
    private UUID projectId;
    private Double cycleTime = 0.0;
    private Double leadTime = 0.0;
    private Long totalStoryPointGained = 23L;
}
