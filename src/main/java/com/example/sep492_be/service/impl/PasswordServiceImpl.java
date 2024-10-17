package com.example.sep492_be.service.impl;

import com.example.sep492_be.entity.PassWordResetEntity;
import com.example.sep492_be.entity.UserEntity;
import com.example.sep492_be.enums.InvalidInputError;
import com.example.sep492_be.exception.ResponseException;
import com.example.sep492_be.repository.PasswordResetCodeRepository;
import com.example.sep492_be.repository.UserRepository;
import com.example.sep492_be.service.EmailService;
import com.example.sep492_be.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordServiceImpl implements PasswordResetService {
    @Autowired
    private PasswordResetCodeRepository resetCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void initiatePasswordReset(String email) {
        UserEntity user = userRepository.findByUserName(email);
        if(user == null) throw new ResponseException(InvalidInputError.USER_NOT_FOUND.getMessage(), InvalidInputError.USER_NOT_FOUND);

        // Generate a random 6-digit reset code
        String resetCode = String.format("%06d", new Random().nextInt(999999));
        resetCodeRepository.deleteByUserId(user.getId());
        PassWordResetEntity resetCodeEntity = PassWordResetEntity.builder()
                .id(UUID.randomUUID())
                .code(resetCode)
                .userId(user.getId())
                .expiryDate(Instant.now().plusSeconds(300)) // Code valid for 1 hour
                .build();
        resetCodeRepository.save(resetCodeEntity);

        String message = "Your password reset code is: " + resetCode + "\nThis code is valid for 5 minutes.";
        emailService.sendSimpleEmail(email, "Password Reset Code", message);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        UserEntity user = userRepository.findByUserName(email);
        if(user == null) throw new ResponseException(InvalidInputError.USER_NOT_FOUND.getMessage(), InvalidInputError.USER_NOT_FOUND);


        PassWordResetEntity resetCode = resetCodeRepository.findByCodeAndUserId(code, user.getId());
        if(resetCode == null) throw new ResponseException(InvalidInputError.TOKEN_CODE_INVALID.getMessage(), InvalidInputError.TOKEN_CODE_INVALID);
        if (resetCode.isExpired()) {
            throw new ResponseException(InvalidInputError.TOKEN_CODE_INVALID.getMessage(), InvalidInputError.TOKEN_CODE_INVALID);        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetCodeRepository.deleteByUserId(user.getId());
    }
}
