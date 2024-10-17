package com.example.sep492_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserResponse {
    private UUID userId;
    private Double cycleTime = 0.0 ;
    private Double leadTime = 0.0;
}
