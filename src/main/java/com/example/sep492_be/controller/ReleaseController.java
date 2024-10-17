package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.ReleaseResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.ReleaseService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/release")
@Setter
@Getter
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseService releaseService;

    @GetMapping
    public PageResponse<ReleaseResponse> getList(ReleaseSearchRequest searchRequest, PageRequestFilter filter) {
        return releaseService.findByReleaseRequest(searchRequest, filter);
    }

    @PutMapping("/{id}")
    public ServiceResponse<Void> update(@PathVariable UUID id, @RequestBody ReleaseRequest request) {
        releaseService.update(id, request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/update-status")
    public ServiceResponse<Void> updateStatus(@PathVariable UUID id, @RequestBody ReleaseUpdateStatusRequest request) {
        releaseService.updateStatus(id, request.getStatus() );
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @DeleteMapping("/{id}")
    public ServiceResponse<Void> delete(@PathVariable UUID id) {
        releaseService.delete(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PostMapping
    public ServiceResponse<Void> create(@RequestBody ReleaseRequest request) {
        releaseService.create(request);
        return ServiceResponse.succeed(HttpStatus.CREATED, null);
    }

    @GetMapping("/{id}")
    public ServiceResponse<ReleaseResponse> getbyId(@PathVariable UUID id) {
        return ServiceResponse.succeed(HttpStatus.OK, releaseService.findById(id));

    }

    @PostMapping("/add-story-to-release")
    public ServiceResponse<ReleaseResponse> addStoryToRelease(@RequestBody ReleaseUserStoryRequest request) {
        releaseService.addStoryToRelease(request.getReleaseId(), request.getUserStoryId());
        return ServiceResponse.succeed(HttpStatus.OK, null);

    }



}
