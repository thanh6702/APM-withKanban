package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.UserStoryResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.UserStoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user-story")
@Setter
@Getter
@RequiredArgsConstructor
public class UserStoryController {
    private final UserStoryService userStoryService;
    @PostMapping
    ServiceResponse<Void> createUserStory(@RequestBody UserStoryRequest request){
        userStoryService.create(request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @GetMapping
    PageResponse<UserStoryResponse> getUserStoryList(UserStorySearchRequest request, PageRequestFilter requestFilter){
        return userStoryService.getResponse(request, requestFilter);
    }

    @GetMapping("/by-sprint")
    PageResponse<UserStoryResponse> getUserStoryListBySprint(UserStorySearchRequest request, PageRequestFilter requestFilter){
        return userStoryService.getResponseBySprint(request, requestFilter);
    }

    @PutMapping("/{id}")
    ServiceResponse<Void> updateUserStory(@PathVariable UUID id, @RequestBody UserStoryRequest request){
        userStoryService.update(id, request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @DeleteMapping("/{id}")
    ServiceResponse<Void> deleteUserStory(@PathVariable UUID id){
        userStoryService.delete(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PostMapping("/{id}/update-status")
    ServiceResponse<Void> updateStatus (@PathVariable UUID id, @RequestBody UserStoryUpdateStatusRequest status){
        userStoryService.updateStatus(id, status.getStatus());
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PostMapping("/{id}/delete-from-sprint")
    ServiceResponse<Void> updateUserStoryStatusDeleteFromSprint(@PathVariable UUID id, @RequestBody UserStoryUpdateStatusRequest status){
        userStoryService.deleteFromSprint(id, status.getStatus());
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PostMapping("/{id}/delete-from-release")
    ServiceResponse<Void> updateUserStoryStatusDeleteFromRelease(@PathVariable UUID id, @RequestBody UserStoryUpdateStatusRequest status) {
        userStoryService.deleteFromRelease(id, status.getStatus());
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @PutMapping("/{id}/complete")
    ServiceResponse<Void> completeUserStory(@PathVariable UUID id){
        userStoryService.completeUserStory(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);

    }
}
