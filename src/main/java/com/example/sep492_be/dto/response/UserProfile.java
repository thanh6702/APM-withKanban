package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.RoleEntity;
import com.example.sep492_be.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private UUID id = UUID.randomUUID();
    private String userName;
    private String firstName;
    private String lastName;
    private String phone;
    private Long dob;;
    private String email;
    private String shortedName;
    private List<BaseResponse> roles;
    private UUID departmentId;

    public UserProfile(UserEntity userEntity){
        this.id = userEntity.getId();
        this.userName = userEntity.getUserName();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.phone = userEntity.getPhone();
        this.departmentId= userEntity.getDepartmentEntity() != null ? userEntity.getDepartmentEntity().getId() : null;
        this.dob = userEntity.getDob() != null ? userEntity.getDob().toEpochMilli() : null;
        List<BaseResponse> baseResponses = new ArrayList<>();
        for (RoleEntity role : userEntity.getRoles()) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setId(role.getId());
            baseResponse.setCode(role.getName().name());
            baseResponse.setName(role.getName().getValue());
            baseResponses.add(baseResponse);
        }
        this.roles = baseResponses;

        this.shortedName = userEntity.getShortedName();
        this.email = userEntity.getEmail();
    }
}
