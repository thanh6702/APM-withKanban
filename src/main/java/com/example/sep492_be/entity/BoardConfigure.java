package com.example.sep492_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "board_configure")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted <> true")
public class BoardConfigure extends BaseEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @OneToMany
    private List<ColumnEntity> columns;

    @OneToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
}
