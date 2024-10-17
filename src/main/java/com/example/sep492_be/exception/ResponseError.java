package com.example.sep492_be.exception;

public interface ResponseError {
    String getName();

    String getMessage();

    default Integer getCode() {
        return 0;
    }
}
