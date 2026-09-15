package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.request.ProgressUpdateRequest;
import com.audiobible.audio_bible_service.dto.request.RegisterRequest;
import com.audiobible.audio_bible_service.dto.request.UserUpdateDTO;
import com.audiobible.audio_bible_service.entity.User;
import com.audiobible.audio_bible_service.security.JwtProvider;
import com.audiobible.audio_bible_service.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.audiobible.audio_bible_service.dto.response.ApiResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/{userId}/progress")
    public ResponseEntity<Object> updateProgress(
            @PathVariable int userId,
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody ProgressUpdateRequest request) {
        if (currentUser == null || !userService.isUserEmail(userId, currentUser.getUsername())) {
            return ResponseEntity.status(403).body("Không có quyền cập nhật tiến trình của tài khoản này");
        }
        userService.updateProgress(userId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật tiến trình thành công"));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<User>> getListRole(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        String token = bearerToken.substring(7).trim();
        String email = jwtProvider.getEmailFromToken(token);

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        return ResponseEntity.ok(ApiResponse.success(200, "Lấy thông tin chi tiết người dùng thành công !", user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateDTO user) {
        if (id == 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Mã người dùng không được để trống !"));
        }

        User findUser = userService.findByID(id);
        if (findUser == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Người dùng không tồn tại !"));
        }

        if (user != null && user.getFullName() != null) {
            findUser.setFullName(user.getFullName());
        }

        if (user != null && user.getAvatarUrl() != null) {
            findUser.setAvatarUrl(user.getAvatarUrl());
        }

        if (user != null && user.getRole() != null) {
            findUser.setRole(user.getRole());
        }

        String oldPassword = user.getOldPassword();
        String newPassword = user.getNewPassword();

        if (newPassword != null && !newPassword.isBlank()) {
            if (oldPassword == null || oldPassword.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Vui lòng nhập mật khẩu hiện tại để thực hiện đổi mật khẩu!"));
            }

            if (!passwordEncoder.matches(oldPassword, findUser.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Mật khẩu hiện tại không chính xác!"));
            }

            if (passwordEncoder.matches(newPassword, findUser.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Mật khẩu mới không được trùng với mật khẩu cũ!"));
            }

            findUser.setPassword(passwordEncoder.encode(newPassword));
        }
        userService.updateUser(id, findUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật thông tin người dùng thành công!", findUser));

    }
}