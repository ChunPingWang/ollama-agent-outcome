package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

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
 * 個人資料的 REST API 控制器。
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
    ResponseEntity<Void> create(@Valid @RequestBody CreateProfileRequest request) {
        createProfileUseCase.create(request.toUseCaseRequest());
        return ResponseEntity.created(URI.create("/profiles")).build();
    }

    @GetMapping("/{id}")
    ProfileResponse get(@PathVariable("id") String id) {
        return new ProfileResponse(getProfileUseCase.get(id));
    }

    @PutMapping("/{id}")
    ProfileResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request) {
        return new ProfileResponse(updateProfileUseCase.update(id, request.toUseCaseRequest()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteProfileUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
