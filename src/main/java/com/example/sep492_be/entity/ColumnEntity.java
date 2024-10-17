package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.ColumnRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "column_table")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class ColumnEntity  extends BaseEntity{
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
    @Column(name = "min_workitem")
    private Long min;
    @Column(name = "max_workitem")
    private Long max;
    @Column(name = "index_column")
    private Long order;

    public ColumnEntity(String name, Long order){
        this.name = name;
        this.min = 0L;
        this.max = null;
        this.order = order;
    }

    public ColumnEntity(ColumnRequest request){
        this.name = request.getName();
        this.min = request.getMin();
        this.max = request.getMax();
    }
}
