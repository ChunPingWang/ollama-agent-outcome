package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * 使用 JPA 實作的 {@link SaveProfilePort}。
 */
@Component
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    /**
     * 建構子。
     *
     * @param profileRepository 個人資料 Repository
     */
    JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public ProfileId save(String name, String email) {
        // 檢查電子郵件是否已存在
        Optional<ProfileEntity> existing = profileRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new DuplicateEmailException("email already exists: " + email);
        }

        try {
            ProfileEntity entity = new ProfileEntity(null, name, email);
            ProfileEntity saved = profileRepository.saveAndFlush(entity);
            return new ProfileId(saved.getId());
        } catch (DataIntegrityViolationException e) {
            // 雙重檢查：若並發或其他原因導致唯一性約束違反，轉為 Domain 例外
            throw new DuplicateEmailException("email already exists: " + email);
        }
    }
}
