package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * REST Controller for managing personal profiles.
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
    ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequest request) {
        var id = com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId.generate();
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.Email(request.email());
        var displayName = request.displayName() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(request.displayName()) : null;
        createProfileUseCase.execute(id, email, displayName);
        return ResponseEntity.created(java.net.URI.create("/api/profiles/" + id.id())).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.ProfileData> getById(
            @PathVariable("id") String id) {
        var profileId = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id);
        var data = getProfileUseCase.execute(profileId);
        return ResponseEntity.ok(data);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String id,
                                @Valid @RequestBody UpdateProfileRequest request) {
        var profileId = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id);
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.Email(request.email());
        var displayName = request.displayName() != null ? new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(request.displayName()) : null;
        updateProfileUseCase.execute(profileId, email, displayName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id) {
        var profileId = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id);
        deleteProfileUseCase.execute(profileId);
        return ResponseEntity.noContent().build();
    }
}
