package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

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

/**
 * 個人資料的 REST Controller。
 */
@RestController
@RequestMapping("/api/profiles")
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
    ResponseEntity<Void> createProfile(@RequestBody CreateProfileRequest request) {
        CreateProfileCommand command = new CreateProfileCommand(request.name(), request.email());
        Profile createdProfile = createProfileUseCase.execute(command);
        URI location = URI.create("/api/profiles/" + createdProfile.id().value());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID id) {
        GetProfileCommand command = new GetProfileCommand(id);
        Profile profile = getProfileUseCase.execute(command);
        ProfileResponse response = new ProfileResponse(profile.id().value(), profile.name(), profile.email().value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<ProfileResponse> updateProfile(@PathVariable UUID id, @RequestBody UpdateProfileRequest request) {
        UpdateProfileCommand command = new UpdateProfileCommand(id, request.name(), request.email());
        Profile updatedProfile = updateProfileUseCase.execute(command);
        ProfileResponse response = new ProfileResponse(updatedProfile.id().value(), updatedProfile.name(), updatedProfile.email().value());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
        DeleteProfileCommand command = new DeleteProfileCommand(id);
        deleteProfileUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
