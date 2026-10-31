package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 使用 JPA 儲存個人資料的 Port 實作。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileJpaRepository repository;
    private final ProfileEntityConverter converter;

    public JpaSaveProfilePort(ProfileJpaRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = converter.toEntity(profile);
            ProfileEntity savedEntity = repository.saveAndFlush(entity);
            return converter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists");
        }
    }
}
