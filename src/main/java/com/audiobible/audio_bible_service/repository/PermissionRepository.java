package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    List<Permission> findByUserID(Integer userId);

    List<Permission> findAll();

    List<Permission> findByModuleAndCode(String module, String code);
}