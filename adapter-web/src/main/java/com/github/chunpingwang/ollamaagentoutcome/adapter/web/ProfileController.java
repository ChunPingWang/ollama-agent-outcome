package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
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
        UUID id = UUID.randomUUID();
        createProfileUseCase.execute(id, request.email(), request.displayName());
        
        URI location = URI.create(String.format("/api/profiles/%s", id));
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getById(@PathVariable("id") String idStr) {
        UUID id = UUID.fromString(idStr);
        var profileOpt = getProfileUseCase.findById(new ProfileId(id));
        
        if (profileOpt.isEmpty()) {
            throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id);
        }
        
        var profile = profileOpt.get();
        ProfileResponse response = new ProfileResponse(
            profile.id().id(),
            profile.getEmail().value(),
            profile.getDisplayName().isPresent() ? profile.getDisplayName().value() : null
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String idStr, 
                                @Valid @RequestBody UpdateProfileRequest request) {
        UUID id = UUID.fromString(idStr);
        updateProfileUseCase.execute(id, request.email(), request.displayName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        UUID id = UUID.fromString(idStr);
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
