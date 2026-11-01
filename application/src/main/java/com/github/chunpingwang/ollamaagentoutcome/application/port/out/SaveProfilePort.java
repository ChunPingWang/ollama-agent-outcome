package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Outbound port for saving a profile entity to persistence storage.
 */
public interface SaveProfilePort {
    /**
     * Saves the given domain Profile.
     * @param profile the profile to save
     * @return the saved profile (may be updated with generated IDs or timestamps if applicable, though here it's mostly void-like in effect for UUIDs)
     */
    Profile save(Profile profile);
}
