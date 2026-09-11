package com.audiobible.audio_bible_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import com.audiobible.audio_bible_service.logging.LogManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger INTERNAL_LOG = LoggerFactory.getLogger("INTERNAL");
    private final LogManagementService logManagementService;

    public GlobalExceptionHandler(LogManagementService logManagementService) {
        this.logManagementService = logManagementService;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex,
                                                                       HttpServletRequest request) {
        INTERNAL_LOG.error("handled_exception requestId={} exception={} message={}",
            MDC.get("requestId"), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        logManagementService.recordHandledException(MDC.get("requestId"), request.getMethod(),
                request.getRequestURI(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("requestId", MDC.get("requestId"));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, HttpServletRequest request) {
        INTERNAL_LOG.error("unhandled_exception requestId={} exception={} message={}",
                MDC.get("requestId"), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        logManagementService.recordHandledException(MDC.get("requestId"), request.getMethod(),
                request.getRequestURI(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("requestId", MDC.get("requestId"));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Đã xảy ra lỗi hệ thống");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}