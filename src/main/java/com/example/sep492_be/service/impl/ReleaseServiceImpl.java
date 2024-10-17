package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.ReleaseRequest;
import com.example.sep492_be.dto.request.ReleaseSearchRequest;
import com.example.sep492_be.dto.response.ProjectResponse;
import com.example.sep492_be.dto.response.ReleaseResponse;
import com.example.sep492_be.dto.response.UserStoryResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.entity.*;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.enums.ReleaseStatus;
import com.example.sep492_be.enums.SprintStatus;
import com.example.sep492_be.enums.UserStoryStatus;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.*;
import com.example.sep492_be.service.ReleaseService;
import com.example.sep492_be.service.UserStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReleaseServiceImpl implements ReleaseService {
    private final ReleaseRepository releaseRepository;
    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final ReleaseUserStoryRepository releaseUserStoryRepository;
    private final SprintRepository sprintRepository;
    private final UserStoryService userStoryService;


    @Override
    @Transactional
    public void create(ReleaseRequest request) {
        ReleaseEntity releaseEntity = new ReleaseEntity(request);
        if(request.getName() == null || request.getName().isEmpty())
            throw new ResponseException(InvalidInputError.RELEASE_NAME_CANNOT_NULL.getMessage(), InvalidInputError.RELEASE_NAME_CANNOT_NULL);
        if(request.getStartDate() == null || request.getEndDate() == null || request.getEndDate() < request.getStartDate())
            throw new ResponseException(InvalidInputError.RELEASE_DATE.getMessage(), InvalidInputError.RELEASE_DATE);
        if(request.getProjectId() != null) {
            ProjectEntity project = projectRepository.findById(request.getProjectId()).orElse(null);
            releaseEntity.setProject(project);
            if(project != null) {
                Long code = releaseRepository.getMaxIndex(project.getId());
                releaseEntity.setCode(code != null ? code + 1 : 1L);
            }

        }
        releaseRepository.save(releaseEntity);
    }

    @Override
    public void update(UUID id, ReleaseRequest request) {
        ReleaseEntity releaseEntity = releaseRepository.findById(id).orElse(null);
        if (releaseEntity == null) return;
        ProjectEntity projectEntity = projectRepository.findById(request.getProjectId()).orElse(null);
        if (projectEntity == null) return;
        if(request.getName() == null || request.getName().isEmpty())
            throw new ResponseException(InvalidInputError.RELEASE_NAME_CANNOT_NULL.getMessage(), InvalidInputError.RELEASE_NAME_CANNOT_NULL);
        if(request.getStartDate() == null || request.getEndDate() == null || request.getEndDate() < request.getStartDate())
            throw new ResponseException(InvalidInputError.RELEASE_DATE.getMessage(), InvalidInputError.RELEASE_DATE);
        releaseEntity.setProject(projectEntity);
        releaseEntity.setDescription(request.getDescription());
        releaseEntity.setTitle(request.getTitle());
        releaseEntity.setName(request.getName());
        releaseEntity.setStartDate(request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null);
        releaseEntity.setEndDate(request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null);
        releaseRepository.save(releaseEntity);
    }

    @Override
    public void delete(UUID id) {
        ReleaseEntity releaseEntity = releaseRepository.findById(id).orElse(null);
        if (releaseEntity == null) return;
        releaseEntity.setDeleted(true);
        releaseRepository.save(releaseEntity);
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, ReleaseStatus status) {
        ReleaseEntity releaseEntity = releaseRepository.findById(id).orElse(null);
        if (releaseEntity == null) return;
        if(status.equals(ReleaseStatus.In_Progress)){
            List<ReleaseEntity> findByProject = releaseRepository.findByProjectAndStatus(releaseEntity.getProject().getId(), ReleaseStatus.In_Progress);
            if(!findByProject.isEmpty()){
                throw new ResponseException(InvalidInputError.RELEASE_STATUS_INVALID.getMessage(), InvalidInputError.RELEASE_STATUS_INVALID);
            }
        }
        releaseEntity.setStatus(status);
        List<SprintEntity> sprintEntities = sprintRepository.findByReleaseId(releaseEntity.getId());
        for (SprintEntity sprintEntity : sprintEntities) {
            if(!sprintEntity.getStatus().equals(SprintStatus.Completed)){
                throw new ResponseException(InvalidInputError.RELEASE_UNABLE_TOCOMPLETE.getMessage(), InvalidInputError.RELEASE_UNABLE_TOCOMPLETE);
            }
        }
        releaseRepository.save(releaseEntity);
    }

    @Override
    public PageResponse<ReleaseResponse> findByReleaseRequest(ReleaseSearchRequest request, PageRequestFilter filter) {
        Pageable pageable = PageRequestFilter.converToPageable(filter);
        Page<ReleaseEntity> releaseEntityPage = releaseRepository.getListRelease(request, pageable);
        List<ReleaseResponse> responses = releaseEntityPage.getContent()
                .stream().map(ReleaseResponse::new).toList();
        userStoryService.getTotalUserStoryPointForRelease(responses);

        return PageResponse.<ReleaseResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(releaseEntityPage.getNumber())
                        .pageSize(releaseEntityPage.getSize())
                        .totalCount(releaseEntityPage.getTotalElements())
                        .build())
                .data(responses)
                .build();    }

    @Override
    public ReleaseResponse findById(UUID id) {
        ReleaseEntity releaseEntity = releaseRepository.findById(id).orElse(null);
        if (releaseEntity == null) return null;
        ReleaseResponse response = new ReleaseResponse(releaseEntity);
        List<ReleaseUserStoryEntity> releaseUserStoryEntities = releaseUserStoryRepository.findByReleaseId(releaseEntity.getId());
        List<UserStoryResponse> userStoryResponses = releaseUserStoryEntities != null
                ? releaseUserStoryEntities.stream().filter(releaseUserStoryEntity -> releaseUserStoryEntity != null && releaseUserStoryEntity.getUserStory() != null).map(releaseUserStoryEntity -> new UserStoryResponse(releaseUserStoryEntity.getUserStory())).toList()
                : new ArrayList<>();
        response.setUserStories(userStoryResponses);
        return response;
    }

    @Override
    @Transactional
    public void addStoryToRelease(UUID id, UUID storyId) {
        ReleaseUserStoryEntity releaseUserStoryEntity = new ReleaseUserStoryEntity();
        releaseUserStoryEntity.setId(UUID.randomUUID());
        ReleaseEntity releaseEntity = releaseRepository.findById(id).orElse(null);
        if (releaseEntity == null) return;
        releaseUserStoryEntity.setRelease(releaseEntity);
        UserStoryEntity userStoryEntity = userStoryRepository.findById(storyId).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(UserStoryStatus.On_Going);
        releaseUserStoryEntity.setUserStory(userStoryEntity);
        releaseUserStoryRepository.save(releaseUserStoryEntity);
    }
}
