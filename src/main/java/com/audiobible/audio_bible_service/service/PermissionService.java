package com.audiobible.audio_bible_service.service;

import org.springframework.stereotype.Service;
import com.audiobible.audio_bible_service.repository.PermissionRepository;
import com.audiobible.audio_bible_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.audiobible.audio_bible_service.dto.response.PermissionDetailWithUsersResponse;
import com.audiobible.audio_bible_service.dto.response.UserPermissionsOverviewResponse;
import com.audiobible.audio_bible_service.dto.response.UserResponse;

import java.util.*;
import com.audiobible.audio_bible_service.entity.Permission;
import com.audiobible.audio_bible_service.entity.User;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

        private final PermissionRepository permissionRepository;
        private final UserRepository userRepository;

        public UserPermissionsOverviewResponse getUserPermissionsGroupedByModule(int userId) {
                List<Permission> userPermissions = permissionRepository.findByUserID(userId);

                Map<String, List<String>> groupedPermissions = userPermissions.stream()
                                .collect(Collectors.groupingBy(
                                                Permission::getModule,
                                                Collectors.mapping(Permission::getCode, Collectors.toList())));

                return UserPermissionsOverviewResponse.builder()
                                .userId(userId)
                                .permissionsByModule(groupedPermissions)
                                .build();
        }

        public Map<String, List<String>> getAllPermissionsGroupedByModule() {
                List<Permission> allPermissions = permissionRepository.findAll();

                return allPermissions.stream()
                                .collect(Collectors.groupingBy(
                                                Permission::getModule,
                                                Collectors.mapping(Permission::getCode, Collectors.toList())));
        }

        public PermissionDetailWithUsersResponse getPermissionDetailWithUsers(String module, String code) {

                List<Permission> permissions = permissionRepository.findByModuleAndCode(module, code);

                if (permissions.isEmpty()) {
                        return PermissionDetailWithUsersResponse.builder()
                                        .module(module)
                                        .code(code)
                                        .users(Collections.emptyList())
                                        .build();
                }

                Permission sample = permissions.get(0);

                List<Integer> userIds = permissions.stream()
                                .map(Permission::getUserID)
                                .distinct()
                                .collect(Collectors.toList());

                List<UserResponse> users = userRepository.findAllByIdIn(userIds).stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());

                return PermissionDetailWithUsersResponse.builder()
                                .module(sample.getModule())
                                .code(sample.getCode())
                                .name(sample.getName())
                                .method(sample.getMethod())
                                .clientPath(sample.getClientPath())
                                .users(users)
                                .build();
        }

        public UserResponse toResponse(User u) {
                return UserResponse.builder()
                                .id(u.getId())
                                .email(u.getEmail())
                                .fullName(u.getFullName())
                                .role(u.getRole())
                                .active(u.getActive())
                                .createdAt(u.getCreatedAt())
                                .build();
        }
}
