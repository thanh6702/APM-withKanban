package com.example.sep492_be.dto.response.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ServiceResponse<T> succeed(HttpStatus status, T datta) {
        return new ServiceResponse<>(status.value(), null, datta);
    }
}
