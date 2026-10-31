package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final FindProfileByEmailPort findProfileByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, FindProfileByEmailPort findProfileByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.findProfileByEmailPort = findProfileByEmailPort;
    }

    @Override
    public ProfileId create(CreateProfileRequest request) {
        // 1. 驗證並建立 Email (若格式無效，Email 建構子會拋出 InvalidEmailException)
        Email email;
        try {
            email = new Email(request.email());
        } catch (InvalidEmailException e) {
            throw new InvalidEmailException("Invalid email format");
        }

        // 2. 檢查電子郵件唯一性
        if (findProfileByEmailPort.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        // 3. 驗證並建立 Name 和 Age (若無效，建構子會拋出對應例外)
        Name name;
        Age age;
        try {
            name = new Name(request.name());
            age = new Age(request.age());
        } catch (InvalidNameException e) {
            throw new InvalidNameException("Invalid name");
        } catch (InvalidAgeException e) {
            throw new InvalidAgeException("Invalid age");
        }

        // 4. 建立 Profile 並儲存
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Profile profile = new Profile(id, email, name, age);
        Profile saved = saveProfilePort.save(profile);
        return saved.getId();
    }
}
