package com.example.sep492_be.dto.response.auth;

import com.example.sep492_be.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthResponse {
    private UUID id;
    private String username;
    private String password;
    public UserAuthResponse(UserEntity entity){
        this.id = entity.getId();
        this.username  = entity.getUserName();
        this.password = entity.getPassword();
    }
}
