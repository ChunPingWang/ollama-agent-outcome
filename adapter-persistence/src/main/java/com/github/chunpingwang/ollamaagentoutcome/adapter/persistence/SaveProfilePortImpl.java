package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * 儲存個人資料 Port 的實作類別。
 */
@Repository
public class SaveProfilePortImpl implements SaveProfilePort {

    private final ProfileConverter converter = new ProfileConverter();
    private final ProfileJpaRepository repository;

    public SaveProfilePortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            ProfileEntity entity = converter.toEntity(profile);
            ProfileEntity saved = repository.saveAndFlush(entity);
            return converter.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("電子郵件已存在");
        }
    }
}
