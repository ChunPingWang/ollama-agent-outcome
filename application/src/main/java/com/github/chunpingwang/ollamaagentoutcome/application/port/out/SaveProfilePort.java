package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Outbound port for saving a profile.
 */
public interface SaveProfilePort {

    /**
     * Saves the given profile.
     * @param profile The profile to save.
     * @return The saved profile.
     */
    Profile save(Profile profile);
}
