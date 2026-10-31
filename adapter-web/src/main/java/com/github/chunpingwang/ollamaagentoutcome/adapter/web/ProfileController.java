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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

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

    @PostMapping
    ResponseEntity<ProfileResponse> createProfile(@RequestBody CreateProfileRequest request) {
        ProfileId profileId = createProfileUseCase.create(request);
        URI location = URI.create("/profiles/" + profileId.getValue());
        return ResponseEntity.created(location)
                .body(new ProfileResponse(profileId.getValue(), null, null, null));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID id) {
        ProfileId profileId = new ProfileId(id);
        Profile profile = getProfileUseCase.getProfile(profileId);
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId().getValue(),
                profile.getEmail().getValue(),
                profile.getName().getValue(),
                profile.getAge().getValue()
        ));
    }

    @PutMapping("/{id}")
    ResponseEntity<ProfileResponse> updateProfile(@PathVariable UUID id,
                                                  @RequestBody UpdateProfileRequest request) {
        ProfileId profileId = new ProfileId(id);
        Profile profile = updateProfileUseCase.updateProfile(profileId, request);
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId().getValue(),
                profile.getEmail().getValue(),
                profile.getName().getValue(),
                profile.getAge().getValue()
        ));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
        ProfileId profileId = new ProfileId(id);
        deleteProfileUseCase.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}
