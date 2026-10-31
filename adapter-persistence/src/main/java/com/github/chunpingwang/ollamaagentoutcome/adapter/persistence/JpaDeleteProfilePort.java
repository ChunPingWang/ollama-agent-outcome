package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 刪除個人資料的 Port 實作。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(String profileId) {
        repository.deleteById(profileId);
        repository.flush();
    }
}
