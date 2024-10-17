package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.WorkLogEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogDetailResponse {
    private UUID id;
    private Long date;
    private Double hours;
    private Long startDate;
    private Long endDate;
    private String description;
    public WorkLogDetailResponse(WorkLogEntity entity){
        this.id = entity.getId();
        this.startDate = entity.getStartDate() != null ? entity.getStartDate().toEpochMilli() : null;
        this.endDate = entity.getEndDate() != null ? entity.getEndDate().toEpochMilli() : null;
        this.description = entity.getDescription();
        Instant startDate = entity.getStartDate();
        Instant endDate = entity.getEndDate();

        if (startDate != null && endDate != null) {
            Duration duration = Duration.between(startDate, endDate);
            this.hours = Math.round((duration.toMinutes() / 60.0) * 100.0) / 100.0; // Round to 2 decimal places
        }
        else{
            this.hours = 0.0;
        }
    }
}
