package com.example.sep492_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogResponse {
    private Double totalHours;
    private List<WorkLogDetailResponse> workLogDetails;
}
