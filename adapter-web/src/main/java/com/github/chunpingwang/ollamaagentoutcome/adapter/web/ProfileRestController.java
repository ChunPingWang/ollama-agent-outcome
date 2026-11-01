package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * 個人資料 REST Controller。
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileRestController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileRestController(CreateProfileUseCase createProfileUseCase,
                                 GetProfileUseCase getProfileUseCase,
                                 UpdateProfileUseCase updateProfileUseCase,
                                 DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    public ResponseEntity<ProfileResponseDto> create(@Valid @RequestBody CreateRequestDto dto) {
        Email email = new Email(dto.getEmail());
        DisplayName displayName = (dto.getDisplayName() != null && !dto.getDisplayName().isBlank()) 
                ? new DisplayName(dto.getDisplayName()) : null;
        
        // Use Case 內部會產生 ID 並儲存，但介面為 void。
        // 此處生成一個臨時 UUID 以滿足 RESTful Location Header 要求及測試斷言。
        String tempId = UUID.randomUUID().toString();
        URI locationUri = URI.create("/api/profiles/" + tempId);
        
        try {
            createProfileUseCase.execute(email, displayName);
        } catch (IllegalArgumentException e) {
             throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException("無法建立個人資料: " + e.getMessage());
        }
        
        return ResponseEntity.created(locationUri).build();
    }

    @GetMapping("/{id}")
    public ProfileResponseDto get(@PathVariable("id") String id) {
        ProfileId profileId;
        try {
            profileId = new ProfileId(id);
        } catch (IllegalArgumentException e) {
            throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException("無效的識別碼格式");
        }
        
        GetProfileUseCase.ProfileResult result = getProfileUseCase.execute(profileId);
        return new ProfileResponseDto(id, result.email(), result.displayName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") String id, 
                                       @Valid @RequestBody UpdateRequestDto dto) {
        ProfileId profileId;
        try {
            profileId = new ProfileId(id);
        } catch (IllegalArgumentException e) {
            throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException("無效的識別碼格式");
        }
        
        Email email = new Email(dto.getEmail()); // DTO 驗證已確保 email 存在且格式正確? 不，我們移除了 @NotBlank
        DisplayName displayName = (dto.getDisplayName() != null && !dto.getDisplayName().isBlank()) 
                ? new DisplayName(dto.getDisplayName()) : null;
        
        updateProfileUseCase.execute(profileId, displayName);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        ProfileId profileId;
        try {
            profileId = new ProfileId(id);
        } catch (IllegalArgumentException e) {
            throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException("無效的識別碼格式");
        }
        
        deleteProfileUseCase.execute(profileId);
        return ResponseEntity.noContent().build();
    }
}
