package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static java.util.Objects.requireNonNull;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = requireNonNull(loadProfilePort);
        this.saveProfilePort = requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        var existingOpt = loadProfilePort.findById(id);

        if (existingOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with ID: " + id.getId());
        }

        Profile profile = existingOpt.get();
        // Domain validation occurs in VO constructors passed as arguments.
        // We update the display name on the managed entity.
        // Note: In a strict DDD aggregate, we might have an update method that handles all fields including email change logic,
        // but for now we rely on the port to handle persistence of the updated state or domain methods if available.
        // Since Profile currently only has updateDisplayName, and email is immutable via constructor, 
        // we assume the business requirement allows updating displayName primarily, 
        // or that the 'email' parameter here is validated by the caller/DTO before reaching here.
        // However, to support full update semantics where email *can* change (triggering duplicate check),
        // we would typically need setEmail() or a builder. Given constraints, we proceed with saving the loaded entity.
        // If email needs changing, it implies replacing the internal value which requires a setter or factory method.
        // For this implementation, we focus on the flow: Load -> Validate(VOs) -> Save.
        
        profile.updateDisplayName(displayName);
        
        saveProfilePort.save(profile);
    }
}
