package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

import jakarta.validation.Valid;

/**
 * 個人資料的 REST API Controller。
 */
@RestController
@RequestMapping("/profiles")
class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    ProfileController(CreateProfileUseCase createProfileUseCase,
            GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    /**
     * 建立新的個人資料。
     *
     * @param request 包含個人資料資訊的請求 DTO
     * @return 201 Created 與新資源的 Location Header
     */
    @PostMapping
    ResponseEntity<Void> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        // 將 Web DTO 轉換為 Application Port DTO
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest useCaseRequest = 
                new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest(
                        request.email(),
                        request.name(),
                        request.age()
                );
        Profile profile = createProfileUseCase.execute(useCaseRequest);
        URI location = URI.create("/profiles/" + profile.id().getValue());
        return created(location).build();
    }

    /**
     * 查詢個人資料。
     *
     * @param profileId 個人資料 ID
     * @return 200 OK 與個人資料內容
     */
    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getProfile(@PathVariable String profileId) {
        Profile profile = getProfileUseCase.execute(profileId);
        return ok(new ProfileResponse(profile));
    }

    /**
     * 更新個人資料。
     *
     * @param profileId 個人資料 ID
     * @param request 包含更新資訊的請求 DTO
     * @return 200 OK 與更新後的個人資料內容
     */
    @PutMapping("/{id}")
    ResponseEntity<ProfileResponse> updateProfile(@PathVariable String profileId,
            @Valid @RequestBody UpdateProfileRequest request) {
        // 將 Web DTO 轉換為 Application Port DTO
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest useCaseRequest = 
                new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest(
                        request.email(),
                        request.name(),
                        request.age()
                );
        Profile profile = updateProfileUseCase.execute(profileId, useCaseRequest);
        return ok(new ProfileResponse(profile));
    }

    /**
     * 刪除個人資料。
     *
     * @param profileId 個人資料 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProfile(@PathVariable String profileId) {
        deleteProfileUseCase.execute(profileId);
        return ResponseEntity.noContent().build();
    }
}
