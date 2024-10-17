package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.WorkLogRequest;
import com.example.sep492_be.dto.response.WorkLogDetailResponse;
import com.example.sep492_be.dto.response.WorkLogResponse;

import java.util.List;
import java.util.UUID;

public interface WorkLogService {
    void createWorkLog(WorkLogRequest request);
    void updateWorkLog(UUID id, WorkLogRequest request);
    void deleteWorkLog(UUID id);
    WorkLogResponse findWorkLogs(UUID taskId);
}
