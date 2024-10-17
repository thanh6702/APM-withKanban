package com.example.sep492_be.controller;

import com.example.sep492_be.dto.request.PageRequestFilter;
import com.example.sep492_be.dto.request.RegisterRequest;
import com.example.sep492_be.dto.request.UserSearchRequest;
import com.example.sep492_be.dto.request.UserStatusUpdate;
import com.example.sep492_be.dto.response.BaseResponse;
import com.example.sep492_be.dto.response.UserProfile;
import com.example.sep492_be.dto.response.UserResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.entity.UserEntity;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.UserRepository;
import com.example.sep492_be.service.UserService;
import lombok.*;
import org.apache.catalina.Service;
import org.apache.catalina.User;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Setter
@Getter
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ServiceResponse<UserProfile> getUserProfile() {
        UserProfile userProfile = userService.getProfile();
        return ServiceResponse.succeed(HttpStatus.OK, userProfile);
    }

    @GetMapping("/staff")
    public PageResponse<UserResponse> getUserProfilePage(UserSearchRequest request, PageRequestFilter pageRequestFilter) {
            return userService.getUserProfilePage(request, pageRequestFilter);
    }

    @PutMapping("/{id}/update-status")
    @Transactional
    public ServiceResponse<UserResponse> updateStatusUser(@PathVariable UUID id, @RequestBody UserStatusUpdate request) {
        UserEntity userEntity = userRepository. findByIdNativeQuery(id);
        if(userEntity == null) throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
        userEntity.setIsActive(request.getIsActive());
        return ServiceResponse.succeed(HttpStatus.OK, new UserResponse(userEntity));
    }

    @GetMapping("/search")
    public List<BaseResponse> searchUser(UserSearchRequest request, PageRequestFilter pageRequestFilter) {
        return userService.searchUser(request);
    }

    @PutMapping("/{id}")
    @Transactional
    public ServiceResponse<UserResponse> updateUser(@PathVariable UUID id, @RequestBody  RegisterRequest request){
       return ServiceResponse.succeed(HttpStatus.OK, userService.updateUser(id, request));
    }
}
