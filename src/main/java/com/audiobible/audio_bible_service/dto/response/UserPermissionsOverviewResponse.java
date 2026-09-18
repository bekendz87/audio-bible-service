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
public class UserPermissionsOverviewResponse {
    private int userId;
    private Map<String, List<String>> permissionsByModule;
}
