package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
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
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        CreateProfileRequest request = new CreateProfileRequest("user@example.com", "John Doe", 25);
        when(createProfileUseCase.create(any(CreateProfileRequest.class))).thenReturn(profileId);

        // When & Then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","name":"John Doe","age":25}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void should_get_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile profile = new Profile(
                profileId,
                new Email("user@example.com"),
                new Name("John Doe"),
                new Age(25)
        );
        when(getProfileUseCase.getProfile(profileId)).thenReturn(profile);

        // When & Then
        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        UpdateProfileRequest request = new UpdateProfileRequest("new@example.com", "New Name", 26);
        Profile profile = new Profile(
                profileId,
                new Email("new@example.com"),
                new Name("New Name"),
                new Age(26)
        );
        when(updateProfileUseCase.updateProfile(any(ProfileId.class), any(UpdateProfileRequest.class))).thenReturn(profile);

        // When & Then
        mockMvc.perform(put("/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@example.com","name":"New Name","age":26}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.age").value(26));
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        doNothing().when(deleteProfileUseCase).deleteProfile(profileId);

        // When & Then
        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_400_when_dto_validation_fails() throws Exception {
        // Given
        String invalidJson = """
                {"email":"","name":"","age":-1}
                """;

        // When & Then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(getProfileUseCase.getProfile(profileId)).thenThrow(new ProfileNotFoundException("Profile not found"));

        // When & Then
        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Profile not found"));
    }

    @Test
    void should_return_409_when_duplicate_email() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        CreateProfileRequest request = new CreateProfileRequest("duplicate@example.com", "John Doe", 25);
        when(createProfileUseCase.create(any(CreateProfileRequest.class))).thenThrow(new DuplicateEmailException("Email already exists"));

        // When & Then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"duplicate@example.com","name":"John Doe","age":25}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void should_return_400_when_other_domain_exception() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        CreateProfileRequest request = new CreateProfileRequest("user@example.com", "John Doe", 25);
        when(createProfileUseCase.create(any(CreateProfileRequest.class))).thenThrow(new InvalidAgeException("Invalid age"));

        // When & Then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","name":"John Doe","age":25}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid age"));
    }
}
