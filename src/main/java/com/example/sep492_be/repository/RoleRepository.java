package com.example.sep492_be.repository;

import com.example.sep492_be.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Set<RoleEntity> findByNameIn(List<String> names);

}
