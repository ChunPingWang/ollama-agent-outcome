package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import jakarta.validation.Valid;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.GetProfileResponse;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.UpdateProfileRequest;

@RestController
@RequestMapping("/profiles")
public class ProfileRestController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteProfileUseCase deleteProfileUseCase;

    ProfileRestController(CreateProfileUseCase createProfileUseCase,
                          GetProfileUseCase getProfileUseCase,
                          UpdateProfileUseCase updateProfileUseCase,
                          DeleteProfileUseCase deleteProfileUseCase) {
        this.createProfileUseCase = createProfileUseCase;
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deleteProfileUseCase = deleteProfileUseCase;
    }

    @PostMapping
    ResponseEntity<GetProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        var command = new CreateProfileUseCase.CreateProfileCommand(request.name(), request.email());
        var profileId = createProfileUseCase.execute(command);
        
        // Construct response immediately after creation since we have the ID and input data
        // In a real scenario with DB persistence, we might fetch it back or just return the created structure.
        // Here we construct it from the request to ensure consistency without extra calls if not needed, 
        // but strictly speaking, the controller should probably map the result of execution.
        // However, execute returns only ID. To return full details in 201, we can reconstruct or assume success implies valid state.
        // Let's return a constructed response based on input + generated ID for simplicity as per typical REST patterns when no refetch is done.
        GetProfileResponse response = new GetProfileResponse(
            profileId.id(),
            request.name(),
            request.email()
        );
        
        URI location = URI.create("/profiles/" + profileId.id().toString());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<GetProfileResponse> getProfile(@PathVariable("id") UUID id) {
        var query = new GetProfileUseCase.GetProfileQuery(new ProfileId(id));
        var result = getProfileUseCase.execute(query);
        
        GetProfileResponse response = new GetProfileResponse(
            id,
            result.name().value(),
            result.email().value()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateProfile(@PathVariable("id") UUID id, 
                                       @Valid @RequestBody UpdateProfileRequest request) {
        var command = new UpdateProfileUseCase.UpdateProfileCommand(
            new ProfileId(id),
            request.name(),
            request.email()
        );
        updateProfileUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProfile(@PathVariable("id") UUID id) {
        var command = new DeleteProfileUseCase.DeleteProfileCommand(new ProfileId(id));
        deleteProfileUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
