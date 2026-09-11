package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.util.FileSecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private final FileSecurityUtil fileSecurityUtil;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir,
            FileSecurityUtil fileSecurityUtil) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileSecurityUtil = fileSecurityUtil;
        try {
            Files.createDirectories(this.rootLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu file!", ex);
        }
    }

    public String storeFile(MultipartFile file, String subFolder) {
        try {
            Path targetFolder = this.rootLocation.resolve(subFolder);
            if (!Files.exists(targetFolder)) {
                Files.createDirectories(targetFolder);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            Path targetLocation = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String internalPath = subFolder + "/" + fileName;

            String encryptedPath = fileSecurityUtil.encrypt(internalPath);

            return "storage/" + encryptedPath;

        } catch (IOException ex) {
            throw new RuntimeException("Lưu file thất bại!", ex);
        }
    }

    public Path loadFileAsResource(String encryptedPath) {
        String internalPath = fileSecurityUtil.decrypt(encryptedPath);
        Path filePath = this.rootLocation.resolve(internalPath).normalize();
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            return filePath;
        } else {
            throw new RuntimeException("File không tồn tại hoặc không thể đọc");
        }
    }
}