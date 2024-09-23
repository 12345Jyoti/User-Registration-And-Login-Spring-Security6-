package com.auth.AuthImpl.utils;
import org.apache.http.HttpStatus;

public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T body;


    public ApiResponse(){}

    public ApiResponse(int statusCode, String message, T body) {
        this.statusCode = statusCode;
        this.message = message;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static <T> ApiResponse<T> success(T body, String message) {
        return new ApiResponse<>(HttpStatus.SC_OK, message, body);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, T body) {
        return new ApiResponse<>(statusCode, message, body);
    }
}
