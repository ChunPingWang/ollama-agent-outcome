package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.net.URI;
import java.util.UUID;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 個人資料的 REST API Controller。
 */
@RestController
@RequestMapping("/profiles")
class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final LoadProfileUseCase loadProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    public ProfileController(CreateProfileUseCase createProfileUseCase,
                             LoadProfileUseCase loadProfileUseCase,
                             UpdateProfileUseCase updateProfileUseCase,
                             DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.loadProfileUseCase = loadProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    ResponseEntity<Void> create(@Validated @RequestBody CreateProfileRequest request) {
        Name name = new Name(request.name());
        Email email = new Email(request.email());
        
        var command = new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand(name, email);
        var profileId = createProfileUseCase.execute(command);
        
        URI location = URI.create("/profiles/" + profileId.value().toString());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> get(@PathVariable("id") String idStr) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId id = new ProfileId(uuid);
        
        var result = loadProfileUseCase.execute(id);
        ProfileResponse response = new ProfileResponse(
            id.value(), 
            result.name().text(), 
            result.email().address()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String idStr, 
                                @Validated @RequestBody UpdateProfileRequest request) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId id = new ProfileId(uuid);
        Name name = new Name(request.name());
        Email email = new Email(request.email());
        
        var command = new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand(id, name, email);
        updateProfileUseCase.execute(command);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        UUID uuid = UUID.fromString(idStr);
        ProfileId id = new ProfileId(uuid);
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
