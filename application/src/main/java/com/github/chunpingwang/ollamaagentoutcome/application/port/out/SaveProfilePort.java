package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Outbound port for saving a profile entity to persistence storage.
 */
public interface SaveProfilePort {
    /**
     * Saves the given profile and returns its ID.
     * @param profile the domain profile to save
     * @return the assigned or existing profile ID
     */
    ProfileId save(Profile profile);
}
