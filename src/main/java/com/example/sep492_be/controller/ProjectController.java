package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.entity.UserStoryEntity;
import com.example.sep492_be.repository.UserStoryRepository;
import com.example.sep492_be.service.ProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/project")
@Setter
@Getter
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserStoryRepository userStoryRepository;

    @PostMapping
    ServiceResponse<Void> createProject(@RequestBody  ProjectRequest request){
        projectService.createProject(request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @GetMapping("/{id}")
    ServiceResponse<ProjectResponse> getProjectById(@PathVariable UUID id){
        return ServiceResponse.succeed(HttpStatus.OK,  projectService.getProject(id));
    }

    @GetMapping
    PageResponse<ProjectResponse> getProjects(ProjectSearchRequest request, PageRequestFilter filter){
        return projectService.getProjects(request, filter);
    }

    @PutMapping("/update-general/{id}")
    ServiceResponse<Void> updateProject(@PathVariable UUID id, @RequestBody ProjectRequest request){
        projectService.updateProject(id, request);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

        @PutMapping("/update-member/{id}")
    ServiceResponse<Void> updateMember(@PathVariable UUID id, @RequestBody ProjectRequest request){
        projectService.updateProject(id, request);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @PutMapping("/{id}/add-member")
    ServiceResponse<Void> addMember(@PathVariable UUID id, @RequestBody AddMemberRequest request){
        projectService.addMember(id, request);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @DeleteMapping("/project-member/{id}")
    ServiceResponse<Void> deleteProjectMember(@PathVariable UUID id){
        projectService.deleteMember(id);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @DeleteMapping("/{id}")
    ServiceResponse<Void> deleteProject(@PathVariable UUID id){
        projectService.deleteProject(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);

    }

    @PostMapping("/{id}/add-column")
    ServiceResponse<Void> addColumn(@PathVariable  UUID id, @RequestBody ColumnRequest request){
        projectService.addColumn(id, request);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @PutMapping("/{id}/column/{columnId}")
    ServiceResponse<Void> updateColumn(@PathVariable  UUID id, @PathVariable UUID columnId, @RequestBody ColumnRequest request){
        projectService.updateColumn(id,columnId, request);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @DeleteMapping("/{id}/column/{columnId}")
    ServiceResponse<Void> deleteColumn(@PathVariable  UUID id, @PathVariable UUID columnId){
        projectService.deleteColumn(id,columnId);
        return ServiceResponse.succeed(HttpStatus.OK,  null);
    }

    @GetMapping("/{id}/column")
    ServiceResponse<List<ColumnResponse>> getListColumn(@PathVariable UUID id){
        return ServiceResponse.succeed(HttpStatus.OK, projectService.getListColumns(id));
    }

    @GetMapping("/{id}/report")
    ServiceResponse<ProjectReportResponse> getProjectReport(@PathVariable UUID id){
        return ServiceResponse.succeed(HttpStatus.OK, projectService.getProjectReport(id, null));
    }

    @GetMapping("/{id}/report-all-user")
    ServiceResponse<ProjectReportByDateResponse> getProjectReportForAllUser(@PathVariable UUID id, Long date){
        return ServiceResponse.succeed(HttpStatus.OK, projectService.projectReportByDate(id, date));
    }

    @GetMapping("/{id}/user-story-point")
    ServiceResponse<Long> getTotalUserStoryPointGained(@PathVariable UUID id, Long date){
        return ServiceResponse.succeed(HttpStatus.OK, projectService.getTotalStoryPoint(id, date));
    }

    @GetMapping("/{id}/point")
    ServiceResponse<BurnDownChartResponse> getByBurnDownChart(@PathVariable UUID id){
        return ServiceResponse.succeed(HttpStatus.OK, projectService.getResponse(id));

    }

}
