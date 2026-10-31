package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 刪除個人資料的使用案例實作。
 */
public class DeleteProfileService implements DeleteProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final RemoveProfilePort removeProfilePort;

    public DeleteProfileService(FindProfileByIdPort findProfileByIdPort, RemoveProfilePort removeProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.removeProfilePort = removeProfilePort;
    }

    @Override
    public void execute(ProfileId id) {
        // 先檢查是否存在，若不存在則拋出例外
        if (findProfileByIdPort.findById(id).isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        // 確認存在後執行移除
        removeProfilePort.removeById(id);
    }
}
