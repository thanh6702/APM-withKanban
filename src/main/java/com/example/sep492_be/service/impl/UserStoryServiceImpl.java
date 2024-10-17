package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.UserStoryRequest;
import com.example.sep492_be.dto.request.UserStorySearchRequest;
import com.example.sep492_be.dto.request.UserStoryUpdateStatusRequest;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.entity.*;
import com.example.sep492_be.enums.UserStoryStatus;
import com.example.sep492_be.repository.*;
import com.example.sep492_be.service.ProjectService;
import com.example.sep492_be.service.UserStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStoryServiceImpl implements UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ReleaseUserStoryRepository releaseUserStoryRepository;
    private final SprintUserStoryRepository sprintUserStoryRepository;
    private final SprintRepository sprintRepository;

    @Override
    @Transactional
    public void create(UserStoryRequest request) {
        ProjectEntity projectEntity = projectRepository.findById(request.getProjectId()).orElse(null);
        UserStoryEntity userStoryEntity = new UserStoryEntity(request);
        if(projectEntity != null) {
            Long maxIndex = userStoryRepository.getMaxIndexByProjectId(projectEntity.getId());
            userStoryEntity.setProject(projectEntity);
            userStoryEntity.setCode(maxIndex == null ? 1 : maxIndex + 1);
        }
        userStoryRepository.save(userStoryEntity);
    }

    @Override
    @Transactional
    public void update(UUID id, UserStoryRequest request) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity != null) {
            userStoryEntity.setDescription(request.getDescription());
            userStoryEntity.setPriorityLevel(request.getPriorityLevel());
            userStoryEntity.setSummary(request.getSummary());
            userStoryEntity.setVersion(request.getVersion());
            userStoryEntity.setAcceptanceCriteria(request.getAcceptanceCriteria());
            userStoryEntity.setUserStoryPoint(request.getUserStoryPoint());
            userStoryRepository.save(userStoryEntity);
        }
    }
    @Override
    public void delete(UUID id) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity != null) {
            userStoryEntity.setDeleted(true);
            userStoryRepository.save(userStoryEntity);
        }
    }

    @Override
    public PageResponse<UserStoryResponse> getResponse(UserStorySearchRequest request, PageRequestFilter requestFilter) {
        Pageable pageable = PageRequestFilter.converToPageable(requestFilter);
        UUID releaseId = null;
        if(request.getSprintId() != null){
            SprintEntity sprintEntity = sprintRepository.findById(request.getSprintId()).orElse(null);
            releaseId = sprintEntity != null && sprintEntity.getRelease() != null ? sprintEntity.getRelease().getId() : null;
        }
        Page<UserStoryEntity> userStoryEntities = userStoryRepository.getListUserStory(request, releaseId ,pageable);
        List<UserStoryResponse> responses = userStoryEntities.getContent().stream().map(UserStoryResponse::new).toList();
        return PageResponse.<UserStoryResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(userStoryEntities.getNumber())
                        .pageSize(userStoryEntities.getSize())
                        .totalCount(userStoryEntities.getTotalElements())
                        .build())
                .data(responses)
                .build();
    }

    @Override
    public PageResponse<UserStoryResponse> getResponseToAddToSprint(UserStorySearchRequest request, PageRequestFilter requestFilter) {
        return null;
    }

    @Override
    @Transactional
    public void deleteFromRelease(UUID id, UserStoryStatus status) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(status);
        userStoryRepository.save(userStoryEntity);
        releaseUserStoryRepository.deleteByUserStoryId(userStoryEntity.getId());
        sprintUserStoryRepository.deleteByUserStoryId(userStoryEntity.getId());
    }
    @Override
    public PageResponse<UserStoryResponse> getResponseBySprint(UserStorySearchRequest request, PageRequestFilter requestFilter) {
        Pageable pageable = PageRequestFilter.converToPageable(requestFilter);
        Page<UserStoryEntity> userStoryEntities = userStoryRepository.getListUserStoryBySprint(request ,pageable);
        List<UserStoryResponse> responses = userStoryEntities.getContent().stream().map(UserStoryResponse::new).toList();
        return PageResponse.<UserStoryResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(userStoryEntities.getNumber())
                        .pageSize(userStoryEntities.getSize())
                        .totalCount(userStoryEntities.getTotalElements())
                        .build())
                .data(responses)
                .build();
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, UserStoryStatus status) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(status);
        userStoryRepository.save(userStoryEntity);
