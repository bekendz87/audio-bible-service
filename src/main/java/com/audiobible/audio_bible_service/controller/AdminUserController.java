package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.request.UserRoleUpdateRequest;
import com.audiobible.audio_bible_service.dto.response.PermissionDetailWithUsersResponse;
import com.audiobible.audio_bible_service.dto.response.UserPermissionsOverviewResponse;
import com.audiobible.audio_bible_service.dto.response.UserResponse;
import com.audiobible.audio_bible_service.service.PermissionService;
import com.audiobible.audio_bible_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.audiobible.audio_bible_service.config.AdminApiController;
import java.util.List;
import java.util.Map;

@RestController
@AdminApiController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;
    private final PermissionService permissionService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPermissionsOverviewResponse> getUserPermissionsGroupedByModule(
            @PathVariable int userId) {
        return ResponseEntity.ok(permissionService.getUserPermissionsGroupedByModule(userId));
    }

    @GetMapping("/modules")
    public ResponseEntity<Map<String, List<String>>> getAllModulePermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissionsGroupedByModule());
    }

    @GetMapping("/detail")
    public ResponseEntity<PermissionDetailWithUsersResponse> getPermissionDetail(
            @RequestParam String module,
            @RequestParam String code) {
        return ResponseEntity.ok(permissionService.getPermissionDetailWithUsers(module, code));
    }

    @GetMapping
    public Page<UserResponse> getUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.getUsers(PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by("createdAt").descending()));
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable int id, @Valid @RequestBody UserRoleUpdateRequest request) {
        return userService.updateRole(id, request.getRole());
    }

    @PatchMapping("/{id}/active")
    public UserResponse updateActive(@PathVariable int id, @RequestParam boolean active) {
        return userService.setActive(id, active);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
