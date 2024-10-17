package com.example.sep492_be.repository;

import com.example.sep492_be.entity.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, UUID> {
    List<ProjectMemberEntity> findAllByProjectIdIn(List<UUID> projectIds);
    void deleteByProjectEntityId(UUID projectId);
}
