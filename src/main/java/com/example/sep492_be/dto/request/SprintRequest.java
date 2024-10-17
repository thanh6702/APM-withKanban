package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.SprintStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintRequest {
    private String name;
    private String goal;
    private Long startDate;
    private Long endDate;
    private String description;
    private UUID releaseId;

}
