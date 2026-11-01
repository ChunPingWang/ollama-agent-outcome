package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
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
 * 個人資料的 REST API Controller。
 */
@RestController
@RequestMapping("/profiles")
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

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequestDto request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = request.getDisplayName() != null ? new DisplayName(request.getDisplayName()) : null;
        
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest appReq = 
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest(email, displayName);
            
        createProfileUseCase.execute(appReq);
        
        URI location = URI.create("/profiles");
        return ResponseEntity.status(HttpStatus.CREATED).location(location).build();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<GetProfileResponseDto> get(@PathVariable("id") String idStr) {
        UUID id = UUID.fromString(idStr);
        ProfileResult result = getProfileUseCase.execute(new ProfileId(id));
        
        GetProfileResponseDto response = new GetProfileResponseDto(
                result.id().value(),
                result.email().value(),
                result.displayName() != null ? result.displayName().value() : null
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> update(@PathVariable("id") String idStr,
                                       @Valid @RequestBody UpdateProfileRequestDto request) {
        UUID profileId = UUID.fromString(idStr);
        DisplayName displayName = request.getDisplayName() != null ? new DisplayName(request.getDisplayName()) : null;
        
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest appReq = 
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest(new ProfileId(profileId), displayName);
            
        updateProfileUseCase.execute(appReq);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        UUID id = UUID.fromString(idStr);
        deleteProfileUseCase.execute(new ProfileId(id));
        
        return ResponseEntity.noContent().build();
    }
}
