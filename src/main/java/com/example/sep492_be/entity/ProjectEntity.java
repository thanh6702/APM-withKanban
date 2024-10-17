package com.example.sep492_be.entity;

import com.example.sep492_be.dto.request.ProjectRequest;
import com.example.sep492_be.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import jakarta.persistence.Column;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "project")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class ProjectEntity  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @Column(name = "shorted_name")
    private String shortedName;
    @ManyToOne
    @JoinColumn(name = "department_id", insertable = false, nullable = false, updatable = false)
    private DepartmentEntity departmentEntity;
    @Column(name = "department_id")
    private UUID department_id;

    @ManyToOne
    @JoinColumn(name = "project_manager_id")
    private UserEntity projectManager;

    @Column(name = "sprint_time")
    private Long sprintTime;
    public ProjectEntity(ProjectRequest request){
        this.name = request.getName();
        this.description = request.getDescription();
        this.status = ProjectStatus.NEW;
        this.shortedName = request.getShortedName();
        this.department_id = request.getDepartmentId();
        this.sprintTime = request.getSprintTime();
    }

}
