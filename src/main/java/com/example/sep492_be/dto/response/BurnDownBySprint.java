package com.example.sep492_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BurnDownBySprint {
    private Long sprintCreatedAt;
    private Long totalPoint;
}
