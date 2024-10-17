package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseUpdateStatusRequest {
    private ReleaseStatus status;
}
