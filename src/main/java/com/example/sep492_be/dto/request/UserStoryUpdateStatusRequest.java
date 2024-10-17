package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.UserStoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryUpdateStatusRequest {
    private UserStoryStatus status;
}
