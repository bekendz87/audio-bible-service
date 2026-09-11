package com.audiobible.audio_bible_service.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import com.audiobible.audio_bible_service.entity.ApplicationLog;
import com.audiobible.audio_bible_service.entity.LogType;
import com.audiobible.audio_bible_service.repository.ApplicationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class LogManagementService {
    private static final Logger FALLBACK_LOG = LoggerFactory.getLogger(LogManagementService.class);
    private final Path logDirectory;
    private final ApplicationLogRepository applicationLogRepository;

    public LogManagementService(@Value("${logging.file.name:logs/application.log}") String logPath,
            ApplicationLogRepository applicationLogRepository) {
        Path configuredPath = Path.of(logPath).toAbsolutePath().normalize();
        this.logDirectory = configuredPath.getParent();
        this.applicationLogRepository = applicationLogRepository;
    }

    @Async("logTaskExecutor")
    public void save(ApplicationLog log) {
        try {
            applicationLogRepository.save(log);
        } catch (Exception exception) {
            FALLBACK_LOG.error("database_log_failed message={}", exception.getMessage(), exception);
        }
    }

    public void recordException(String requestId, String method, String path, Throwable exception) {
        StringWriter stackTraceWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTraceWriter));
        save(ApplicationLog.builder()
                .logType(LogType.INTERNAL)
                .level("ERROR")
                .requestId(requestId)
                .httpMethod(method)
                .path(path)
                .exceptionClass(exception.getClass().getName())
                .message(exception.toString())
                .stackTrace(stackTraceWriter.toString())
                .build());
    }

    public void recordHandledException(String requestId, String method, String path, Throwable exception) {
        recordException(requestId, method, path, exception);
    }

    public Page<ApplicationLog> find(LogType type, String requestId, Pageable pageable) {
        if (requestId != null && !requestId.isBlank()) {
            return applicationLogRepository.findByRequestId(requestId, pageable);
        }
        if (type != null) {
            return applicationLogRepository.findByLogType(type, pageable);
        }
        return applicationLogRepository.findAll(pageable);
    }

    public void clearDatabaseLogs() {
        applicationLogRepository.deleteAllInBatch();
    }

    public List<String> listLogFiles() throws IOException {
        if (!Files.exists(logDirectory)) {
            return List.of();
        }

        try (Stream<Path> files = Files.list(logDirectory)) {
            return files.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".log") || name.endsWith(".gz"))
                    .sorted()
                    .toList();
        }
    }

    public String read(String fileName, int lines) throws IOException {
        Path file = safeResolve(fileName);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("File log không tồn tại");
        }

        List<String> content = Files.readAllLines(file);
        int start = Math.max(0, content.size() - Math.min(Math.max(lines, 1), 10_000));
        return String.join(System.lineSeparator(), content.subList(start, content.size()));
    }

    public void clearCurrentLog() throws IOException {
        Path current = safeResolve("application.log");
        if (Files.exists(current)) {
            Files.writeString(current, "", StandardOpenOption.TRUNCATE_EXISTING);
        }

    }

    private Path safeResolve(String fileName) {
        if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Tên file log không hợp lệ");
        }

        Path resolved = logDirectory.resolve(fileName).normalize();
        if (!resolved.getParent().equals(logDirectory)) {
            throw new IllegalArgumentException("Đường dẫn log không hợp lệ");
        }

        return resolved;
    }
}
