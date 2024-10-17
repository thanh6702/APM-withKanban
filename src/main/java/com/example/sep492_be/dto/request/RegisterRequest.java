package com.example.sep492_be.dto.request;

import com.example.sep492_be.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Long dob;
    private String roles;
    private Boolean isDirector = false;
    private UUID departmentId;
    private Long collaborationDate;
    private Boolean gender;

}
