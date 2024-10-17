package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String firstName;
    private String lastName;
    private Boolean gender;
    private String email;
    private String phone;
    private Long collaborationDate;
    private String departmentName;
    private Boolean isActive;
    private String role;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.name = userEntity.getFirstName() + " " + userEntity.getLastName();
        this.gender = userEntity.getGender();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.email = userEntity.getEmail();
        this.phone = userEntity.getPhone();
        this.collaborationDate = userEntity.getCollaborationStartDate() != null ? userEntity.getCollaborationStartDate().toEpochMilli() : null;
        this.departmentName = userEntity.getDepartmentEntity() != null && userEntity.getDepartmentEntity().getName()!= null ? userEntity.getDepartmentEntity().getName() : null;
        this.isActive = userEntity.getIsActive() == null || userEntity.getIsActive();
        String role = "";
        if( userEntity.getRoles() != null && !userEntity.getRoles().isEmpty() && userEntity.getRoles().stream().iterator().next()!= null){
            role = userEntity.getRoles().stream().iterator().next().getName().name();
        }
        this.role = role;
    }
}
