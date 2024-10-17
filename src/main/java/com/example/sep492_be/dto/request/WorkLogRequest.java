package com.example.sep492_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogRequest {
    private UUID taskId;
    private Double hours;
//    private Long date;
    private String description;
    private Long startDate;
    private Long endDate;
}
