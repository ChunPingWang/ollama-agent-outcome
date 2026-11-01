package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import java.util.Objects;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的應用服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, 
                                SaveProfilePort saveProfilePort, 
                                ExistsByEmailPort existsByEmailPort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public Profile execute(UpdateProfileCommand command) {
        String id = Objects.requireNonNull(command.id(), "識別碼不能為空");
        
        var existingOpt = loadProfilePort.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ProfileNotFoundException("找不到識別碼為 " + id + " 的個人資料");
        }
        
        Profile profile = existingOpt.get();
        
        // Check email uniqueness excluding current profile
        if (!profile.email().address().equals(command.email())) {
            if (existsByEmailPort.existsByEmail(command.email())) {
                throw new DuplicateEmailException("電子郵件已存在: " + command.email());
            }
        }
        
        Email newEmail = new Email(command.email());
        DisplayName newName = new DisplayName(command.displayName());
        
        return saveProfilePort.save(profile.changeDisplayName(newName).changeEmail(newEmail));
    }
}
