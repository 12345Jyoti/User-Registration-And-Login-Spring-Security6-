package com.application.Application.common.exception;

import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.management.AttributeNotFoundException;
import javax.security.sasl.AuthenticationException;

import com.application.Application.common.constant.Constants;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * GlobalExceptionHandler handles and catches all type of exception and throw common response for
 * all the exception {@link ErrorHandlerFields}
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorHandlerFields> handleNullPointerException(
            NullPointerException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.NULL_POINTER_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorHandlerFields> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN; // Assuming 403 status code for access denied
        String errorCode = Constants.ACCESS_DENIED_EXCEPTION;
        String message = "User is not Authorized: " + exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }


    @ExceptionHandler(AttributeNotFoundException.class)
    public ResponseEntity<ErrorHandlerFields> handleAttributeNotFoundException(
            AttributeNotFoundException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorCode = Constants.ATTRIBUTE_NOT_FOUND_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }


    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<ErrorHandlerFields> handleAttributeAlreadyExistException(
            FileAlreadyExistsException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String errorCode = Constants.ATTRIBUTE_AlREADY_PRESENT_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
    @ExceptionHandler(HttpClientErrorException.MethodNotAllowed.class)
    public ResponseEntity<ErrorHandlerFields> handleMethodNotAllowedException(
            HttpClientErrorException.MethodNotAllowed  exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode =Constants.ILLEGAL_ARGUMENT_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.ILLEGAL_ARGUMENT_EXCEPTION;
        String message = (ex.getMessage());
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorHandlerFields> handleJsonParseException(IllegalArgumentException exception,
                                                                       HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.ILLEGAL_ARGUMENT_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorHandlerFields> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.ILLEGAL_ARGUMENT_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorHandlerFields> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.CONSTRAINT_VIOLATION_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorHandlerFields> handleInvalidFormat(InvalidFormatException exception,
                                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        String errorCode = Constants.INVALID_FORMAT_EXCEPTION;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException ex) {
        LOGGER.error("ClientAbortException occurred: {}", ex.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorHandlerFields> handleRuntimeException(
            RuntimeException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = Constants.INTERNAL_SERVER_ERROR;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorHandlerFields> handleRuntimeException(
            AuthenticationException exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorCode = Constants.UNAUTHORIZED;
        String message = exception.getMessage();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorHandlerFields> handleInvalidEnumValueException(HttpMessageNotReadableException exception, HttpServletRequest request ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.INVALID_FORMAT_EXCEPTION;
        String message = (exception.getMessage());
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorHandlerFields> handleInvalidEnumValueException(MethodArgumentTypeMismatchException exception, HttpServletRequest request ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = Constants.INVALID_FORMAT_EXCEPTION;
        String message = "DataType Mismatch";
        String path = request.getRequestURI();
        String method = request.getMethod();
        String errorId = UUID.randomUUID().toString();
        ErrorHandlerFields error =
                new ErrorHandlerFields(errorId, errorCode, message, status.value(), status.name(), path,
                        method, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }


}
