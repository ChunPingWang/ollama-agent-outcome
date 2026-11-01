package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * JPA implementation of LoadProfilePort.
 */
@Service
public class JpaLoadProfilePort implements LoadProfilePort {

    private final ProfileRepository repository;

    JpaLoadProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.id())
                .map(this::toDomain);
    }

    private Profile toDomain(ProfileEntity entity) {
        return new Profile(
            new ProfileId(entity.getId()),
            new Name(entity.getName()),
            new Email(entity.getEmail())
        );
    }
}
