package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.ProjectMemberEntity;
import com.example.sep492_be.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberResponse {
    private UUID id;
    private BaseResponse user;
    private Long startDate;
    private Long endDate;
    private String roleName;
    private Long assignedDate;

    public ProjectMemberResponse(ProjectMemberEntity entity){
        this.id = entity.getId();
        this.startDate = entity.getStartDate() != null ? entity.getStartDate().toEpochMilli() : null;
        this.endDate = entity.getEndDate() != null ? entity.getEndDate().toEpochMilli() : null;
        BaseResponse userResponse = new BaseResponse();
        UserEntity userEntity = entity.getUserEntity();
        if(userEntity != null){
            userResponse.setName(userEntity.getFirstName() + " " + userEntity.getLastName());
            userResponse.setShortedName(userEntity.getShortedName());
            userResponse.setId(userEntity.getId());
            userResponse.setPhoneNumber(userEntity.getPhone() != null ? userEntity.getPhone() : "09191231234");
        }
        this.user = userResponse;
        this.roleName = entity.getRoleName() != null ? entity.getRoleName() : "Scrum master";
        this.assignedDate = entity.getAssignedDate() != null ? entity.getAssignedDate().toEpochMilli() : Instant.now().toEpochMilli();
    }
}
