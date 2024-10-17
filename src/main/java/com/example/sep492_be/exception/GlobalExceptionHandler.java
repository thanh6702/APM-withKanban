package com.example.sep492_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import  com.example.sep492_be.exception.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<ErrorResponse> handleResponseException(ResponseException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getError().getCode()) // Giả sử `getError` trả về một enum hoặc đối tượng có phương thức `getCode`
                .message(ex.getMessage())
                .data(ex.getData())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Sử dụng HTTP status phù hợp
    }
}
