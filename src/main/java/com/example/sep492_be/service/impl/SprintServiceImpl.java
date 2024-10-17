package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.SprintRequest;
import com.example.sep492_be.dto.request.SprintSearchRequest;
import com.example.sep492_be.dto.response.ReleaseResponse;
import com.example.sep492_be.dto.response.SprintResponse;
import com.example.sep492_be.dto.response.UserStoryResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.entity.*;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.enums.SprintStatus;
import com.example.sep492_be.enums.UserStoryStatus;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.*;
import com.example.sep492_be.service.SprintService;
import com.example.sep492_be.service.UserStoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final ReleaseRepository releaseRepository;
    private final SprintUserStoryRepository sprintUserStoryRepository;
    private final UserStoryRepository userStoryRepository;
    private final UserStoryService userStoryService;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void createSprint(SprintRequest request) {
        SprintEntity entity = new SprintEntity(request);
        ReleaseEntity releaseEntity = releaseRepository.findById(request.getReleaseId()).orElse(null);
        ProjectEntity project = releaseEntity != null ? releaseEntity.getProject() : null;
        if(project != null) {
            validateSprint(project.getId(), entity.getStartDate());
            Instant startDate = entity.getStartDate();
            Long duration = project.getSprintTime();
            Instant endDate = startDate.plus(duration, ChronoUnit.DAYS);
            entity.setEndDate(endDate);
            Long maxIndex = sprintRepository.getMaxIndex(project.getId());
            entity.setCode(maxIndex != null ? maxIndex + 1 : 1);
        }
        entity.setRelease(releaseEntity);
        sprintRepository.save(entity);
    }

    private void validateSprint(UUID projectId, Instant startDate){
        Instant maxEndDate = sprintRepository.findByDate(projectId);
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        if(maxEndDate == null) return;
        // Convert Instant to LocalDate using the specified time zone
        LocalDate startLocalDate = startDate.atZone(zoneId).toLocalDate();
        LocalDate maxEndLocalDate = maxEndDate.atZone(zoneId).toLocalDate();


        if (maxEndLocalDate.isAfter(startLocalDate)) {
           throw new ResponseException(InvalidInputError.SPRINT_DATE_INVALID.getMessage(), InvalidInputError.SPRINT_DATE_INVALID);
        }
    }

    // This cron expression will run the job once a day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void updateStatusAllProject(){
        List<ProjectEntity> projectEntitys = projectRepository.findAll();
        projectEntitys.forEach(projectEntity -> updateSprint(projectEntity.getId()));
    }
    @Transactional
    public void    updateSprint(UUID projectId){
        List<SprintEntity> sprintEntities = sprintRepository.findByProjectUpdateSprint(projectId);
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        // Convert Instant to LocalDate using the specified time zone
        LocalDate nowLocalDate = now.atZone(zoneId).toLocalDate();
        for (SprintEntity sprintEntity : sprintEntities) {
            LocalDate localDate = sprintEntity.getEndDate().atZone(zoneId).toLocalDate();
            if(!sprintEntity.getStatus().equals(SprintStatus.Completed) && !localDate.isAfter(nowLocalDate)){
                List<UserStoryEntity> userStoryEntities = sprintUserStoryRepository.getListUserStory(sprintEntity.getId());
                userStoryEntities.forEach(entity -> entity.setStatus(UserStoryStatus.Pending));
                sprintUserStoryRepository.deleteBySprintId(sprintEntity.getId());
            }
            if(!sprintEntity.getStatus().equals(SprintStatus.In_Progress) && !sprintEntity.getStatus().equals(SprintStatus.Completed)) {
                LocalDate startDate = sprintEntity.getStartDate().atZone(zoneId).toLocalDate();
                LocalDate endDate = sprintEntity.getEndDate().atZone(zoneId).toLocalDate();
                if(nowLocalDate.isAfter(startDate) && nowLocalDate.isBefore(endDate)) {
                    sprintEntity.setStatus(SprintStatus.In_Progress);
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateSprint(UUID id, SprintRequest request) {
        SprintEntity entity = sprintRepository.findById(id).orElse(null);
        if (entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStartDate(request.getStartDate() != null? Instant.ofEpochMilli(request.getStartDate()) : null);
        entity.setEndDate(request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null);
        entity.setGoal(request.getGoal());
        sprintRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteSprint(UUID id) {
        SprintEntity entity = sprintRepository.findById(id).orElse(null);
        if (entity == null) return;
        entity.setDeleted(true);
        sprintRepository.save(entity);
    }

    @Override
    @Transactional
    public void updateStatusSprint(UUID id, SprintStatus status) {
        SprintEntity entity = sprintRepository.findById(id).orElse(null);
        if (entity == null) return;
        if(status.equals(SprintStatus.In_Progress)){
            List<SprintEntity> sprintEntities = sprintRepository.findByProjectId(entity.getRelease().getProject().getId() );
            if(!sprintEntities.isEmpty()){
                throw new ResponseException(InvalidInputError.SPRINT_STATUS_INVALID.getMessage(), InvalidInputError.SPRINT_STATUS_INVALID);
            }
        }
        entity.setStatus(status);
        if(status.equals(SprintStatus.Completed)){
            List<SprintUserStoryEntity> userStoryEntities = sprintUserStoryRepository.getList(id);
            for (SprintUserStoryEntity userStoryEntity : userStoryEntities) {
                if(!userStoryEntity.getUserStory().getStatus().equals(UserStoryStatus.Completed))
                    throw new ResponseException(InvalidInputError.SPRINT_UNABLE_TOCOMPLETE.getMessage(), InvalidInputError.SPRINT_UNABLE_TOCOMPLETE);
            }
        }
        sprintRepository.save(entity);
    }

    @Override
    @Transactional
    public PageResponse<SprintResponse> getSprints(SprintSearchRequest request, PageRequestFilter filter) {
        Pageable pageable = PageRequestFilter.converToPageable(filter);
        updateSprint(request.getProjectId());
        Page<SprintEntity> page = sprintRepository.searchSprint(request, pageable);
        List<SprintResponse> responses = page.getContent()
                .stream().map(SprintResponse::new).toList();
        userStoryService.getTotalUserStoryPointForSprint(responses);
        return PageResponse.<SprintResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(page.getNumber())
                        .pageSize(page.getSize())
                        .totalCount(page.getTotalElements())
                        .build())
                .data(responses)
                .build();
    }

    @Override
    public SprintResponse getDetail(UUID id) {
        SprintEntity entity = sprintRepository.findById(id).orElse(null);
        if (entity == null) return null;
        SprintResponse response = new SprintResponse(entity);
        List<SprintUserStoryEntity> sprintUserStoryEntities = sprintUserStoryRepository.getList(response.getId());
        List<UserStoryResponse> userStoryResponses = sprintUserStoryEntities.stream().filter(userStoryEntity -> userStoryEntity.getUserStory() != null)
                .map(sprintUserStoryEntity -> new UserStoryResponse(sprintUserStoryEntity.getUserStory())).toList();
        response.setUserStories(userStoryResponses);
        return response;
    }

    @Override
    @Transactional
    public void addStoryToSprint(UUID sprintId, UUID storyId) {
        SprintUserStoryEntity sprintUserStoryEntity = new SprintUserStoryEntity();
        sprintUserStoryEntity.setId(UUID.randomUUID());
        SprintEntity sprintEntity = sprintRepository.findById(sprintId).orElse(null);
        if (sprintEntity == null) return;
        sprintUserStoryEntity.setSprint(sprintEntity);
        UserStoryEntity userStoryEntity = userStoryRepository.findById(storyId).orElse(null);
        if (userStoryEntity == null) return;
        userStoryEntity.setStatus(UserStoryStatus.In_Progress);
        sprintUserStoryEntity.setUserStory(userStoryEntity);
        sprintUserStoryRepository.save(sprintUserStoryEntity);
    }
}
