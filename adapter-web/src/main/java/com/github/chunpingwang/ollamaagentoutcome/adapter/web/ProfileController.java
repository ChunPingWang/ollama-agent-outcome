package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.springframework.http.HttpStatus.*;

import java.net.URI;

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

/**
 * 個人資料 REST API Controller。
 */
@RestController
@RequestMapping("/profiles")
class ProfileController {

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
    ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequestDTO request) {
        createProfileUseCase.execute(new CreateProfileUseCase.CreateProfileRequest(request.name(), request.email()));
        // Return 201 Created with Location header
        return ResponseEntity.created(URI.create("/profiles")).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponseDTO> getById(@PathVariable("id") String id) {
        var profile = getProfileUseCase.execute(id);
        var response = new ProfileResponseDTO(profile.id().value(), profile.name().text(), profile.email().address());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String id, 
                                @Valid @RequestBody UpdateProfileRequestDTO request) {
        if (!request.id().equals(id)) {
            throw new IllegalArgumentException("路徑中的 ID 與請求內容的 ID 不一致");
        }
        updateProfileUseCase.execute(new UpdateProfileUseCase.UpdateProfileRequest(id, request.name(), request.email()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
