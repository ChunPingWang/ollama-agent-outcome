package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for getting an existing profile by ID.
 */
public interface GetProfileUseCase {
    /**
     * Executes the get profile use case.
     * @param query contains the profile ID to look up
     * @return result containing the profile details if found
     */
    GetProfileResult execute(GetProfileQuery query);

    /**
     * Query object containing input data for retrieval.
     */
    record GetProfileQuery(ProfileId id) {}

    /**
     * Result object returned after successful lookup.
     */
    record GetProfileResult(Name name, Email email) {}
}
