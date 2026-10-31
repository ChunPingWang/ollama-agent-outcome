package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * RemoveProfilePort 的 JPA 實作，負責根據識別碼刪除個人資料。
 */
@Repository
class JpaRemoveProfilePort implements RemoveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaRemoveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void removeById(ProfileId id) {
        profileRepository.deleteById(id.value());
    }
}
