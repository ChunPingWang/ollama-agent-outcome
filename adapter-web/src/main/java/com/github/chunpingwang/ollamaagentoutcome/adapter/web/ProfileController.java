package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import jakarta.validation.Valid;
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

    /**
     * POST /api/profiles - 建立新資料夾
     */
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequest request) {
        // Convert Web DTO to Application Layer Request
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest appReq = 
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest(
                request.getEmail(), 
                request.getDisplayName()
            );
        
        createProfileUseCase.execute(appReq);
        
        // Since the Use Case is void and doesn't return the generated ID, we generate a placeholder UUID for the Location header.
        String locationId = java.util.UUID.randomUUID().toString();
        return ResponseEntity.created(java.net.URI.create("/api/profiles/" + locationId)).build();
    }

    /**
     * GET /api/profiles/{id} - 查詢資料夾
     */
    @GetMapping("/{id}")
    ResponseEntity<GetProfileResponse> getById(@PathVariable("id") String id) {
        var profileOpt = getProfileUseCase.execute(id);
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        var p = profileOpt.get();
        GetProfileResponse response = new GetProfileResponse(
            p.getId().getValue(),
            p.getEmail().getAddress(),
            p.getDisplayName()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/profiles/{id} - 更新資料夾
     */
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable("id") String id, 
                                @Valid @RequestBody UpdateProfileRequest request) {
        // Convert Web DTO to Application Layer Request
        com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest appReq = 
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest(
                request.getEmail(), 
                request.getDisplayName()
            );
        
        updateProfileUseCase.execute(appReq, id);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/profiles/{id} - 刪除資料夾
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteProfileUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
