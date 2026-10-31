package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 使用 JPA 依 ID 刪除個人資料的 Port 實作。
 */
class JpaDeleteProfileByIdPort implements DeleteProfileByIdPort {

    private final ProfileRepository profileRepository;

    JpaDeleteProfileByIdPort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(ProfileId id) {
        profileRepository.deleteById(id.getValue());
    }
}
