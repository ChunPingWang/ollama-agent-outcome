package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * 個人資料 REST API Controller。
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileRestController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileRestController(CreateProfileUseCase createProfileUseCase,
                                 GetProfileUseCase getProfileUseCase,
                                 UpdateProfileUseCase updateProfileUseCase,
                                 DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@RequestBody CreateProfileRequest request) {
        var command = new CreateProfileCommand(request.email(), request.displayName());
        Profile profile = createProfileUseCase.execute(command);
        
        URI location = URI.create("/api/profiles/" + profile.id());
        return ResponseEntity.created(location).body(toResponse(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable("id") String id) {
        return getProfileUseCase.execute(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> update(@PathVariable("id") String id, 
                                                  @RequestBody UpdateProfileRequest request) {
        // 注意：UpdateProfileRequest 不需要包含 id，因為 id 來自路徑
        var command = new UpdateProfileCommand(id, request.email(), request.displayName());
        Profile profile = updateProfileUseCase.execute(command);
        return ResponseEntity.ok(toResponse(profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    private static ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(profile.id(), profile.email().address(), profile.displayName().name());
    }
}
