package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static java.util.Objects.requireNonNullElse;

import org.springframework.http.HttpHeaders;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
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

    public ProfileController(CreateProfileUseCase createProfileUseCase,
                             GetProfileUseCase getProfileUseCase,
                             UpdateProfileUseCase updateProfileUseCase,
                             DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<Void> create(@Validated @RequestBody CreateProfileRequestDto dto) {
        var email = new Email(dto.email());
        var displayName = requireNonNullElse(dto.displayName(), "").isBlank() ? null : new DisplayName(dto.displayName());
        
        createProfileUseCase.execute(new CreateProfileRequest(email, displayName));
        
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, "/profiles").build();
    }

    @GetMapping(path = "{id}", produces = "application/json")
    ResponseEntity<GetProfileResponseDto> getById(@PathVariable("id") String idStr) {
        var profileResult = getProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)));
        
        var response = new GetProfileResponseDto(
                profileResult.id().value(),
                profileResult.email().value(),
                profileResult.displayName() != null ? profileResult.displayName().value() : null
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "{id}", consumes = "application/json", produces = "application/json")
    ResponseEntity<Void> update(@PathVariable("id") String idStr, 
                                @Validated @RequestBody UpdateProfileRequestDto dto) {
        var id = new ProfileId(java.util.UUID.fromString(idStr));
        var displayName = requireNonNullElse(dto.displayName(), "").isBlank() ? null : new DisplayName(dto.displayName());
        
        updateProfileUseCase.execute(new UpdateProfileRequest(id, displayName));
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        var id = new ProfileId(java.util.UUID.fromString(idStr));
        deleteProfileUseCase.execute(id);
        
        return ResponseEntity.noContent().build();
    }
}
