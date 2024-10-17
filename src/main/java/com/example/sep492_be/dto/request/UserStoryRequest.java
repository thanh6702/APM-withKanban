package com.example.sep492_be.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryRequest {
    private String summary;
    private Long priorityLevel;
    private UUID projectId;
    private String description;
    private String version;
    private Long userStoryPoint;
    private String acceptanceCriteria;
}
