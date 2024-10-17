package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintSearchRequest {
    private String name;
    private SprintStatus status;
    private UUID projectId;
    private UUID releaseId;
}
