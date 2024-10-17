package com.example.sep492_be.service;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String subject, String body);
}
