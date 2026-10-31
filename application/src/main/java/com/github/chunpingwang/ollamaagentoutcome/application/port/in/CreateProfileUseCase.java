package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {

    /**
     * Creates a new profile with the given command data.
     *
     * @param name   the user's name
     * @param email  the user's email address
     * @param age    the user's age
     * @return the ID of the newly created profile
     */
    ProfileId create(String name, String email, int age);
}
