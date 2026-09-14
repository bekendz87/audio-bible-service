package com.audiobible.audio_bible_service.exception;

import com.audiobible.audio_bible_service.dto.response.ApiResponse;
import com.audiobible.audio_bible_service.logging.LogManagementService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger INTERNAL_LOG = LoggerFactory.getLogger("INTERNAL");
        private final LogManagementService logManagementService;

        public GlobalExceptionHandler(LogManagementService logManagementService) {
                this.logManagementService = logManagementService;
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex,
                        HttpServletRequest request) {
                INTERNAL_LOG.error("handled_exception requestId={} exception={} message={}",
                                MDC.get("requestId"), ex.getClass().getSimpleName(), ex.getMessage(), ex);
                logManagementService.recordHandledException(MDC.get("requestId"), request.getMethod(),
                                request.getRequestURI(), ex);

                ApiResponse<Void> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleException(Exception ex, HttpServletRequest request) {
                INTERNAL_LOG.error("unhandled_exception requestId={} exception={} message={}",
                                MDC.get("requestId"), ex.getClass().getSimpleName(), ex.getMessage(), ex);
                logManagementService.recordHandledException(MDC.get("requestId"), request.getMethod(),
                                request.getRequestURI(), ex);

                ApiResponse<Void> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Đã xảy ra lỗi hệ thống");

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}