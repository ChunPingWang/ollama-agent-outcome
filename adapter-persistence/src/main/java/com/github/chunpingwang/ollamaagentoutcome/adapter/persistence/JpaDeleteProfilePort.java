package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import org.springframework.stereotype.Repository;

/**
 * JPA 實作：刪除個人資料 Port。根據 ID 執行硬刪除。
 */
@Repository
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    public JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(String id) {
        profileRepository.deleteById(id);
    }
}
