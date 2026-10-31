package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.net.URI;
import java.util.UUID;

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
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 個人資料 REST API Controller。
 */
@RestController
@RequestMapping("/api/profiles")
class ProfileRestController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileRestController(CreateProfileUseCase createProfileUseCase, GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase, DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody CreateProfileRequestDTO request) {
        var profileId = createProfileUseCase.create(request.email(), request.displayName());
        return ResponseEntity.created(URI.create("/api/profiles/" + profileId.value())).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponseDTO> getById(@PathVariable("id") String id) {
        var profile = getProfileUseCase.getById(new ProfileId(UUID.fromString(id)))
                .orElseThrow(() -> new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(new ProfileId(UUID.fromString(id))));
        
        var response = new ProfileResponseDTO(profile.getId().value().toString(), 
                profile.getEmail().getValue(), 
                profile.getDisplayName() != null ? profile.getDisplayName().getValue() : "");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String id, @RequestBody UpdateProfileRequestDTO request) {
        updateProfileUseCase.update(id, request.email(), request.displayName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
        deleteProfileUseCase.deleteById(new ProfileId(UUID.fromString(id)));
        return ResponseEntity.noContent().build();
    }
}
