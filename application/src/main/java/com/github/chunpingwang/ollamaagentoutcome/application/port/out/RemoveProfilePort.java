package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Outbound port for removing a profile from persistence storage.
 */
public interface RemoveProfilePort {
    /**
     * Removes the profile with the specified ID.
     * @param id of the profile to remove
     */
    void deleteById(ProfileId id);
}
