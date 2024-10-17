package com.example.sep492_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BurnDownChartResponse {
    private Long totalPoint;
    private List<BurnDownBySprint> sprints;
}
