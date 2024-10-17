package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseSearchRequest {
    private String name;
    private String code;
    private UUID projectId;
    private ReleaseStatus status;
    private List<ReleaseStatus> statuses;

}
