package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 新增個人資料的使用者情境實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileRequest request) {
        // 建立值物件，若格式無效會自動拋出 InvalidEmailException / InvalidNameException
        Name name = new Name(request.name());
        Email email = new Email(request.email());
        
        Profile profile = new Profile(
            new ProfileId(UUID.randomUUID().toString()), 
            name, 
            email
        );
        
        try {
            saveProfilePort.save(profile);
        } catch (DuplicateEmailException e) {
            throw e;
        }
    }
}
