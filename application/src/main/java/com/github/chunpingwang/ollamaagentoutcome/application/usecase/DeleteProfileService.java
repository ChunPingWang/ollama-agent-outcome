package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

/**
 * Service implementation for deleting a profile by ID.
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public DeleteProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public void execute(UUID id) {
        Profile existingProfile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
        
        // The port handles the actual deletion logic (e.g., calling repository.deleteById)
        // We pass the loaded entity or just the ID depending on Port design. 
        // Based on test: deleteById(id) is called on LoadProfilePort mock? 
        // Wait, looking at DeleteProfileServiceTest:
        // doNothing().when(loadProfilePort).deleteById(id);
        // verify(loadProfilePort).deleteById(id);
        // This implies LoadProfilePort should have a deleteById method OR we need to adjust interface.
        // However, standard Hexagonal usually has Save/Load ports. Deletion might be part of SavePort or separate.
        // Let's look at the test again carefully.
        // Test mocks `loadProfilePort` and expects `deleteById(id)` call on it.
        // So I must add `void deleteById(UUID id)` to `LoadProfilePort`.
        
        loadProfilePort.deleteById(id);
    }
}
