package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 個人資料 REST Controller。
 */
@RestController
@RequestMapping("/api/profiles")
@Validated
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
    @ResponseStatus(HttpStatus.CREATED)
    void create(@Valid @RequestBody CreateProfileRequest request, HttpServletResponse response) {
        var useCaseReq = new CreateProfileUseCase.CreateProfileRequest(request.getEmail(), request.getDisplayName());
        createProfileUseCase.execute(useCaseReq);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    GetProfileResponse getById(@PathVariable("id") String id) {
        var profile = getProfileUseCase.findById(id).orElseThrow(() -> 
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id));
            
        return new GetProfileResponse(
            profile.id().value(),
            profile.email().address(),
            profile.displayName() != null ? profile.displayName().name() : ""
        );
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    void update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request) {
        var useCaseReq = new UpdateProfileUseCase.UpdateRequest(request.getEmail(), request.getDisplayName());
        updateProfileUseCase.execute(useCaseReq, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") String id) {
        deleteProfileUseCase.deleteById(id);
    }
}
