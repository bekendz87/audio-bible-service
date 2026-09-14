package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    List<Permission> findByUserID(Integer userId);

}