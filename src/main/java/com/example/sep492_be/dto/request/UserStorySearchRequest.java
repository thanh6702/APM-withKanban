package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.UserStoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStorySearchRequest {
    private UUID projectId;
    private String searchTerm;
    private List<UserStoryStatus> status;
    private UUID sprintId;
    private Long priority;
    private Long priorityLevel;

}
