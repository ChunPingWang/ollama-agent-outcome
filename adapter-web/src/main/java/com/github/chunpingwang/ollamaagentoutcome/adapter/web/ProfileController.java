package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.status;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 個人資料的 REST Controller。
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
    ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank()) 
            ? new DisplayName(request.getDisplayName()) 
            : new DisplayName("");
        
        Profile profile = createProfileUseCase.execute(email, displayName);
        
        return status(CREATED).location(URI.create("/api/profiles/" + profile.getId().id())).body(toResponse(profile));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getById(@PathVariable("id") String idStr) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId profileId = new ProfileId(uuid);
        
        return getProfileUseCase.execute(profileId)
                .map(p -> ResponseEntity.ok().body(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String idStr,
                                @Valid @RequestBody UpdateProfileRequest request) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId profileId = new ProfileId(uuid);
        Email email = new Email(request.getEmail());
        DisplayName displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank()) 
            ? new DisplayName(request.getDisplayName()) 
            : new DisplayName("");
            
        updateProfileUseCase.execute(profileId, email, displayName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId profileId = new ProfileId(uuid);
        
        deleteProfileUseCase.execute(profileId);
        return ResponseEntity.noContent().build();
    }

    private ProfileResponse toResponse(Profile profile) {
        String displayNameValue = (profile.getDisplayName() != null && !profile.getDisplayName().value().isBlank()) 
            ? profile.getDisplayName().value() : "";
            
        return new ProfileResponse(
            profile.getId().id().toString(),
            profile.getEmail().value(),
            displayNameValue
        );
    }
}
