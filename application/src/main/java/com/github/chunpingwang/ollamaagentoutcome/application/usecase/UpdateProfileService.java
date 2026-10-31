package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public Profile execute(String profileId, UpdateProfileRequest request) {
        Optional<Profile> existingProfileOpt = loadProfilePort.findById(profileId);
        if (existingProfileOpt.isEmpty()) {
            throw new ProfileNotFoundException(profileId);
        }

        Profile existingProfile = existingProfileOpt.get();
        
        Email newEmail = new Email(request.email());
        Name newName = new Name(request.name());
        Age newAge = new Age(request.age());
        
        // 建立新的 Profile 實例（不可變設計），包含所有欄位的更新
        Profile updatedProfile = existingProfile.update(newEmail, newName, newAge);
        
        // 儲存更新後的資料
        return saveProfilePort.save(updatedProfile);
    }
}
