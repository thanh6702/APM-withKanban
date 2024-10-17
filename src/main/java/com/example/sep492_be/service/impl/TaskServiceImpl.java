package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.AddCommentRequest;
import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.TaskRequest;
import com.example.sep492_be.dto.request.TaskSearchRequest;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.entity.*;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.*;
import com.example.sep492_be.service.TaskHistoryService;
import com.example.sep492_be.service.TaskService;
import com.example.sep492_be.service.UserService;
import com.example.sep492_be.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.events.CommentEvent;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;
    private final TaskHistoryService taskHistoryService;
    @Override
    @Transactional
    public void create(TaskRequest request) {
        TaskEntity entity = new TaskEntity(request);
        if(request.getName() == null || request.getName().isEmpty())
            throw new ResponseException(InvalidInputError.TASK_NAME_NOT_NULL.getMessage(),InvalidInputError.TASK_NAME_NOT_NULL);
        if(request.getEstimatedEndDate() == null || request.getStartDate() == null || request.getEstimatedEndDate() < request.getStartDate())
            throw new ResponseException(InvalidInputError.TASK_DATE.getMessage(),InvalidInputError.TASK_DATE);
        if(request.getUserStoryId() == null)
            throw new ResponseException(InvalidInputError.USER_STORY_INVALID.getMessage(),InvalidInputError.USER_STORY_INVALID);
        UserProfile userProfile = userService.getProfile();
        entity.setCreatedBy(userProfile.getId());
        UserEntity user = userRepository.findById(request.getUserId()).orElse(null);
        entity.setAssignTo(user);
        if(request.getUserStoryId() == null) throw new ResponseException(InvalidInputError.USER_STORY_INVALID.getMessage(), InvalidInputError.USER_STORY_INVALID) ;
        UserStoryEntity userStoryEntity = userStoryRepository.findById(request.getUserStoryId()).orElse(null);
        if(userStoryEntity != null) entity.setUserStory(userStoryEntity);
        if(userStoryEntity != null && userStoryEntity.getProject() != null) {
            ColumnEntity columnEntity = projectRepository.findMinColumn(userStoryEntity.getProject().getId());
            entity.setColumnEntity(columnEntity);
            List<TaskEntity> taskEntities = taskRepository.getTaskByColumnId(request.getUserId(), columnEntity.getId(), userStoryEntity.getProject().getId());
            if(!taskEntities.isEmpty()
                    && columnEntity.getMax() != null
                    && taskEntities.size() >= columnEntity.getMax())
                throw new ResponseException(InvalidInputError.WIP_INVALID.getMessage(),InvalidInputError.WIP_INVALID );
        }
        entity.setCreatedBy(userProfile.getId());
        if(userStoryEntity != null && userStoryEntity.getProject() != null && userStoryEntity.getProject().getId() != null) {
            Long maxCode = taskRepository.getMaxCode(userStoryEntity.getProject().getId());
            entity.setCode(maxCode != null ? maxCode + 1 : 1L);
        }
        if(userStoryEntity != null && userStoryEntity.getProject() != null) entity.setProject(userStoryEntity.getProject());
        entity = taskRepository.save(entity);
        taskHistoryService.createTaskHistory("Đã tạo công việc", entity, userProfile.getId());

    }
    private void enrichCreatedBy(List<TaskResponse> responses){
        List<UUID> ids = responses.stream().map(TaskResponse::getCreatedById).toList();
        Map<UUID, UserEntity> userEntityMap = userRepository.findByIdIn(ids).stream().collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
        responses.forEach(taskResponse -> {
            UserEntity userEntity = userEntityMap.get(taskResponse.getCreatedById());
            if(userEntity != null) taskResponse.setCreatedBy(new UserResponse(userEntity));
        });
    }

    @Override
    public PageResponse<TaskResponse> getTasks(TaskSearchRequest request, PageRequestFilter filter) {
        request.setStartDate(DateUtils.convertToStartOfDayInMillis(request.getStartDate()));
        request.setEndDate(DateUtils.convertToEndOfDayInMillis(request.getEndDate()));
        Pageable pageable = PageRequestFilter.converToPageable(filter);
        Page<TaskEntity> taskEntities = taskRepository.getTasksByProjectIdAndUserId(
                request.getSearchTerm() != null && !request.getSearchTerm().isEmpty() ? request.getSearchTerm() : null,
                request.getProjectId(),
                request.getUserId(),
                request.getPriority(),
                request.getStatusId(),
                request.getStartDate() != null ? Instant.ofEpochMilli(request.getStartDate()) : null,
                request.getEndDate() != null ? Instant.ofEpochMilli(request.getEndDate()) : null,
                request.getReleaseId(),
                request.getSprintId(),
                request.getUserStoryId(),
                pageable);
        List<TaskResponse> taskResponses = taskEntities.getContent().stream().map(TaskResponse::new).collect(Collectors.toList());
        enrichCreatedBy(taskResponses);
        return PageResponse.<TaskResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(taskEntities.getNumber())
                        .pageSize(taskEntities.getSize())
                        .totalCount(taskEntities.getTotalElements())
                        .build())
                .data(taskResponses)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TaskEntity task = taskRepository.findById(id).orElse(null);
        if(task == null) return;
        task.setDeleted(true);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void update(UUID id, TaskRequest request) {
        TaskEntity task = taskRepository.findById(id).orElse(null);
        if(request.getName() == null || request.getName().isEmpty())
            throw new ResponseException(InvalidInputError.TASK_NAME_NOT_NULL.getMessage(),InvalidInputError.TASK_NAME_NOT_NULL);
        if(request.getStartDate() == null || request.getEstimatedEndDate() == null || request.getEstimatedEndDate() < request.getStartDate())
            throw new ResponseException(InvalidInputError.TASK_DATE.getMessage(),InvalidInputError.TASK_DATE);
        if(request.getUserStoryId() == null)
            throw new ResponseException(InvalidInputError.USER_STORY_INVALID.getMessage(),InvalidInputError.USER_STORY_INVALID);
        if(task == null) return;
        task.setName(request.getName());
        if(request.getDescription() != null) task.setDescription(request.getDescription());
        if(request.getStartDate() != null) task.setStartDate(Instant.ofEpochMilli(request.getStartDate()));
        if(request.getEstimatedEndDate() != null) task.setEstimatedEndDate(Instant.ofEpochMilli(request.getEstimatedEndDate()));
        if(request.getPriorityLevel() != null) task.setPriority(request.getPriorityLevel());
        if(request.getUserId() != null) {
            Optional<UserEntity> user = userRepository.findById(request.getUserId());
            user.ifPresent(task::setAssignTo);
        }
        if(request.getDescription() != null) {
           task.setDescription(request.getDescription());
        }
        UserProfile userProfile = userService.getProfile();
        taskHistoryService.createTaskHistory("Đã cập nhật công việc", task, userProfile.getId());

    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        TaskEntity task = taskRepository.findById(id).orElse(null);
        if(task == null) return null;
        TaskResponse taskResponse = new TaskResponse(task);
        List<Comment> comment = commentRepository.findAllByTaskId(taskResponse.getId());
        List<CommentResponse> commentResponse = comment.stream().map(CommentResponse::new).toList();
        taskResponse.setComments(commentResponse);
        enrichCreatedBy(Collections.singletonList(taskResponse));
        return taskResponse;
    }

    @Override
    @Transactional
    public CommentResponse addComment(UUID id, AddCommentRequest request) {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        TaskEntity task = taskRepository.findById(id).orElse(null);
        if(task == null) return null;
        if(request.getComment() == null || request.getComment().isEmpty())
            throw new ResponseException(InvalidInputError.COMMENT_NOT_NULL_OR_EMPTY.getMessage(), InvalidInputError.COMMENT_NOT_NULL_OR_EMPTY);
        comment.setTask(task);
        UserProfile userResponse = userService.getProfile();
        UserEntity user = userRepository.findById(userResponse.getId()).orElse(null);
        if(user == null) return null;
        comment.setUser(user);
        comment.setContent(request.getComment());
        return new CommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void updateComment(UUID id, AddCommentRequest request) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if(comment == null) return;
        if(request.getComment() == null || request.getComment().isEmpty())
            throw new ResponseException(InvalidInputError.COMMENT_NOT_NULL_OR_EMPTY.getMessage(), InvalidInputError.COMMENT_NOT_NULL_OR_EMPTY);
        comment.setContent(request.getComment());
    }

    @Override
    public List<ColumnResponse> getListColumns(UUID taskId) {
        TaskEntity task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return null;
        ProjectEntity project = projectRepository.findById(task.getUserStory().getProject().getId()).orElse(null);
        if(project == null) return null;
        List<ColumnEntity> columnEntities = columnRepository.findByProjectId(project.getId());

        return columnEntities.stream().map(ColumnResponse::new).toList();
    }

    @Override
    public List<TaskResponse> getTaskByColumnId(UUID columnId, UUID userId) {
        List<TaskEntity> task = taskRepository.getTasksByColumnEntityId(columnId, userId);
        List<UUID> taskIds = task.stream().map(TaskEntity::getId).toList();
        Map<UUID, List<Comment>> comments = commentRepository.findAllByListTaskId(taskIds).stream().collect(Collectors.groupingBy(Comment::getTaskId));
        List<TaskResponse> taskResponses = task.stream().map(TaskResponse::new).toList();
        taskResponses.forEach(taskEntity -> {
            List<Comment> comments1 = comments.get(taskEntity.getId());
            if(comments1 == null) return;
            List<CommentResponse> commentResponses = comments1.stream().map(CommentResponse::new).toList();
            taskEntity.setComments(commentResponses);
        });
        enrichCreatedBy(taskResponses);
        return taskResponses;
    }

    @Override
    @Transactional
    public void updateTaskStatus(UUID taskId, UUID columnId) {
        TaskEntity task = taskRepository.findById(taskId).orElse(null);
        if(task == null) return;
        ColumnEntity column = columnRepository.findById(columnId).orElse(null);
        if(column == null) throw new ResponseException(InvalidInputError.STATUS_INVALID.getMessage(),InvalidInputError.STATUS_INVALID);
        List<TaskEntity> taskEntities = taskRepository.getTaskByColumnId(task.getAssignId(), columnId, task.getProject().getId());
        if(!taskEntities.isEmpty()
                && column.getMax() != null
                && taskEntities.size() >= column.getMax())
            throw new ResponseException(InvalidInputError.WIP_INVALID.getMessage(),InvalidInputError.WIP_INVALID );
        task.setColumnEntity(column);

        if(column.getOrder() == null) return;
        if(column.getOrder().equals(columnRepository.findMaxOrderById(task.getProject().getId()))) task.setEndDate(Instant.now());
        UserProfile userProfile = userService.getProfile();
        taskHistoryService.createTaskHistory("Đã cập nhật trạng thái công việc thành " + column.getName(), task, userProfile.getId());
    }
}
