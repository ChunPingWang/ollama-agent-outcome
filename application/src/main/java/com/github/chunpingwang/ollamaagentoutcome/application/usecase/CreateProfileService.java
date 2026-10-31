package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void create(CreateProfileRequest request) {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Profile profile = new Profile(
            id,
            new Email(request.email()),
            new Name(request.name()),
            new Bio(request.bio())
        );
        try {
            saveProfilePort.save(profile);
        } catch (DuplicateEmailException e) {
            throw e;
        } catch (Exception e) {
            // 若 Port 拋出其他與唯一性相關的例外，可在此轉換，但根據 spec 主要處理 DuplicateEmailException
            // 此處為了簡化，若 Port 內部已處理轉換則直接拋出，否則可能需要更細緻的處理
            // 根據測試預期，Port 會直接拋出 DuplicateEmailException
            throw new RuntimeException("建立個人資料失敗", e);
        }
    }
}
