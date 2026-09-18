package com.audiobible.audio_bible_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDetailWithUsersResponse {
    private String module;
    private String code;
    private String name;
    private String method;
    private String clientPath;
    private List<UserResponse> users;
}