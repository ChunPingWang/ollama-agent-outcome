package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * JPA 實作的 SaveProfile Port。
 */
@Component
public class JpaSaveProfilePort implements SaveProfilePort {

    private static final Logger log = LoggerFactory.getLogger(JpaSaveProfilePort.class);

    private final ProfileRepository repository;

    public JpaSaveProfilePort(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Profile save(com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile) {
        try {
            var entity = ProfileEntityConverter.toEntity(profile);
            var savedEntity = repository.saveAndFlush(entity);
            return ProfileEntityConverter.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Duplicate email detected", e);
            }
            throw new DuplicateEmailException();
        }
    }
}
