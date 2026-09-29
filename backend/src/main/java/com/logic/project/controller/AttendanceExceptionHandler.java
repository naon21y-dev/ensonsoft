package com.logic.project.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;

@Order(0)
@RestControllerAdvice(assignableTypes = AttendanceController.class)
public class AttendanceExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> business(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("code", error.getMessage().startsWith("attendance.errors.")
                ? error.getMessage() : "attendance.errors.invalidInput"));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> invalid(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("code", "attendance.errors.invalidInput"));
    }
}
