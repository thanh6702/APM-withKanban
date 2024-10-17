package com.example.sep492_be.repository;

import com.example.sep492_be.entity.PassWordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetCodeRepository extends JpaRepository<PassWordResetEntity, UUID> {
    PassWordResetEntity findByCodeAndUserId(String code, UUID userId);
    void deleteByUserId(UUID userId);
}
