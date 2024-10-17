package com.example.sep492_be.service;

public interface PasswordResetService {
    void initiatePasswordReset(String email);
    void resetPassword(String email, String code, String newPassword);
}
