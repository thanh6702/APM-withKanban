package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.AddCommentRequest;
import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.TaskRequest;
import com.example.sep492_be.dto.request.TaskSearchRequest;
import com.example.sep492_be.dto.response.ColumnResponse;
import com.example.sep492_be.dto.response.CommentResponse;
import com.example.sep492_be.dto.response.TaskResponse;
import com.example.sep492_be.dto.response.util.PageResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    void create(TaskRequest taskEntity);

    PageResponse<TaskResponse> getTasks(TaskSearchRequest request, PageRequestFilter filter);
    void update(UUID id, TaskRequest request);
    void delete(UUID id);
    TaskResponse getTaskById(UUID id);
    CommentResponse addComment(UUID id, AddCommentRequest request);
    void deleteComment(UUID id);
    void updateComment(UUID id, AddCommentRequest request);
    List<ColumnResponse> getListColumns(UUID taskId);

    List<TaskResponse> getTaskByColumnId(UUID columnId, UUID userId);
    void updateTaskStatus(UUID taskId, UUID columnId);
}
