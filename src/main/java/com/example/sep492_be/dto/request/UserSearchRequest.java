package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.StaffTabEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private String searchField;
    private StaffTabEnum tab;
    private UUID projectId;
    private Boolean isManager = false;
}
