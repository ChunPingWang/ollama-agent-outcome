package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 個人資料的 REST Controller。
 */
@RestController
@RequestMapping("/profiles")
class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    ProfileController(CreateProfileUseCase createProfileUseCase, GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase, DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    ResponseEntity<Void> createProfile(@RequestBody @Valid CreateProfileRequest request) {
        ProfileId id = createProfileUseCase.execute(new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest(
                request.name(), request.email(), request.note()));
        URI location = URI.create("/profiles/" + id.value());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    ProfileResponse getProfile(@PathVariable("id") UUID id) {
        return ProfileResponse.from(getProfileUseCase.execute(new ProfileId(id)));
    }

    @PutMapping("/{id}")
    ProfileResponse updateProfile(@PathVariable("id") UUID id, @RequestBody @Valid UpdateProfileRequest request) {
        return ProfileResponse.from(updateProfileUseCase.execute(new ProfileId(id),
                new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest(
                        request.name(), request.email(), request.note())));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProfile(@PathVariable("id") UUID id) {
        deleteProfileUseCase.execute(new ProfileId(id));
        return ResponseEntity.noContent().build();
    }
}
