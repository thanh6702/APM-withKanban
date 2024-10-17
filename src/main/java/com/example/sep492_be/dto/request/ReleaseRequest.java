package com.example.sep492_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseRequest {
    private String title;
    private String description;
    private String name;
    private Long startDate;
    private Long endDate;
    private UUID projectId;

}
