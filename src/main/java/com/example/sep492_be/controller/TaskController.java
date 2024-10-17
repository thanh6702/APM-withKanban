package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.ColumnResponse;
import com.example.sep492_be.dto.response.CommentResponse;
import com.example.sep492_be.dto.response.TaskHistoryResponse;
import com.example.sep492_be.dto.response.TaskResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.TaskHistoryService;
import com.example.sep492_be.service.TaskService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task")
@Setter
@Getter
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskHistoryService taskHistoryService;

    @PostMapping("")
    ServiceResponse<Void> create(@RequestBody TaskRequest task) {
        taskService.create(task);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PutMapping("/{id}")
    ServiceResponse<Void> update(@PathVariable UUID id, @RequestBody TaskRequest task) {
        taskService.update(id, task);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @GetMapping("")
    PageResponse<TaskResponse> getAll(TaskSearchRequest request, PageRequestFilter filter) {
        return taskService.getTasks(request, filter);
    }

    @GetMapping("/{id}")
    ServiceResponse<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ServiceResponse.succeed(HttpStatus.OK, taskService.getTaskById(id));
    }

    @GetMapping("/{id}/history")
    ServiceResponse<List<TaskHistoryResponse>> getTaskHistory(@PathVariable UUID id) {
        return ServiceResponse.succeed(HttpStatus.OK, taskHistoryService.getListTaskHistory(id));
    }
    @DeleteMapping("/{id}")
    ServiceResponse<TaskResponse> deleteTask(@PathVariable UUID id) {
        taskService.delete(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PostMapping("/{id}/add-comment")
    ServiceResponse<CommentResponse> addComment(@PathVariable UUID id, @RequestBody AddCommentRequest request) {
        return ServiceResponse.succeed(HttpStatus.OK,  taskService.addComment(id, request));
    }
    // id is comment id
    @DeleteMapping("/delete-comment/{id}")
    ServiceResponse<Void> deleteComment(@PathVariable UUID id) {
        taskService.deleteComment(id);
        return ServiceResponse.succeed(HttpStatus.OK,null);

    }
// id is comment id
    @PutMapping("/comment/{id}/update")
    ServiceResponse<Void> updateComment(@PathVariable UUID id, @RequestBody AddCommentRequest request) {
        taskService.updateComment(id, request);
        return ServiceResponse.succeed(HttpStatus.OK,null);

    }

    @GetMapping("/{id}/column")
    ServiceResponse<List<ColumnResponse>> getListColumnResponse(@PathVariable  UUID id){
        return ServiceResponse.succeed(HttpStatus.OK, taskService.getListColumns(id));

    }

    @GetMapping("/column/{id}/task")
    ServiceResponse<List<TaskResponse>> getListTaskResponse(@PathVariable UUID id, @RequestParam(required = false) UUID userId){
        return ServiceResponse.succeed(HttpStatus.OK, taskService.getTaskByColumnId(id, userId));

    }

    @PutMapping("/{id}/update-column")
    ServiceResponse<Void> updateColumn(@PathVariable UUID id, @RequestBody TaskUpdateColumn request){
        taskService.updateTaskStatus(id, request.getColumnId());
        return ServiceResponse.succeed(HttpStatus.OK,null);
    }
}
