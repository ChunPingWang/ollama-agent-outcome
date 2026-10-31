package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 使用 JPA 儲存個人資料的 Port 實作。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository repository;

    public JpaSaveProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Profile profile) {
        try {
            // 轉換為 Entity 並直接存入，依賴 DB unique constraint 處理重複 Email
            ProfileEntity entity = ProfileEntityConverter.toEntity(profile);
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            // 若 DB 層面觸發唯一約束（如 email duplicate），則轉換為 Domain Exception
            throw new DuplicateEmailException();
        }
    }
}
