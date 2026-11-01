package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA 實作：儲存個人資料 Port。處理新增與更新，並轉換唯一約束違反例外。
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile profile) {
        try {
            var existingOpt = profileRepository.findById(profile.id().value());
            ProfileEntity entity;
            
            if (existingOpt.isPresent()) {
                // 更新流程：先取得受管 Entity，再更新欄位，避免以新實體覆蓋導致 isNew=true 被誤判為 INSERT
                entity = existingOpt.get();
                entity.setEmail(profile.email().address());
                
                String displayNameStr = Optional.ofNullable(profile.displayName()).map(DisplayName::name).orElse("");
                entity.setDisplayName(displayNameStr.isBlank() ? "" : displayNameStr);
            } else {
                // 建立流程：使用新實體建構子，isNew 預設為 true，JPA 會執行 INSERT
                entity = toEntity(profile);
            }
            
            profileRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("email already exists");
        }
    }

    private ProfileEntity toEntity(Profile profile) {
        return new ProfileEntity(
            profile.id().value(),
            profile.email().address(),
            Optional.ofNullable(profile.displayName()).map(DisplayName::name).filter(s -> !s.isBlank()).orElse("")
        );
    }
}
