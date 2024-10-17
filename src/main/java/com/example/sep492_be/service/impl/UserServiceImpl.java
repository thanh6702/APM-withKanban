package com.example.sep492_be.service.impl;

import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.*;
import com.example.sep492_be.dto.response.util.PageResponse;
import com.example.sep492_be.dto.response.util.Paging;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.entity.DepartmentEntity;
import com.example.sep492_be.entity.RoleEntity;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.configuration.JwtTokenUtil;
import com.example.sep492_be.dto.response.auth.AuthResponse;
import com.example.sep492_be.entity.UserEntity;
import com.example.sep492_be.enums.RoleEnum;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.DepartmentRepository;
import com.example.sep492_be.repository.RoleRepository;
import com.example.sep492_be.repository.UserRepository;
import com.example.sep492_be.service.EmailService;
import com.example.sep492_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final JwtTokenUtil jwtUtil;
    private final EmailService emailService;
    @Override
    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByUserName(request.getUsername().trim());
        if(user == null)   throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
        if(user.getIsActive() != null && !user.getIsActive())    throw new ResponseException(InvalidInputError.USER_INACTIVE.getMessage(), InvalidInputError.USER_INACTIVE);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUserName());
            return new AuthResponse(token);
        } else {
            throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
        }
    }

    @Override
    public void register(RegisterRequest request) {
        UserEntity userEntity = userRepository.findByUserName(request.getUsername());
        if(userEntity != null){
            throw new ResponseException(InvalidInputError.USERNAME_DUPLICATED.getMessage(), InvalidInputError.USERNAME_DUPLICATED);
        }
        if(request.getRoles() == null){
            throw new ResponseException(InvalidInputError.ROLES_NOT_EMPLOY.getMessage(), InvalidInputError.ROLES_NOT_EMPLOY);
        }
        DepartmentEntity departmentEntity = departmentRepository.findById(request.getDepartmentId()).get();
        List<String> roles = Collections.singletonList(request.getRoles());
        Set<RoleEntity> roleEntities = roleRepository.findByNameIn(roles);
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .userName(request.getUsername())
                .firstName(request.getFirstName())
                .email(request.getUsername())
                .isActive(true)
                .lastName(request.getLastName())
                .dob(request.getDob() == null ? Instant.now() : new Date(request.getDob()).toInstant())
                .phone(request.getPhoneNumber())
                .roles(roleEntities)
                .departmentEntity(departmentEntity)
                .department_id(request.getDepartmentId())
                .isDirector(request.getIsDirector())
                .gender(request.getGender())
                .collaborationStartDate(request.getCollaborationDate() != null ? Instant.ofEpochMilli(request.getCollaborationDate()) : null)
                .build();
        String password = generateSecureRandomPassword(6);
        user.setPassword(passwordEncoder.encode(password));
        sendEmailWithPassword(request.getUsername(), password );
        userRepository.save(user);
    }
    private void sendEmailWithPassword(String email, String password) {
        String subject = "Your Account Password";
        String body = "Dear User, \n  Welcome you to Agile Project Management Using Kanban system\n\nYour account has been created successfully. Here is your password: " + password + "\n\nPlease change your password after logging in.";
        emailService.sendSimpleEmail(email, subject, body);
    }
    private String generateSecureRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
    @Override
    @Transactional
    public UserResponse changePassword(ChangePasswordRequest request) {
        UserEntity userEntity = userRepository.findByUserName(request.getUsername());
        if(userEntity != null){
            if(passwordEncoder.matches(request.getOldPassword(), userEntity.getPassword())){
                userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
                return new UserResponse(userEntity);
            }
            else{
                throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
            }
        }
        throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public UserProfile getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = ((UserEntity) authentication.getPrincipal()).getUserName();
            UserEntity userEntity = findByUsername(username);
            if (userEntity == null) {
                throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
            }
            return new UserProfile(userEntity);
        }
        throw new ResponseException(InvalidInputError.INVALID_USERNAME_PASSWORD.getMessage(), InvalidInputError.INVALID_USERNAME_PASSWORD);
    }

    @Override
    public PageResponse<UserResponse> getUserProfilePage(UserSearchRequest request, PageRequestFilter pageRequestFilter) {
        pageRequestFilter.setSort(List.of("created_at"));
        Pageable pageable = PageRequestFilter.converToPageable(pageRequestFilter);
        UserProfile userProfile = getProfile();
        Page<UserEntity> userEntities = userRepository.getListUser(request.getSearchField(), request.getTab().name(), userProfile.getId(), userProfile.getDepartmentId(), pageable);
        List<UserResponse> contents = userEntities.getContent().stream().map(UserResponse::new).toList();
        return PageResponse.<UserResponse>builder()
                .paging(Paging.builder()
                        .pageIndex(userEntities.getNumber())
                        .pageSize(userEntities.getSize())
                        .totalCount(userEntities.getTotalElements())
                        .build())
                .data(contents)
                .build();
    }

    @Override
    public List<BaseResponse> searchUser(UserSearchRequest searchRequest) {
        if(searchRequest.getSearchField() == null || searchRequest.getSearchField().isBlank()) searchRequest.setSearchField(null);
        List<UserEntity> userEntities = userRepository.searchUser(searchRequest.getSearchField() == null || searchRequest.getSearchField().isBlank() ? null  : searchRequest.getSearchField(), searchRequest.getProjectId(), searchRequest.getIsManager());
        return userEntities.stream().map(BaseResponse::new).toList();
    }

    @Override
    public UserResponse updateUser(UUID id, RegisterRequest request) {
        UserEntity userEntity = userRepository.findByIdNativeQuery(id);
        if(request.getGender() != null) userEntity.setGender(request.getGender());
        if(request.getDepartmentId() != null){
            DepartmentEntity departmentEntity = departmentRepository.findById(request.getDepartmentId()).orElse(null);
            if(departmentEntity != null) {
                userEntity.setDepartmentEntity(departmentEntity);
                userEntity.setDepartment_id(departmentEntity.getId());
            }
        }
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        if(request.getCollaborationDate() != null) {
            userEntity.setCollaborationStartDate(Instant.ofEpochMilli(request.getCollaborationDate()));
        }
        if(request.getDob() != null){
            userEntity.setDob(Instant.ofEpochMilli(request.getDob()));
        }
        if(request.getPhoneNumber() != null) userEntity.setPhone(request.getPhoneNumber());
        if(request.getGender() != null) userEntity.setGender(request.getGender());
        if(request.getRoles() != null){
            Set<RoleEntity> roleEntity = roleRepository.findByNameIn(Collections.singletonList(request.getRoles()));
            if(roleEntity != null && !roleEntity.isEmpty()) userEntity.setRoles(roleEntity);
        }
        return new UserResponse(userEntity);
    }
}
