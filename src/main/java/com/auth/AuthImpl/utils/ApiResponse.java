package com.auth.AuthImpl.utils;
import org.apache.http.HttpStatus;


public class ApiResponse<T,Y> {

    private int statusCode;
    private String message;
    private T body;
    private Y errorBody;


    //


    public Y getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(Y errorBody) {
        this.errorBody = errorBody;
    }

    public ApiResponse(){}

    public ApiResponse(int statusCode, String message, T body , Y errorBody) {
        this.statusCode = statusCode;
        this.message = message;
        this.body = body;
        this.errorBody=errorBody;
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

    public static <T,Y> ApiResponse<T,Y> success(T body, String message,Y errorBody) {
        return new ApiResponse<>(HttpStatus.SC_OK, message, body,errorBody);
    }

    public static <T,Y> ApiResponse<T,Y> error(int statusCode, String message, T body,Y errorBody) {
        return new ApiResponse<>(statusCode, message, body,errorBody);
    }
}
