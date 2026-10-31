package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * SaveProfilePort 的 JPA 實作，負責將領域物件持久化並處理唯一約束衝突。
 */
@Repository
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public ProfileId saveAndReturnId(Name name, Email email) {
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, name.text(), email.address());
        
        try {
            // 使用 saveAndFlush 確保在單一交易內觸發唯一性約束檢查
            profileRepository.saveAndFlush(entity);
            return new ProfileId(id);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(e);
        }
    }

    @Override
    public ProfileId save(Profile profile) {
        ProfileEntity existing = profileRepository.findById(profile.id().value()).orElse(null);
        if (existing != null) {
            existing.setName(profile.name().text());
            existing.setEmail(profile.email().address());
            profileRepository.save(existing);
        } else {
            ProfileEntity entity = new ProfileEntity(profile.id().value(), profile.name().text(), profile.email().address());
            try {
                profileRepository.saveAndFlush(entity);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEmailException(e);
            }
        }
        return profile.id();
    }
}
