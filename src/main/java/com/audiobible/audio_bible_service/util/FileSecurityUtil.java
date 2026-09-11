package com.audiobible.audio_bible_service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class FileSecurityUtil {

    private static final String ALGORITHM = "AES";

    @Value("${file.security.secret-key}")
    private String secretKey;

    public String encrypt(String plainText) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa đường dẫn file", e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cipherText);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Đường dẫn file không hợp lệ hoặc đã bị can thiệp", e);
        }
    }
}