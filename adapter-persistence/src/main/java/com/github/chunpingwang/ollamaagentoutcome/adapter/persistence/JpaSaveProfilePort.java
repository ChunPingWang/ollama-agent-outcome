package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA implementation of SaveProfilePort.
 */
@Service
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;

    JpaSaveProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProfileId save(Profile profile) {
        try {
            java.util.Optional<ProfileEntity> existingOpt = repository.findById(profile.id().id());
            
            if (existingOpt.isPresent()) {
                // 更新：修改受管實體的欄位，避免 isNew() 導致 INSERT 衝突
                ProfileEntity entity = existingOpt.get();
                entity.setName(profile.name().value());
                entity.setEmail(profile.email().value());
                repository.saveAndFlush(entity);
            } else {
                // 新增：建立新 Entity
                ProfileEntity newEntity = toEntity(profile);
                repository.saveAndFlush(newEntity);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
        
        return profile.id();
    }

    private ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
            profile.id().id(),
            profile.name().value(),
            profile.email().value()
        );
    }
}
