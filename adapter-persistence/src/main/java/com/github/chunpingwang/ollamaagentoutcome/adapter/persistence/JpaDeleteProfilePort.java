package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 使用 JPA 刪除個人資料的 Port 實作。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository repository;

    public JpaDeleteProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(String id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("找不到識別碼為 " + id);
        }
        repository.deleteById(id);
    }
}
