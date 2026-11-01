package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * JPA implementation of SaveProfilePort and ExistsByEmailPort.
 */
@Repository
public class JpaSaveProfilePort implements SaveProfilePort, ExistsByEmailPort {

    private final ProfileRepository profileRepository;

    public JpaSaveProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void save(Profile domainEntity) {
        try {
            ProfileEntity entity = ProfileEntityConverter.toEntity(domainEntity);
            // Use saveAndFlush to trigger constraints immediately in test transactions
            profileRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Duplicate email found");
        }
    }

    @Override
    public boolean existsByEmail(String email, String excludeId) {
        if (excludeId == null || excludeId.isBlank()) {
            return profileRepository.existsByEmail(email);
        } else {
            return profileRepository.existsByEmailAndIdNot(email, excludeId);
        }
    }
}
