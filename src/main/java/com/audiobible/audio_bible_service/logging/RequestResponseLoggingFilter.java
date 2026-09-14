package com.audiobible.audio_bible_service.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.audiobible.audio_bible_service.entity.ApplicationLog;
import com.audiobible.audio_bible_service.entity.LogType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
        private static final Logger REQUEST_LOG = LoggerFactory.getLogger("REQUEST");
        private static final Logger CLIENT_RESPONSE_LOG = LoggerFactory.getLogger("CLIENT_RESPONSE");
        private static final Logger INTERNAL_LOG = LoggerFactory.getLogger("INTERNAL");
        private static final int MAX_BODY_LENGTH = 8_000;
        private final LogManagementService logManagementService;

        public RequestResponseLoggingFilter(LogManagementService logManagementService) {
                this.logManagementService = logManagementService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {
                ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request);
                ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(response);
                String requestId = StringUtils.hasText(request.getHeader("X-Request-Id"))
                                ? request.getHeader("X-Request-Id")
                                : UUID.randomUUID().toString();
                long startedAt = System.nanoTime();
                MDC.put("requestId", requestId);
                response.setHeader("X-Request-Id", requestId);

                try {
                        filterChain.doFilter(cachedRequest, cachedResponse);
                } catch (Exception exception) {
                        INTERNAL_LOG.error("request_failed requestId={} method={} path={} message={}",
                                        requestId, request.getMethod(), request.getRequestURI(), exception.getMessage(),
                                        exception);
                        logManagementService.recordException(requestId, request.getMethod(), request.getRequestURI(),
                                        exception);
                        throw exception;
                } finally {
                        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
                        String requestBody = sanitize(
                                        body(cachedRequest.getContentAsByteArray(), request.getCharacterEncoding(),
                                                        request.getContentType()));
                        String responseBody = sanitize(
                                        body(cachedResponse.getContentAsByteArray(), response.getCharacterEncoding(),
                                                        response.getContentType()));
                        REQUEST_LOG.info("requestId={} method={} path={} query={} remote={} contentType={} body={}",
                                        requestId, request.getMethod(), request.getRequestURI(),
                                        sanitize(request.getQueryString()),
                                        request.getRemoteAddr(), request.getContentType(), requestBody);
                        logManagementService.save(ApplicationLog.builder()
                                        .logType(LogType.REQUEST).level("INFO").requestId(requestId)
                                        .httpMethod(request.getMethod()).path(request.getRequestURI())
                                        .queryString(sanitize(request.getQueryString()))
                                        .remoteAddress(request.getRemoteAddr())
                                        .contentType(request.getContentType()).requestBody(requestBody).build());
                        CLIENT_RESPONSE_LOG.info(
                                        "requestId={} method={} path={} status={} durationMs={} contentType={} body={}",
                                        requestId, request.getMethod(), request.getRequestURI(), response.getStatus(),
                                        durationMs,
                                        response.getContentType(), responseBody);
                        logManagementService.save(ApplicationLog.builder()
                                        .logType(LogType.CLIENT_RESPONSE)
                                        .level(response.getStatus() >= 500 ? "ERROR" : "INFO")
                                        .requestId(requestId).httpMethod(request.getMethod())
                                        .path(request.getRequestURI())
                                        .httpStatus(response.getStatus()).durationMs(durationMs)
                                        .contentType(response.getContentType()).responseBody(responseBody).build());
                        cachedResponse.copyBodyToResponse();
                        MDC.remove("requestId");
                }
        }

        private String body(byte[] bytes, String encoding, String contentType) {
                if (bytes == null || bytes.length == 0) {
                        return "<empty>";
                }

                if (contentType != null && (contentType.startsWith("audio/")
                                || contentType.startsWith("image/")
                                || contentType.startsWith("application/octet-stream"))) {
                        return "<binary omitted>";
                }

                String value = new String(bytes,
                                encoding == null ? StandardCharsets.UTF_8 : java.nio.charset.Charset.forName(encoding));
                return value.length() > MAX_BODY_LENGTH ? value.substring(0, MAX_BODY_LENGTH) + "...[truncated]"
                                : value;
        }

        private String sanitize(String value) {
                if (value == null || value.isBlank()) {
                        return "<empty>";
                }
                return value
                                .replaceAll("(?i)(\\\"(?:password|token|accessToken|refreshToken)\\\"\\s*:\\s*\\\")[^\\\"]*\\\"",
                                                "$1***\"")
                                .replaceAll("(?i)(password|token|accessToken|refreshToken)=([^&\\s]+)", "$1=***");
        }
}
