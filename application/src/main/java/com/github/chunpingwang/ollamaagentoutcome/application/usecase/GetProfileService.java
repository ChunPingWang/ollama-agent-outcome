package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的應用層服務實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    /**
     * 根據識別碼查詢個人資料。
     */
    @Override
    public java.util.Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> findById(String id) {
        return loadProfilePort.findById(id);
    }
}
