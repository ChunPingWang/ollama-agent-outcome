package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * JPA Delete Profile Port 實作，執行物理刪除。
 */
@Repository
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileRepository repository;

    JpaDeleteProfilePort(ProfileRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
