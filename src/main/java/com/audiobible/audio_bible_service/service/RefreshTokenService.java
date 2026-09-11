package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.entity.RefreshToken;
import com.audiobible.audio_bible_service.entity.User;
import com.audiobible.audio_bible_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    @Value("${jwt.refresh-expiration:2592000000}")
    private long expiration;
    private final SecureRandom random = new SecureRandom();

    public String create(User user) {
        String raw = newToken();
        repository.save(RefreshToken.builder().tokenHash(hash(raw)).user(user)
                .expiresAt(LocalDateTime.now().plusNanos(expiration * 1_000_000)).build());
        return raw;
    }

    public User validate(String raw) {
        RefreshToken token = repository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token không hợp lệ"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token đã hết hạn hoặc bị thu hồi");
        }
        return token.getUser();
    }

    public void revoke(String raw) {
        repository.findByTokenHash(hash(raw)).ifPresent(token -> {
            token.setRevoked(true);
            repository.save(token);
        });
    }

    public void revokeAll(User user) {
        repository.deleteAllByUserId(user.getId());
    }

    private String newToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể tạo refresh token", ex);
        }
    }
}
