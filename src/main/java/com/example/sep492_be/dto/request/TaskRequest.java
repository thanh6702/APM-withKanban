package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.PriorityLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String name;
    private String description;
    private UUID userId;
    private PriorityLevelEnum priorityLevel;
    private Long startDate;
    private Long endDate;
    private Long estimatedEndDate;
    private UUID userStoryId;
}
