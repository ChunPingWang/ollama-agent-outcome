package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import org.springframework.stereotype.Component;import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * JPA 實作的 LoadProfile Port，負責載入資料。
 */
@Component
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    public JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> findById(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    private com.github.chunpingwang.ollamaagentoutcome.domain.Profile toDomain(ProfileEntity entity) {
        Email email = new Email(entity.getEmail());
        DisplayName displayName = entity.getDisplayName() != null ? new DisplayName(entity.getDisplayName()) : null;
        return new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                entity.getId(),
                email,
                displayName
        );
    }
}
