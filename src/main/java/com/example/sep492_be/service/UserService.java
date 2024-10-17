package com.example.sep492_be.service;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.BaseResponse;
import com.example.sep492_be.dto.response.UserProfile;
import com.example.sep492_be.dto.response.UserResponse;
import com.example.sep492_be.dto.response.auth.AuthResponse;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    AuthResponse login(AuthRequest request);
    UserResponse changePassword(ChangePasswordRequest request);
    void register(RegisterRequest request);
    UserEntity findByUsername(String username);
    UserProfile getProfile();
    PageResponse<UserResponse> getUserProfilePage(UserSearchRequest request, PageRequestFilter pageRequestFilter);
    List<BaseResponse> searchUser(UserSearchRequest searchRequest);
    UserResponse updateUser(UUID id, RegisterRequest request);
}
