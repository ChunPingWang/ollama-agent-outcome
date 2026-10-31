package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.UUID;

/**
 * JPA 實作的刪除個人資料 Port。
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final JpaProfileRepository repository;

    public JpaDeleteProfilePort(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ProfileNotFoundException(id);
        }
        // 使用 deleteById + flush，避免一級快取導致後續查詢仍看到已刪除的實體
        repository.deleteById(id);
        repository.flush(); 
    }
}
