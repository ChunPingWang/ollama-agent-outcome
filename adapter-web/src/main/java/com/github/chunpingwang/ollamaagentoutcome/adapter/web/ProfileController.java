package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 個人資料的 REST Controller，負責將 HTTP 請求映射至 Application Inbound Ports。
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

    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = new DisplayName(request.getDisplayName());
        
        ProfileId id = createProfileUseCase.execute(email, displayName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/profiles/" + id.id().toString());
        
        return new ResponseEntity<>(new ProfileResponse(id.id().toString(), email.value(), displayName.isPresent() ? displayName.value() : ""), headers, HttpStatus.CREATED);
    }

    @GetMapping(path =("/{id}"), produces = "application/json")
    ResponseEntity<ProfileResponse> getById(@PathVariable("id") String idStr) {
        var profileOpt = getProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)));
        
        if (profileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profile = profileOpt.get();
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId().id().toString(),
                profile.getEmailValue(),
                profile.getDisplayNameValue()
        ));
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    ResponseEntity<Void> update(@PathVariable("id") String idStr, 
                                @Valid @RequestBody UpdateProfileRequest request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = new DisplayName(request.getDisplayName());
        
        updateProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)), email, displayName);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        deleteProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)));
        
        return ResponseEntity.noContent().build();
    }
}
