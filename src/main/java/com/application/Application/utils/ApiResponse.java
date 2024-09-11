package com.application.Application.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T body;

    public static <T> ApiResponse<T> success(T body, String message) {
        return new ApiResponse<>(HttpStatus.SC_OK, message, body);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, T body) {
        return new ApiResponse<>(statusCode, message, body);
    }
}
