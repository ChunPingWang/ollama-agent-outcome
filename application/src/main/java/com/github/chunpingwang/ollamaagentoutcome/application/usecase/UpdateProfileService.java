package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final FindProfileByEmailPort findProfileByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort,
                                FindProfileByEmailPort findProfileByEmailPort,
                                SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.findProfileByEmailPort = findProfileByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public Profile updateProfile(ProfileId id, UpdateProfileRequest request) {
        // 1. 檢查 Profile 是否存在
        Profile existingProfile = findProfileByIdPort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));

        // 2. 驗證並建立新的 Email
        Email newEmail;
        try {
            newEmail = new Email(request.email());
        } catch (InvalidEmailException e) {
            throw new InvalidEmailException("Invalid email format");
        }

        // 3. 檢查電子郵件唯一性 (若 email 有變更)
        if (!existingProfile.getEmail().equals(newEmail)) {
            if (findProfileByEmailPort.findByEmail(newEmail).isPresent()) {
                throw new DuplicateEmailException("Email already exists");
            }
        }

        // 4. 驗證並建立新的 Name 和 Age
        Name newName;
        Age newAge;
        try {
            newName = new Name(request.name());
            newAge = new Age(request.age());
        } catch (InvalidNameException e) {
            throw new InvalidNameException("Invalid name");
        } catch (InvalidAgeException e) {
            throw new InvalidAgeException("Invalid age");
        }

        // 5. 建立更新後的 Profile 並儲存
        Profile updatedProfile = new Profile(id, newEmail, newName, newAge);
        return saveProfilePort.save(updatedProfile);
    }
}
