package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.SprintRequest;
import com.example.sep492_be.dto.request.SprintSearchRequest;
import com.example.sep492_be.dto.response.SprintResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.enums.SprintStatus;

import java.util.UUID;

public interface SprintService {
    void createSprint(SprintRequest request);
    void updateSprint(UUID id, SprintRequest request);
    void deleteSprint(UUID id);
    void updateStatusSprint(UUID id, SprintStatus status);
    PageResponse<SprintResponse> getSprints(SprintSearchRequest request, PageRequestFilter filter);
    SprintResponse getDetail(UUID id);
    void addStoryToSprint(UUID sprintId, UUID storyId);
}
