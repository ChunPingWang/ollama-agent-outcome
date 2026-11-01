package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for retrieving an existing profile by ID.
 */
public interface GetProfileUseCase {

    /**
     * Execute the use case to get profile data by its ID.
     *
     * @param id The unique identifier of the profile.
     * @return A record containing the profile's id, email, and displayName.
     */
    ProfileData execute(ProfileId id);

    /**
     * Data transfer object representing the result of getting a profile.
     */
    record ProfileData(String id, String email, String displayName) {}
}
