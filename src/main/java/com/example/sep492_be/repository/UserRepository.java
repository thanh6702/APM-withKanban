package com.example.sep492_be.repository;

import com.example.sep492_be.entity.UserEntity;
import com.example.sep492_be.enums.StaffTabEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query(value = "SELECT * FROM  user x where x.userName = :username ", nativeQuery = true)
    UserEntity findByUserName(String username);
    List<UserEntity> findByIdIn(List<UUID> ids);
    @Query(value = "SELECT DISTINCT x.* " +
            "FROM user x " +
            "LEFT JOIN project_member pm ON x.id = pm.user_id " +
            "LEFT JOIN project p ON p.id = pm.project_id " +
            "WHERE (:searchTerm IS NULL OR CONCAT(x.first_name, ' ', x.last_name) LIKE CONCAT('%', :searchTerm, '%') " +
            "       OR x.shorted_name LIKE CONCAT('%', :searchTerm, '%')) " +
            "AND ((:tab = 'MY_PROJECT' AND p.project_manager_id = :userId) " +
            "     OR (:tab = 'MY_DEPARTMENT' AND x.department_id = :departmentId) " +
            "     OR :tab = 'ALL') ",
            countQuery = "SELECT COUNT(DISTINCT x.id) " +
                    "FROM user x " +
                    "LEFT JOIN project_member pm ON x.id = pm.user_id " +
                    "LEFT JOIN project p ON p.id = pm.project_id " +
                    "WHERE (:searchTerm IS NULL OR CONCAT(x.first_name, ' ', x.last_name) LIKE CONCAT('%', :searchTerm, '%') " +
                    "       OR x.shorted_name LIKE CONCAT('%', :searchTerm, '%')) " +
                    "AND ((:tab = 'MY_PROJECT' AND p.project_manager_id = :userId) " +
                    "     OR (:tab = 'MY_DEPARTMENT' AND x.department_id = :departmentId) " +
                    "     OR :tab = 'ALL') ",
            nativeQuery = true)
    Page<UserEntity> getListUser(String searchTerm, String tab, UUID userId, UUID departmentId, Pageable pageable);

    @Query(value = "SELECT * FROM user x where x.id = :id ", nativeQuery = true)
    UserEntity  findByIdNativeQuery(UUID id);

    @Query("select distinct x from UserEntity x" +
            " left join x.roles r " +
            " left join ProjectMemberEntity pm on x.id = pm.userId " +
            " left join ProjectEntity p  on p.id = pm.projectId " +
            " where (:searchTerm is null or concat(x.firstName, '', x.lastName) like concat('%', :searchTerm, '%') or x.shortedName like concat('%', :searchTerm, '%'))" +
            " and (:projectId is null or p.id = :projectId )" +
            " and ((:isManager = false and r.name = 'EMPLOYEE')" +
            " or (:isManager = TRUE and r.name = 'PM'))"
    )
    List<UserEntity> searchUser(String searchTerm, UUID projectId, Boolean isManager);

}
