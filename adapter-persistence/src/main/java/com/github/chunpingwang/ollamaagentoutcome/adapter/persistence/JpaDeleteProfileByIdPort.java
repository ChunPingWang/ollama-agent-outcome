package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;

/**
 * DeleteProfileByIdPort 的 JPA 實作。
 */
@Component
public class JpaDeleteProfileByIdPort implements DeleteProfileByIdPort {

    private final ProfileRepository repository;

    public JpaDeleteProfileByIdPort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(ProfileId id) {
        Optional<com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity> existingOpt = 
            repository.findById(id.value());
        
        if (existingOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with id: " + id.value());
        }
        
        try {
            repository.deleteById(id.value());
        } catch (Exception e) {
             // In a real transactional context, standard delete is sufficient.
             // If strict flush is needed for test isolation, the caller should handle it or we rely on container behavior.
             // However, to be safe against FK constraints or other issues in tests, we just let it propagate or wrap.
             // Since spec doesn't specify complex cascade failures here, simple deletion is fine.
             throw new RuntimeException(e);
        }
    }
}
