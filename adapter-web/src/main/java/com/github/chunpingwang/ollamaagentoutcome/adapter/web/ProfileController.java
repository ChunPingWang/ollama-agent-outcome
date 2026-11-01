package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
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

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank()) 
                ? new DisplayName(request.getDisplayName()) : new DisplayName((String) null);
        
        ProfileId id = createProfileUseCase.execute(email, displayName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(org.springframework.web.util.UriComponentsBuilder.fromPath("/api/profiles/" + id.id()).build().toUri());
        
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(new ProfileResponse(id.id().toString(), email.value(), displayName.isSet() ? displayName.value() : null));
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<ProfileResponse> getById(@PathVariable("id") String idStr) {
        var profileOpt = getProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)));
        if (profileOpt.isEmpty()) {
            throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(java.util.UUID.fromString(idStr));
        }
        var profile = profileOpt.get();
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId().id().toString(),
                profile.getEmail().value(),
                profile.getDisplayName().isSet() ? profile.getDisplayName().value() : null
        ));
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> update(@PathVariable("id") String idStr, @Valid @RequestBody UpdateProfileRequest request) {
        Email email = new Email(request.getEmail());
        DisplayName displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank()) 
                ? new DisplayName(request.getDisplayName()) : new DisplayName((String) null);
        
        try {
            updateProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)), email, displayName);
        } catch (com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException e) {
            throw e;
        } catch (com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException e) {
             throw e;
        }
        
        // PUT 成功回傳 200 OK，符合 RESTful 慣例與 Spec 定義
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String idStr) {
        try {
            deleteProfileUseCase.execute(new ProfileId(java.util.UUID.fromString(idStr)));
        } catch (com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException e) {
            throw e;
        }
        return ResponseEntity.noContent().build();
    }
}
