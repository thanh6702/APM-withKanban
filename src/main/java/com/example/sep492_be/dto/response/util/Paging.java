package com.example.sep492_be.dto.response.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paging {
    private long totalCount;
    private int pageIndex;
    private int pageSize;
    private int totalPages;
}
