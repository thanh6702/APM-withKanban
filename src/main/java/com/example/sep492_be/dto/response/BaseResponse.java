package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private UUID id;
    private String code;
    private String name;
    private String phoneNumber;
    private String shortedName;
    public BaseResponse(UserEntity userEntity){
        this.id = userEntity.getId();
        this.name = userEntity.getFirstName() + " " + userEntity.getLastName();
        this.shortedName = userEntity.getShortedName();
        this.phoneNumber = userEntity.getPhone() != null && !userEntity.getPhone().isBlank() ? userEntity.getPhone()  : "09192939123";
    }
}
