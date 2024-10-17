package com.example.sep492_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseUserStoryRequest {
    private UUID userStoryId;
    private UUID releaseId;
}
