package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileRestController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @MockitoBean
    private GetProfileUseCase getProfileUseCase;

    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // Arrange
        UUID newId = UUID.randomUUID();
        String jsonBody = """
            {"name": "John Doe", "email": "john@example.com"}
            """;
        
        when(createProfileUseCase.execute(any(CreateProfileUseCase.CreateProfileCommand.class)))
                .thenReturn(new ProfileId(newId));

        // Act & Assert
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(newId.toString()));
    }

    @Test
    void should_reject_invalid_input_on_create() throws Exception {
        // Arrange: Empty name and invalid email format will trigger Jakarta Validation errors in DTO or Domain validation.
        // Assuming DTO validation handles basic constraints, or Controller calls UseCase which validates.
        // Let's test 400 for bad JSON structure first (handled by Spring), then domain/validation error.
        // Here we simulate a case where the request is valid syntax but fails business logic? 
        // No, T6 asks to verify HTTP status codes. Let's assume DTO has @NotBlank etc.
        // If DTO validation fails, it returns 400 automatically via GlobalExceptionHandler handling MethodArgumentNotValidException.
        
        String jsonBody = """
            {"name": "", "email": "invalid-email"}
            """;

        // Act & Assert
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_409_when_duplicate_email_during_create() throws Exception {
        // Arrange
        String jsonBody = """
            {"name": "John Doe", "email": "john@example.com"}
            """;
            
        doThrow(new DuplicateEmailException())
            .when(createProfileUseCase).execute(any(CreateProfileUseCase.CreateProfileCommand.class));

        // Act & Assert
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("email already exists"));
    }

    @Test
    void should_get_profile_and_return_200() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Name name = new Name("Jane Doe");
        Email email = new Email("jane@example.com");
        
        GetProfileUseCase.GetProfileResult result = new GetProfileUseCase.GetProfileResult(name, email);
        
        when(getProfileUseCase.execute(any(GetProfileUseCase.GetProfileQuery.class)))
                .thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/profiles/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void should_return_404_when_profile_not_found_on_get() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        ProfileId profileId = new ProfileId(nonExistentId);
        
        doThrow(new ProfileNotFoundException(profileId))
            .when(getProfileUseCase).execute(any(GetProfileUseCase.GetProfileQuery.class));

        // Act & Assert
        mockMvc.perform(get("/profiles/{id}", nonExistentId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists()); // Message content depends on handler implementation
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        String jsonBody = """
            {"name": "Updated Name", "email": "updated@example.com"}
            """;
            
        doNothing().when(updateProfileUseCase).execute(any(UpdateProfileUseCase.UpdateProfileCommand.class));

        // Act & Assert
        mockMvc.perform(put("/profiles/{id}", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());
                
        verifyNoInteractions(createProfileUseCase, getProfileUseCase, deleteProfileUseCase);
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        
        doNothing().when(deleteProfileUseCase).execute(any(DeleteProfileUseCase.DeleteProfileCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/profiles/{id}", id.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        ProfileId profileId = new ProfileId(nonExistentId);
        
        doThrow(new ProfileNotFoundException(profileId))
            .when(deleteProfileUseCase).execute(any(DeleteProfileUseCase.DeleteProfileCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/profiles/{id}", nonExistentId.toString()))
                .andExpect(status().isNotFound());
    }
}
