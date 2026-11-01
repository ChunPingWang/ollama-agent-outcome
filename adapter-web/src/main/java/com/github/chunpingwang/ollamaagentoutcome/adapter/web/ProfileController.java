package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileDisplayNameUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 個人資料的 REST Controller。
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileDisplayNameUseCase updateProfileDisplayNameUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileController(CreateProfileUseCase createProfileUseCase,
                             GetProfileUseCase getProfileUseCase,
                             UpdateProfileDisplayNameUseCase updateProfileDisplayNameUseCase,
                             DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileDisplayNameUseCase = updateProfileDisplayNameUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    ResponseEntity<Void> create(@Validated @Valid @RequestBody CreateProfileRequest request) {
        ProfileId id = createProfileUseCase.execute(request.getEmail(), request.getDisplayName());
        return ResponseEntity.created(java.net.URI.create("/api/profiles/" + id.value())).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getById(@PathVariable("id") String id) {
        var profile = getProfileUseCase.execute(new ProfileId(id));
        var response = new ProfileResponse(
            profile.id().value(),
            profile.email().address(),
            profile.displayName() != null ? profile.displayName().name() : null
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String id,
                                @Validated @Valid @RequestBody UpdateProfileRequest request) {
        DisplayName displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank()) 
                ? new DisplayName(request.getDisplayName()) 
                : null;
        updateProfileDisplayNameUseCase.execute(new ProfileId(id), displayName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteProfileUseCase.execute(new ProfileId(id));
        return ResponseEntity.noContent().build();
    }
}
