package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA 實作的 SaveProfile Port，負責儲存個人資料並處理唯一性約束衝突。
 */
@Component
class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            var entity = ProfileEntityConverter.toEntity(profile);
            // useAndFlush triggers unique constraint check immediately within transaction
            var savedEntity = profileRepository.saveAndFlush(entity);
            return ProfileEntityConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email");
        }
    }
}
