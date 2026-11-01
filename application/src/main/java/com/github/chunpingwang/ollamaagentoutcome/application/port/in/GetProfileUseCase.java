package com.github.chunpingwang.ollamaagentoutcome.application.port.in;


import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Inbound port for retrieving a profile by ID.
 */
public interface GetProfileUseCase {
    Profile execute(GetProfileQuery query);
}