//        releaseUserStoryRepository.deleteByUserStoryId(userStoryEntity.getId());
//        sprintUserStoryRepository.deleteByUserStoryId(userStoryEntity.getId());
    }

    @Override
    @Transactional
    public void deleteFromSprint(UUID id, UserStoryStatus status) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(status);
        userStoryRepository.save(userStoryEntity);
        sprintUserStoryRepository.deleteByUserStoryId(id);
    }

    @Override
    @Transactional
    public void completeUserStory(UUID id) {
        UserStoryEntity userStoryEntity = userStoryRepository.findById(id).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(UserStoryStatus.Completed);
        userStoryEntity.setCompletedAt(Instant.now());

    }

    @Override
    public void getTotalUserStoryPointForRelease(List<ReleaseResponse> releases) {
        List<UUID> releaseIds = releases.stream().map(ReleaseResponse::getId).toList();
        Map<UUID, List<ReleaseUserStoryEntity>> userStoryMap = userStoryRepository.getReleaseUserStoryEntitiesByRelease(releaseIds)
                .stream().collect(Collectors.groupingBy(ReleaseUserStoryEntity::getReleaseId));
        Map<UUID, UserStoryEntity> userStoryEntityMap = userStoryRepository.getUserStoryEntitiesByRelease(releaseIds)
                .stream().collect(Collectors.toMap(UserStoryEntity::getId, e -> e));
        releases.forEach(releaseResponse -> {
            List<ReleaseUserStoryEntity> sprintUserStoryEntities = userStoryMap.get(releaseResponse.getId());
            long totalPoint = 0L;
            if(sprintUserStoryEntities != null){
                for (ReleaseUserStoryEntity sprintUserStoryEntity : sprintUserStoryEntities) {
                    UserStoryEntity userStoryEntity = userStoryEntityMap.get(sprintUserStoryEntity.getUserStoryId());
                    if(userStoryEntity != null && userStoryEntity.getUserStoryPoint() != null) totalPoint += userStoryEntity.getUserStoryPoint();
                }
            }
            releaseResponse.setTotalPoint(totalPoint);

        });
    }

    @Override
    public void getTotalUserStoryPointForSprint(List<SprintResponse> sprints) {
        List<UUID> sprintIds = sprints.stream().map(SprintResponse::getId).toList();
        Map<UUID, List<SprintUserStoryEntity>> userStoryMap = userStoryRepository.getSprintUserStoryEntitiesBySprint(sprintIds)
                .stream().collect(Collectors.groupingBy(SprintUserStoryEntity::getSprintId));
        Map<UUID, UserStoryEntity> userStoryEntityMap = userStoryRepository.getUserStoryEntitiesBySprint(sprintIds)
                .stream().collect(Collectors.toMap(UserStoryEntity::getId, e -> e));
        sprints.forEach(sprintEntity -> {
            List<SprintUserStoryEntity> sprintUserStoryEntities = userStoryMap.get(sprintEntity.getId());
            long totalPoint = 0L;
            if(sprintUserStoryEntities != null){
                for (SprintUserStoryEntity sprintUserStoryEntity : sprintUserStoryEntities) {
                    UserStoryEntity userStoryEntity = userStoryEntityMap.get(sprintUserStoryEntity.getUserStoryId());
                    if(userStoryEntity != null && userStoryEntity.getUserStoryPoint() != null) totalPoint += userStoryEntity.getUserStoryPoint();
                }
            }
            sprintEntity.setTotalPoint(totalPoint);

        });
    }
}
