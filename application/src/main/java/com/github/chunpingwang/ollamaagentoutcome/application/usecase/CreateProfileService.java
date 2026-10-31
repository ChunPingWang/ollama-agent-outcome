package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的 Use Case 實作。
 * 負責驗證輸入並呼叫儲存 Port。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    /**
     * 建構子。
     *
     * @param saveProfilePort 儲存個人資料的 Port
     */
    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public ProfileId createProfile(String name, String email) {
        // 驗證 Email 格式，若無效則拋出 InvalidEmailException
        new Email(email);

        // 呼叫 Port 儲存資料，若重複則 Port 會拋出 DuplicateEmailException
        return saveProfilePort.save(name, email);
    }
}
