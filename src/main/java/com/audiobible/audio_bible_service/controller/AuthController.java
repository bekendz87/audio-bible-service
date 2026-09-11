package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.request.LoginRequest;
import com.audiobible.audio_bible_service.dto.request.RegisterRequest;
import com.audiobible.audio_bible_service.dto.request.RefreshTokenRequest;
import com.audiobible.audio_bible_service.dto.response.AuthResponse;
import com.audiobible.audio_bible_service.entity.Role;
import com.audiobible.audio_bible_service.entity.User;
import com.audiobible.audio_bible_service.repository.UserRepository;
import com.audiobible.audio_bible_service.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final com.audiobible.audio_bible_service.service.RefreshTokenService refreshTokenService;
    @Value("${jwt.expiration}")
    private long accessExpiration;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("Đăng ký tài khoản thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = refreshTokenService.create(user);

        return ResponseEntity.ok(AuthResponse.builder().token(token).accessToken(token).refreshToken(refreshToken)
                .expiresIn(accessExpiration).email(user.getEmail()).role(user.getRole().name()).build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        User user = refreshTokenService.validate(request.getRefreshToken());
        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(AuthResponse.builder().token(token).accessToken(token)
                .refreshToken(request.getRefreshToken()).expiresIn(accessExpiration)
                .email(user.getEmail()).role(user.getRole().name()).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
