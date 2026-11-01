package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的 Service 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort, 
                                ExistsByEmailPort existsByEmailPort, 
                                SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(ProfileId id, String email, String displayName) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found with id: " + id.value());
        }
        
        Profile profile = profileOpt.get();
        Email newEmailValueObject = new Email(email);
        
        // Check uniqueness excluding current ID? 
        // The spec says unique globally. If the email is changing to something else that already exists, it should fail.
        // If the email remains the same as current, we don't need to check against others, but checking all and ignoring self is safer or just check existence.
        // For simplicity in TDD RED phase, we assume existsByEmail checks global DB state.
        // To avoid false positive when updating SAME email, we might need a method existsByEmailExcludingId, 
        // but based on simple port definition `existsByEmail(Email)`, let's assume the Port handles logic or Service does.
        // Given the prompt's port list doesn't have exclude-ID version, we'll stick to basic exists. 
        // However, standard practice for update-with-unique-constraint usually requires exclusion. 
        // Let's implement strict check first. If test fails because of self-update, we adjust port/service later.
        // Actually, looking at typical implementations, if I change email from A to B, and B exists (and B != A), throw error.
        // If I keep A as A, no error.
        // Since `ExistsByEmailPort` only takes Email, we can't pass ID here easily without modifying port signature.
        // Let's modify the service logic: if new email equals old email, skip uniqueness check? Or rely on Port implementation details?
        // To be safe and strictly follow "Application layer defines outbound ports", let's assume the simplest case:
        // We will add a small check in Service: if profile.getEmailString().equals(email.toLowerCase()), skip existence check.
        
        String currentEmail = profile.getEmailString();
        if (!currentEmail.equals(newEmailValueObject.value())) {
            if (existsByEmailPort.existsByEmail(newEmailValueObject)) {
                throw new DuplicateEmailException("email already exists: " + email);
            }
        }

        profile.update(email, displayName);
        saveProfilePort.save(profile);
    }
}
