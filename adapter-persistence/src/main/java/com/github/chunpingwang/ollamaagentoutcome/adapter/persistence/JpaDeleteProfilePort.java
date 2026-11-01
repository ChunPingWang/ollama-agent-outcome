package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JPA 實作的刪除個人資料 Port。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    /**
     * 建構子注入 Repository。
     *
     * @param repository Spring Data Repository
     */
    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        UUID uuid = id.id();
        if (repository.existsById(uuid.toString())) {
            repository.deleteById(uuid.toString());
        }
    }
}
