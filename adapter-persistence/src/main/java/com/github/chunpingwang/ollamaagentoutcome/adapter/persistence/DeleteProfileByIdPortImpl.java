package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import org.springframework.stereotype.Repository;

/**
 * 依識別碼刪除個人資料 Port 的實作類別。
 */
@Repository
public class DeleteProfileByIdPortImpl implements DeleteProfileByIdPort {

    private final ProfileJpaRepository repository;

    public DeleteProfileByIdPortImpl(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
