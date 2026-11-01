package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * JPA 實作的 LoadProfile Port，負責根據 ID 查詢。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    public JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> findById(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId id) {
        return repository.findById(id.value()).map(JpaSaveProfilePort::convertToDomain);
    }
}
