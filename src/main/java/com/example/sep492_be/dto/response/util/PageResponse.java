package com.example.sep492_be.dto.response.util;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int code;
    private String message;
    private Paging paging;
    private List<T> data;

    public static <T> PageResponse<T> succeed(HttpStatus status, PageResponse data) {
        data.setCode(status.value());
        return data;
    }
}