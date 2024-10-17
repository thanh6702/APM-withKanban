package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.UserStoryRequest;
import com.example.sep492_be.dto.request.UserStorySearchRequest;
import com.example.sep492_be.dto.response.BurnDownChartResponse;
import com.example.sep492_be.dto.response.ReleaseResponse;
import com.example.sep492_be.dto.response.SprintResponse;
import com.example.sep492_be.dto.response.UserStoryResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.enums.UserStoryStatus;

import java.util.List;
import java.util.UUID;

public interface UserStoryService {

    void create(UserStoryRequest request);
    void update(UUID id, UserStoryRequest request);
    void delete(UUID id);
    PageResponse<UserStoryResponse> getResponse(UserStorySearchRequest request, PageRequestFilter requestFilter);
    PageResponse<UserStoryResponse> getResponseToAddToSprint(UserStorySearchRequest request, PageRequestFilter requestFilter);
    void deleteFromRelease(UUID id, UserStoryStatus status);

    PageResponse<UserStoryResponse> getResponseBySprint(UserStorySearchRequest request, PageRequestFilter requestFilter);
    void updateStatus(UUID id, UserStoryStatus status);
    void deleteFromSprint(UUID id, UserStoryStatus status);
    void completeUserStory(UUID id);
    void getTotalUserStoryPointForSprint(List<SprintResponse> sprints);
    void getTotalUserStoryPointForRelease(List<ReleaseResponse> releases);
}
