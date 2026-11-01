package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 使用 JPA 實作 SaveProfile Port。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Profile profile) {
        try {
            String id = profile.id().getValue();
            boolean exists = profileRepository.existsById(id);
            
            ProfileEntity entity = ProfileConverter.toEntity(profile);
            
            // 若 ID 已存在，則視為更新操作（isNew=false），否則為新增
            if (exists) {
                entity.setNew(false);
            } else {
                entity.setNew(true);
            }
            
            profileRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists: " + profile.email().getAddress());
        }
    }
}
