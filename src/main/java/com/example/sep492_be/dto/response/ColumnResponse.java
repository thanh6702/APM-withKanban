package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.ColumnEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnResponse {
    private UUID id;
    private String name;
    private Long min;
    private Long max;
    private Long order;

    public ColumnResponse(ColumnEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.min = entity.getMin();
        this.max = entity.getMax();
        this.order = entity.getOrder();
    }
}
