package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作的 {@link DeleteProfilePort}。
 */
@Component
class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository profileRepository;

    /**
     * 建構子。
     *
     * @param profileRepository 個人資料 Repository
     */
    JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(ProfileId id) {
        // 先檢查是否存在，若不存在則拋出例外，確保業務邏輯一致性
        if (!profileRepository.existsById(id.getValue())) {
            throw new ProfileNotFoundException("Profile not found: " + id);
        }
        profileRepository.deleteById(id.getValue());
    }
}
