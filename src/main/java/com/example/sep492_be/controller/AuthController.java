package com.example.sep492_be.controller;

import com.example.sep492_be.configuration.JwtTokenUtil;
import com.example.sep492_be.dto.request.*;
import com.example.sep492_be.dto.response.UserResponse;
import com.example.sep492_be.dto.response.auth.AuthResponse;
import com.example.sep492_be.dto.response.util.ServiceResponse;
import com.example.sep492_be.service.PasswordResetService;
import com.example.sep492_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ServiceResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(request.getEmail());
        return ServiceResponse.succeed(HttpStatus.OK, "Generate token successfully");
    }

    @PostMapping("/reset-password")
    public ServiceResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ServiceResponse.succeed(HttpStatus.OK, "Reset password successfully");

    }
    @PostMapping("/login")
    public ServiceResponse<AuthResponse> login(@RequestBody AuthRequest request){
        return ServiceResponse.succeed(HttpStatus.OK, userService.login(request));
    }

    @PostMapping("/change-password")
    public ServiceResponse<UserResponse> changePassword(@RequestBody ChangePasswordRequest request){
        return ServiceResponse.succeed(HttpStatus.OK, userService.changePassword(request));
    }

    @PostMapping("/create-account")
    public ServiceResponse<Void> register(@RequestBody RegisterRequest request){
        userService.register(request);
        return ServiceResponse.succeed(HttpStatus.OK, null);
    }
}
