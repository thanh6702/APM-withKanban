package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.SprintResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.SprintService;
import com.example.sep492_be.service.UserStoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sprint")
@Setter
@Getter
@RequiredArgsConstructor
public class SprintController {
    private final SprintService sprintService;
    private final UserStoryService userStoryService;
    @PostMapping("")
    ServiceResponse<Void> create(@RequestBody SprintRequest request){
        sprintService.createSprint(request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @GetMapping("")
    PageResponse<SprintResponse> get(SprintSearchRequest request, PageRequestFilter filter){
        return sprintService.getSprints(request, filter);
    }

    @GetMapping("/{id}")
    ServiceResponse<SprintResponse> getById(@PathVariable UUID id){
        return ServiceResponse.succeed(HttpStatus.OK, sprintService.getDetail(id));
    }

    @PutMapping("/{id}")
    ServiceResponse<Void> update(@PathVariable UUID id,@RequestBody SprintRequest request){
        sprintService.updateSprint(id, request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @DeleteMapping("/{id}")
    ServiceResponse<Void> delete(@PathVariable UUID id){
        sprintService.deleteSprint(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @PostMapping("/add-user-story-to-sprint")
    ServiceResponse<Void> addUserStoryToSprint(@RequestBody SprintUserStoryRequest request){
        sprintService.addStoryToSprint(request.getSprintId(), request.getUserStoryId());
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/update-status")
    ServiceResponse<Void> updateStatus(@PathVariable UUID id, @RequestBody SprintUpdateStatusRequest request){
        sprintService.updateStatusSprint(id, request.getStatus());
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }



}
