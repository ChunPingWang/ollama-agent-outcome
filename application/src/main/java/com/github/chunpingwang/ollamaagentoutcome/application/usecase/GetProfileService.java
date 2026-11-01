package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import java.util.Optional;

/**
 * 查詢個人資料的應用層服務實作。若找不到則回傳空 Optional。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    /**
     * 根據識別碼查詢個人資料。直接將 Port 的結果向上傳遞，由 Controller 決定是否拋出例外。
     */
    @Override
    public Optional<Profile> execute(String id) {
        return loadProfilePort.findById(id);
    }
}
