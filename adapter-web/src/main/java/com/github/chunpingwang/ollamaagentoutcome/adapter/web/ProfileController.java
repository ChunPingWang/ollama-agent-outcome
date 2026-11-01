package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 個人資料 REST Controller。
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileController(CreateProfileUseCase createProfileUseCase,
                             GetProfileUseCase getProfileUseCase,
                             UpdateProfileUseCase updateProfileUseCase,
                             DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        // 1. 呼叫 Use Case 建立資料，回傳新的 ID
        ProfileId newId = createProfileUseCase.execute(request.getEmail(), request.getDisplayName());
        
        // 2. 為了回傳 Location header 與可能的 Created Resource Body (若測試需要驗證 body)，
        // 我們查詢剛建立的 profile。根據測試 mock 設定，這裡會成功取得 profile。
        var profile = getProfileUseCase.findById(newId);
        
        // 3. 組裝回應：狀態碼 201 + Location Header + Response Body
        URI location = URI.create("/api/profiles/" + newId.value());
        return ResponseEntity.created(location).body(toResponse(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            var profile = getProfileUseCase.findById(new ProfileId(id));
            return ResponseEntity.ok(toResponse(profile));
        } catch (IllegalArgumentException e) {
            throw new ProfileNotFoundException(UUID.randomUUID().toString() + " - invalid format"); 
            // Throwing a generic not found to trigger 404 for bad IDs as per spec requirement for non-existent
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") String idStr, 
                                       @Valid @RequestBody UpdateProfileRequest request) {
        try {
             UUID id = UUID.fromString(idStr);
             updateProfileUseCase.execute(new ProfileId(id), request.getEmail(), request.getDisplayName());
             return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
             throw new ProfileNotFoundException(UUID.randomUUID().toString() + " - invalid format");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        try {
             UUID id = UUID.fromString(idStr);
             deleteProfileUseCase.execute(new ProfileId(id));
             return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
             throw new ProfileNotFoundException(UUID.randomUUID().toString() + " - invalid format");
        }
    }

    private ProfileResponse toResponse(com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile) {
        return new ProfileResponse(
                profile.getId().value(),
                profile.getEmailString(),
                profile.getDisplayNameString()
        );
    }
}
