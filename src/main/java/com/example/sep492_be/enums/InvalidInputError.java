package com.example.sep492_be.enums;

import com.example.sep492_be.exception.ResponseError;

public enum InvalidInputError implements ResponseError {
    INVALID_USERNAME_PASSWORD(4000000, "Username or password is wrong."),
    USER_INACTIVE(4000003, "User have been inactived."),
    USER_STORY_INVALID(4000011, "User story can not be empty."),
    STATUS_INVALID(4000012, "Status is invalid."),
    PROJECT_CODE_DUPLICATE(4000013, "Project code or project name is duplicated."),
    COMMENT_NOT_NULL_OR_EMPTY(4000014, "Comment cannot be null or emtpy."),
    RELEASE_NAME_CANNOT_NULL(4000015, "Release name can not be null"),
    RELEASE_DATE(40000016, "Release date is invalid"),
    TASK_NAME_NOT_NULL(4000017, "Task name is invalid"),
    TASK_DATE(4000018, "Task date is invalid"),
    TASK_USER_STORY_NOTNULL(4000018, "user story can not be empty."),
    ROLE_CANNOT_BE_NULL(4000018, "role can not be empty."),

    USERNAME_DUPLICATED(40000001, "Email have been existed in the system."),
    ROLES_NOT_EMPLOY(40000002, "Roles can not empty"),
    PROJECT_NOT_FOUND(40000003, "Project Not found"),
    RELEASE_STATUS_INVALID(40000004, "The release is unable to be in progress."),
    SPRINT_STATUS_INVALID(40000004, "The sprint is unable to be in progress."),
    WIP_INVALID(40000004, "The work is over work limit."),

    RELEASE_UNABLE_TOCOMPLETE(40000005, "Sprints need to become completed before completing this release."),
    SPRINT_UNABLE_TOCOMPLETE(40000005, "User stories need to become completed or removed before completing this sprint."),
    SPRINT_DATE_INVALID(40000006, "Sprints date is invalid."),
    USER_NOT_FOUND(40000006, "User not found."),
    TOKEN_CODE_INVALID(40000007, "Reset code is invalid"),
    WORKLOG_STARTDATE_INVALID(40000007, "Worklog start date is invalid"),
    WORKLOG_ENDDATE_INVALID(40000007, "Worklog end date is invalid"),
    WORKLOG_DATE_INVALID(40000007, "Worklog date is invalid"),

    ;

    private final Integer code;
    private final String message;

    InvalidInputError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
