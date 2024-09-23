package com.auth.AuthImpl.utils.exception;

import java.time.LocalDateTime;


public class ErrorHandlerFields {
    private String errorId;
    private String errorCode;
    private String message;
    private Integer statusCode;
    private String statusName;
    private String path;
    private String method;
    private LocalDateTime timestamp;

    public ErrorHandlerFields(){}

    public ErrorHandlerFields(String errorId, String errorCode, String message, Integer statusCode, String statusName, String path, String method, LocalDateTime timestamp) {
        this.errorId = errorId;
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.path = path;
        this.method = method;
        this.timestamp = timestamp;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}