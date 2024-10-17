package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.WorkLogRequest;
import com.example.sep492_be.dto.response.WorkLogResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.WorkLogService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/work-log")
@Setter
@Getter
@RequiredArgsConstructor
public class WorkLogController {
    private final WorkLogService workLogService;

    @PostMapping
    ServiceResponse<Void> createWorkLog(@RequestBody WorkLogRequest workLogRequest) {
        workLogService.createWorkLog(workLogRequest);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @PutMapping("/{id}")
    ServiceResponse<Void> updateWorkLog(@PathVariable UUID id, @RequestBody WorkLogRequest workLogRequest) {
        workLogService.updateWorkLog(id, workLogRequest);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }

    @DeleteMapping("/{id}")
    ServiceResponse<Void> deleteWorkLog(@PathVariable UUID id) {
        workLogService.deleteWorkLog(id);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
    @GetMapping("/{id}")
    ServiceResponse<WorkLogResponse> getWorkLog(@PathVariable UUID id) {
        return ServiceResponse.succeed(HttpStatus.OK, workLogService.findWorkLogs(id));
    }
}
