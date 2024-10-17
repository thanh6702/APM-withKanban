package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.entity.*;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.enums.ProjectStatus;
import com.example.sep492_be.enums.UserStoryStatus;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.*;
import com.example.sep492_be.service.ProjectService;
import com.example.sep492_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ColumnRepository columnRepository;
    private final TaskRepository taskRepository;
    private final TaskExecutionAutoConfiguration taskExecutionAutoConfiguration;
    private final WorkLogRepository workLogRepository;
    private final UserStoryRepository userStoryRepository;
    private final ReleaseUserStoryRepository releaseUserStoryRepository;
    private final SprintUserStoryRepository sprintUserStoryRepository;

    private final SprintRepository sprintRepository;

    @Override
    public void createProject(ProjectRequest request) {
        ProjectEntity projectEntity = new ProjectEntity(request);
        UserProfile userProfile = userService.getProfile();
        projectEntity.setCreatedBy(userProfile.getId());
        UserEntity userEntitySave = userRepository.findById(userProfile.getId()).get();
        projectEntity.setProjectManager(userEntitySave);
        ProjectEntity checked = projectRepository.findByShortedName(request.getShortedName(), request.getName());
        if(checked != null) throw new ResponseException(InvalidInputError.PROJECT_CODE_DUPLICATE.getMessage(), InvalidInputError.PROJECT_CODE_DUPLICATE);
        projectEntity = projectRepository.save(projectEntity);
        // Create Project Member
        List<ProjectMemberEntity> projectMemberEntities = new ArrayList<>();
        List<UUID> userId = request.getMembers().stream().map(ProjectMemberRequest::getUserId).toList();
        Map<UUID, UserEntity> userEntities = userRepository.findByIdIn(userId).stream().collect(Collectors.toMap(
                UserEntity::getId,
                userEntity -> userEntity
        ));
        for (ProjectMemberRequest member : request.getMembers()) {
            ProjectMemberEntity projectMemberEntity = new ProjectMemberEntity();
            projectMemberEntity.setProjectEntity(projectEntity);
            projectMemberEntity.setProjectId(projectEntity.getId());
            UserEntity userEntity = userEntities.get(member.getUserId());
            if(userEntity == null) {return;}
            projectMemberEntity.setUserEntity(userEntity);
            projectMemberEntity.setUserId(userEntity.getId());
            projectMemberEntity.setRoleName(member.getRoleName());
            projectMemberEntities.add(projectMemberEntity);
        }
        ColumnEntity newColumnEnitity = new ColumnEntity("To do", 1L);
        newColumnEnitity.setProject(projectEntity);
        ColumnEntity inProgress = new ColumnEntity("In Progress", 2L);
        inProgress.setProject(projectEntity);
        ColumnEntity pending = new ColumnEntity("Pending", 3L);
        pending.setProject(projectEntity);
        ColumnEntity completed = new ColumnEntity("Completed", 4L);
        completed.setProject(projectEntity);
        List<ColumnEntity> columnEntities = List.of(newColumnEnitity, inProgress, pending, completed    );
        columnRepository.saveAll(columnEntities);
        projectMemberRepository.saveAll(projectMemberEntities);
    }

    @Override
    public ProjectResponse getProject(UUID id) {
        ProjectEntity projectEntity = projectRepository.findById(id).orElse(null);
        if(projectEntity == null) {return null;}
        ProjectResponse projectResponse = new ProjectResponse(projectEntity);
        Map<UUID, List<ProjectMemberResponse>> map = getUserByProject(Collections.singletonList(id));
        projectResponse.setMembers(map.get(id));
        List<ColumnEntity> columnEntities = columnRepository.findByProjectId(projectEntity.getId());
        List<ColumnResponse> columnResponses = columnEntities.stream().map(ColumnResponse::new).toList();
        projectResponse.setColumnResponses(columnResponses);
        return projectResponse;
    }
    private Map<UUID, List<ProjectMemberResponse>> getUserByProject(List<UUID> projectIds){
        List<ProjectMemberEntity> projectMemberEntities = projectMemberRepository.findAllByProjectIdIn(projectIds);
        Map<UUID, List<ProjectMemberEntity>> projectMemberMap = projectMemberEntities.stream().collect(Collectors.groupingBy(
                entity -> entity.getProjectEntity().getId()
        ));
        Map<UUID, List<ProjectMemberResponse>> projectResponseMap = new HashMap<>();
        for (Map.Entry<UUID, List<ProjectMemberEntity>> entry : projectMemberMap.entrySet()) {
            UUID projectId = entry.getKey();
            List<ProjectMemberEntity> list = entry.getValue();
            List<ProjectMemberResponse> projectMemberResponseList = new ArrayList<>();
            for(ProjectMemberEntity projectMemberEntity : list){
                ProjectMemberResponse projectMemberResponse = new ProjectMemberResponse(projectMemberEntity);
                projectMemberResponseList.add(projectMemberResponse);
            }
            projectResponseMap.put(projectId, projectMemberResponseList);
        }
        return projectResponseMap;
    }

    @Override
    public PageResponse<ProjectResponse> getProjects(ProjectSearchRequest request, PageRequestFilter filter) {
        Pageable pageable = PageRequestFilter.converToPageable(filter);
        UserProfile userProfile = userService.getProfile();
        request.setUserId(userProfile.getId());
        Page<ProjectEntity> projectEntities = projectRepository.search(
                request.getSearchTerm() == null || request.getSearchTerm().isBlank() ? null : request.getSearchTerm(),
                request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null,
                request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null,
                request.getTab().name(),
                request.getUserId(),
                request.getDepartmentId(),
                request.getStatuses(),
                pageable);
        List<ProjectResponse> responses = projectEntities.getContent().stream().map(ProjectResponse::new).toList();
        return PageResponse.<ProjectResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(projectEntities.getNumber())
                        .pageSize(projectEntities.getSize())
                        .totalCount(projectEntities.getTotalElements())
                        .build())
                .data(responses)
                .build();
    }

    @Override
    @Transactional
    public void updateProject(UUID id, ProjectRequest request) {
        UserProfile userProfile = userService.getProfile();
        ProjectEntity checked = projectRepository.findByShortedName(request.getShortedName(), request.getName());
        if(checked != null && !checked.getId().equals(id)) throw new ResponseException(InvalidInputError.PROJECT_CODE_DUPLICATE.getMessage(), InvalidInputError.PROJECT_CODE_DUPLICATE);
        Optional<ProjectEntity> projectEntity = projectRepository.findById(id);
        if(projectEntity.isEmpty()) throw new ResponseException(InvalidInputError.PROJECT_NOT_FOUND.getMessage(), InvalidInputError.PROJECT_NOT_FOUND);
        ProjectEntity project = projectEntity.get();
        project.setUpdatedBy(userProfile.getId());
        project.setUpdatedAt(Instant.now());
        project.setDescription(request.getDescription());
        project.setShortedName(request.getShortedName());
        project.setName(request.getName());
        project.setStatus(request.getStatus());
        project.setSprintTime(request.getSprintTime());
//        project.setStartDate(request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null);
//        project.setEndDate(request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null);
        if(request.getStatus() != null && request.getStatus().equals(ProjectStatus.IN_PROGRESS)) project.setStartDate(Instant.now());
        if(request.getStatus() != null && request.getStatus().equals(ProjectStatus.FINISHED)) project.setEndDate(Instant.now());
        projectRepository.save(project);

    }

    @Override
    public void updateMember(UUID id, ProjectRequest request) {
        ProjectEntity projectEntity = projectRepository.findById(id).orElse(null);
        if(projectEntity == null) return;
        projectMemberRepository.deleteByProjectEntityId(id);
        List<ProjectMemberEntity> projectMemberEntities = new ArrayList<>();
        List<UUID> userId = request.getMembers().stream().map(ProjectMemberRequest::getUserId).toList();
        Map<UUID, UserEntity> userEntities = userRepository.findByIdIn(userId).stream().collect(Collectors.toMap(
                UserEntity::getId,
                userEntity -> userEntity
        ));
        request.getMembers().forEach(member -> {
            ProjectMemberEntity projectMemberEntity = new ProjectMemberEntity();
            projectMemberEntity.setProjectEntity(projectEntity);
            UserEntity userEntity = userEntities.get(member.getUserId());
            if(userEntity == null) {return;}
            projectMemberEntity.setUserEntity(userEntity);
            projectMemberEntities.add(projectMemberEntity);
        });
        projectMemberRepository.saveAll(projectMemberEntities);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        UserProfile userProfile = userService.getProfile();
        Optional<ProjectEntity> projectEntity = projectRepository.findById(id);
        if(projectEntity.isEmpty()) throw new ResponseException(InvalidInputError.PROJECT_NOT_FOUND.getMessage(), InvalidInputError.PROJECT_NOT_FOUND);
        ProjectEntity project = projectEntity.get();
        project.setDeleted(true);
        project.setUpdatedAt(Instant.now());
        project.setUpdatedBy(userProfile.getId());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void addMember(UUID id, AddMemberRequest request) {
        ProjectEntity projectEntity = projectRepository.findById(id).orElse(null);
        if(projectEntity == null) return;
        if(request.getRoleName() == null || request.getRoleName().isBlank())
            throw new ResponseException(InvalidInputError.ROLE_CANNOT_BE_NULL.getMessage(), InvalidInputError.ROLE_CANNOT_BE_NULL);
        ProjectMemberEntity projectMemberEntity = new ProjectMemberEntity();
        projectMemberEntity.setProjectEntity(projectEntity);
        projectMemberEntity.setProjectId(id);
        projectMemberEntity.setAssignedDate(new Date(request.getAssignedDate()).toInstant());
        UserEntity userEntity = userRepository.findById(request.getUserId()).get();
        projectMemberEntity.setUserEntity(userEntity);
        projectMemberEntity.setRoleName(request.getRoleName());
        projectMemberEntity.setUserId(request.getUserId());
        projectMemberRepository.save(projectMemberEntity);
    }

    @Override
    @Transactional
    public void deleteMember(UUID id) {
        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findById(id).orElse(null);
        if(projectMemberEntity == null) return;
        projectMemberRepository.delete(projectMemberEntity);
    }

    @Override
    @Transactional
    public void addColumn(UUID projectId, ColumnRequest request) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElse(null);
        ColumnEntity columnEntity = new ColumnEntity(request);
        columnEntity.setProject(projectEntity);
        if(request.getOrder() == null || request.getOrder() > columnRepository.findMaxOrderById(projectId) + 1){
            columnEntity.setOrder(columnRepository.findMaxOrderById(projectId) + 1);
        }
        if(request.getOrder() <= 0){
            columnEntity.setOrder(1L);
            updateOrder(1L, projectId);
        }
        else{
            columnEntity.setOrder(request.getOrder());
            updateOrder(request.getOrder(), projectId);
        }
        columnRepository.save(columnEntity);
    }

    private void updateOrder(Long order, UUID projectId){
        List<ColumnEntity> columnEntities= columnRepository.findColumnOrderGreaterByAndProjectId(order, projectId);
        columnEntities.forEach(entity -> entity.setOrder(entity.getOrder() + 1));
    }

    @Override
    public List<ColumnResponse> getListColumns(UUID projectId) {
        List<ColumnEntity> columnEntities = columnRepository.findByProjectId(projectId);
        return columnEntities.stream()
                .sorted(Comparator.comparing(ColumnEntity::getOrder)) // Assuming 'getOrder' gets the order field
                .map(ColumnResponse::new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void updateColumn(UUID projectId, UUID columnId, ColumnRequest request) {
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElse(null);
        if(columnEntity == null) return;
        if(!columnEntity.getOrder().equals(request.getOrder())) {
            columnEntity.setOrder(request.getOrder());
            List<ColumnEntity> columnEntities= columnRepository.findColumnOrderGreaterByAndProjectId(request.getOrder(), projectId);
            columnEntities.forEach(entity -> {
                if(!entity.getId().equals(columnEntity.getId()))
                    entity.setOrder(entity.getOrder() + 1);
            });
        }
        columnEntity.setMax(request.getMax());
        columnEntity.setName(request.getName());
        columnRepository.save(columnEntity);

    }

    @Override
    @Transactional
    public void deleteColumn(UUID projectId, UUID columnId){
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElse(null);
        if(columnEntity == null) return;
        columnEntity.setDeleted(true);
        columnEntity.setUpdatedAt(Instant.now());
        columnRepository.save(columnEntity);
        List<ColumnEntity> columnEntities= columnRepository.findColumnOrderGreaterByAndProjectId(columnEntity.getOrder(), projectId);
        columnEntities.forEach(entity -> entity.setOrder(entity.getOrder() - 1));
    }

    @Override
    public ProjectReportResponse getProjectReport(UUID projectId, Long date) {
        List<TaskEntity> taskEntities = taskRepository.getTaskByProjectId(projectId, calculateEndOfDayInEpochMillis(date));
        ProjectReportResponse response = new ProjectReportResponse();
        response.setCycleTime((double) Math.round(calculateTotalDurationInHours(taskEntities) * 100 / taskEntities.size()) / 100);
        response.setProjectId(projectId);
        List<WorkLogEntity> workLogEntities = workLogRepository.findAllByTaskIdInAndDate(projectId, calculateEndOfDayInEpochMillis(date));
        response.setLeadTime((double) Math.round(calculateTotalDurationInHoursWorKLog(workLogEntities) * 100 / taskEntities.size()) / 100 );
        return response;
    }

    @Override
    public ProjectReportByDateResponse projectReportByDate(UUID projectId, Long date) {
        ProjectReportResponse projectReportResponse = getProjectReport(projectId, date);
        ProjectReportByDateResponse projectReportByDateResponse = new ProjectReportByDateResponse();
        projectReportByDateResponse.setId(projectId);
        projectReportByDateResponse.setCycleTime(projectReportResponse.getCycleTime());
        projectReportByDateResponse.setLeadTime(projectReportResponse.getLeadTime());
        Map<UUID, List<TaskEntity>> taskEntityMap = taskRepository.getTaskByProjectId(projectId, calculateEndOfDayInEpochMillis(date))
                .stream().collect(Collectors.groupingBy(TaskEntity::getAssignId));
        Map<UUID, ProjectUserResponse> userResponseMap = new HashMap<>();
        Map<UUID, List<WorkLogEntity>> workLogMap = workLogRepository.findAllByTaskIdInAndDate(projectId, calculateEndOfDayInEpochMillis(date))
                .stream().filter(entity -> entity.getTask() != null && entity.getTask().getAssignId() != null).collect(Collectors.groupingBy(entity -> entity.getTask().getAssignId()));
        taskEntityMap.forEach((uuid, taskEntity) -> {
            ProjectUserResponse projectUserResponse;
            if(userResponseMap.containsKey(uuid)) {
                projectUserResponse = userResponseMap.get(uuid);
            }
            else {
                projectUserResponse = new ProjectUserResponse();
                projectUserResponse.setUserId(uuid);
                userResponseMap.put(uuid, projectUserResponse);
            }
            List<WorkLogEntity> workLogEntities = workLogMap.get(uuid);
            if (workLogEntities != null)
                projectUserResponse.setLeadTime((double) Math.round(calculateTotalDurationInHoursWorKLog(workLogEntities) * 100 / taskEntity.size()) / 100);
            projectUserResponse.setCycleTime((double) Math.round(calculateTotalDurationInHours(taskEntity) * 100 / taskEntity.size()) / 100);
        });
        workLogMap.forEach((uuid, workLogList) -> {
            ProjectUserResponse projectUserResponse;
            if(userResponseMap.containsKey(uuid)) {
                projectUserResponse = userResponseMap.get(uuid);
            }
            else {
                projectUserResponse = new ProjectUserResponse();
                projectUserResponse.setUserId(uuid);
                userResponseMap.put(uuid, projectUserResponse);
            }
            List<TaskEntity> taskEntities = taskEntityMap.get(uuid);
            if(taskEntities == null) return;
            if (workLogList != null)
                projectUserResponse.setLeadTime((double) Math.round(calculateTotalDurationInHoursWorKLog(workLogList) * 100 / taskEntities.size()) / 100);
        });
        projectReportByDateResponse.setUserResponses(userResponseMap.values().stream().toList());
        return projectReportByDateResponse;
    }

    public static double calculateTotalDurationInHours(List<TaskEntity> taskEntities) {
        double totalDurationInHours = 0.0;

        if (taskEntities != null) {
            for (TaskEntity task : taskEntities) {
                Instant startDate = task.getCreatedAt();
                Instant endDate = task.getEndDate();

                if (startDate != null && endDate != null) {
                    Duration duration = Duration.between(startDate, endDate);
                    totalDurationInHours += (duration.toMinutes() / 60.0); // Convert minutes to hours
                }
            }
            totalDurationInHours = Math.round(totalDurationInHours * 100 / taskEntities.size() ) / 100.0;
        }

        return totalDurationInHours;
    }

    public static double calculateTotalDurationInHoursWorKLog(List<WorkLogEntity> workLogEntities) {
        double totalDurationInHours = 0.0;

        if (workLogEntities != null) {
            for (WorkLogEntity workLogEntity : workLogEntities) {
                Instant startDate = workLogEntity.getStartDate();
                Instant endDate = workLogEntity.getEndDate();

                if (startDate != null && endDate != null) {
                    Duration duration = Duration.between(startDate, endDate);
                    totalDurationInHours += (duration.toMinutes() / 60.0); // Convert minutes to hours
                }
            }
        }

        return totalDurationInHours;
    }


    public Instant calculateEndOfDayInEpochMillis(Long epochMillis) {

        // Convert the epoch milliseconds to Instant
        Instant instant = epochMillis != null ? Instant.ofEpochMilli(epochMillis) : Instant.now();

        // Convert Instant to ZonedDateTime in UTC+7 timezone
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC+7"));

        // Adjust to the end of the day in UTC+7 timezone
        ZonedDateTime endOfDay = zonedDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // Return the Instant representing the end of the day
        return endOfDay.toInstant();
    }

    @Override
    public Long getTotalStoryPoint(UUID projectId, Long date) {
        List<UserStoryEntity> userStoryEntities = userStoryRepository.getUserStoryEntitiesBy(projectId, calculateEndOfDayInEpochMillis(date));
        Long sum = userStoryEntities.stream()
                .filter(entity -> entity.getUserStoryPoint() != null)
                .map(UserStoryEntity::getUserStoryPoint)
                .mapToLong(Long::longValue)
                .sum();
        return sum;
    }

    @Override
    public BurnDownChartResponse getResponse(UUID releaseId) {
        List<UserStoryEntity> releaseUserStoryEntities = releaseUserStoryRepository.findUserStoryByReleaseId(releaseId);
        Long totalPoint =  releaseUserStoryEntities.stream().filter(userStoryEntity -> userStoryEntity.getStatus() != null).mapToLong(value -> value.getUserStoryPoint()).sum();
        List<SprintEntity> sprintEntities = sprintRepository.findByReleaseId(releaseId);
        List<UUID> sprintIds = sprintEntities.stream().map(SprintEntity::getId).toList();
        List<UserStoryEntity> userStoryEntities = sprintUserStoryRepository.getListUserStoryByListSprintIds(sprintIds);
        List<SprintUserStoryEntity> sprintUserStoryRepositories = sprintUserStoryRepository.getListByListSprintIds(sprintIds);
        Map<UUID, List<SprintUserStoryEntity>> sprintUserStoryMap = sprintUserStoryRepositories.stream().collect(Collectors.groupingBy(SprintUserStoryEntity::getSprintId));
        Map<UUID, UserStoryEntity> uuidUserStoryEntityMap = userStoryEntities.stream().collect(Collectors.toMap(UserStoryEntity::getId, x -> x));
        List<BurnDownBySprint> burnDownBySprints = new ArrayList<>();
        for (SprintEntity sprintEntity : sprintEntities) {
            BurnDownBySprint burnDownBySprint = new BurnDownBySprint();
            if(sprintEntity.getStartDate() == null) continue;
            burnDownBySprint.setSprintCreatedAt( sprintEntity.getStartDate().toEpochMilli());
            Long point = 0L;
            List<SprintUserStoryEntity> sprintUserStoryEntities = sprintUserStoryMap.get(sprintEntity.getId());
            if(sprintUserStoryEntities == null) {
                burnDownBySprint.setTotalPoint(point);
                burnDownBySprints.add(burnDownBySprint);
                continue;
            }
            for (SprintUserStoryEntity sprintUserStoryEntity : sprintUserStoryEntities) {
                UserStoryEntity userStoryEntity = uuidUserStoryEntityMap.get(sprintUserStoryEntity.getUserStoryId());
                if(userStoryEntity != null && userStoryEntity.getStatus().equals(UserStoryStatus.Completed)) point += userStoryEntity.getUserStoryPoint();
            }
            burnDownBySprint.setTotalPoint(point);
            burnDownBySprints.add(burnDownBySprint);
        }
        BurnDownChartResponse burnDownChartResponse = new BurnDownChartResponse();
        burnDownChartResponse.setTotalPoint(totalPoint);
        burnDownBySprints.sort(Comparator.comparing(BurnDownBySprint::getSprintCreatedAt));
        burnDownChartResponse.setSprints(burnDownBySprints);
        return burnDownChartResponse;
    }
}
