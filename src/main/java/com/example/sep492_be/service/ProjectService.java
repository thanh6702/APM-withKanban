package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    void createProject(ProjectRequest request);
    ProjectResponse getProject(UUID id);
    PageResponse<ProjectResponse> getProjects(ProjectSearchRequest request, PageRequestFilter requestFilter);

    void updateProject(UUID id, ProjectRequest request);
    void updateMember(UUID id, ProjectRequest request);
    void addMember(UUID id, AddMemberRequest request);
    void deleteMember(UUID id);;

    void deleteProject(UUID id);

    void addColumn(UUID projectId, ColumnRequest request);
    void updateColumn(UUID projectId, UUID columnId, ColumnRequest request);
    void deleteColumn(UUID projectId, UUID columnId);

    List<ColumnResponse> getListColumns(UUID projectId);
    ProjectReportResponse getProjectReport(UUID projectId, Long date);
    ProjectReportByDateResponse projectReportByDate(UUID projectId, Long date);
    Long getTotalStoryPoint(UUID projectId, Long date);

    BurnDownChartResponse getResponse(UUID releaseId);
}
