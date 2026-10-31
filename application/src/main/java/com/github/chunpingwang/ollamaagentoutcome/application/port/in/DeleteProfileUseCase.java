package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Application layer inbound port for deleting a profile.
 */
public interface DeleteProfileUseCase {
    void execute(ProfileId id);
}
