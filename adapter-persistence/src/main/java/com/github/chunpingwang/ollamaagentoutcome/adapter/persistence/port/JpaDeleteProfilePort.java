package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.ProfileJpaRepository;

/**
 * 使用 JPA 實現 DeleteProfilePort，負責刪除個人資料。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        repository.deleteById(id.value());
    }
}
