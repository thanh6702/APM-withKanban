package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.ReleaseRequest;
import com.example.sep492_be.dto.request.ReleaseSearchRequest;
import com.example.sep492_be.dto.response.ReleaseResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.enums.ReleaseStatus;

import java.util.UUID;

public interface ReleaseService {
    void create(ReleaseRequest request);
    void update(UUID id, ReleaseRequest request);
    void delete(UUID id);
    void updateStatus(UUID id, ReleaseStatus status);
    PageResponse<ReleaseResponse> findByReleaseRequest(ReleaseSearchRequest request, PageRequestFilter filter);
    ReleaseResponse findById(UUID id);
    void addStoryToRelease(UUID id, UUID storyId);
}
