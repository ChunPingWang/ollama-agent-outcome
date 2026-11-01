package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * SaveProfilePort 的 JPA 實作。
 * 處理 Domain 與 JPA Entity 的轉換，並捕捉唯一性約束違規以拋出正確的 Domain Exception。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;
    private final ProfileEntityConverter converter;

    public JpaSaveProfilePort(ProfileRepository repository, ProfileEntityConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Profile save(Profile profile) {
        try {
            Optional<ProfileEntity> existingOpt = 
                repository.findById(profile.getId().value());
            
            if (existingOpt.isPresent()) {
                // 若存在，則為更新操作：修改受管實體的欄位
                ProfileEntity entity = existingOpt.get();
                entity.setEmail(profile.getEmailString());
                entity.setDisplayName(profile.getDisplayNameString());
                return converter.toDomain(entity);
            } else {
                // 若不存在，則為新增操作：建立新 Entity 並儲存
                ProfileEntity newEntity = converter.toEntity(profile);
                repository.saveAndFlush(newEntity);
                return converter.toDomain(newEntity);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }
}
