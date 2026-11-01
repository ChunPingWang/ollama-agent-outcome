package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import jakarta.validation.Valid;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.CreateProfileRequestDto;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.ProfileResponseDto;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.UpdateProfileRequestDto;

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
    ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequestDto dto) {
        createProfileUseCase.execute(dto.getEmail(), dto.getDisplayName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponseDto> getById(@PathVariable("id") UUID id) {
        var profile = getProfileUseCase.execute(id)
                .orElseThrow(() -> new ProfileNotFoundException("profile not found: " + id));
        
        String displayName = profile.displayName() != null ? profile.displayName().value() : null;
        ProfileResponseDto response = new ProfileResponseDto(
            profile.id().id().toString(),
            profile.email().value(),
            displayName
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") UUID id,
                                @Valid @RequestBody UpdateProfileRequestDto dto) {
        updateProfileUseCase.execute(id, dto.getEmail(), dto.getDisplayName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
