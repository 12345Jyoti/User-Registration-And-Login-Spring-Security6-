package com.application.Application.common.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandlerFields {
    private String errorId;
    private String errorCode;
    private String message;
    private Integer statusCode;
    private String statusName;
    private String path;
    private String method;
    private LocalDateTime timestamp;
}