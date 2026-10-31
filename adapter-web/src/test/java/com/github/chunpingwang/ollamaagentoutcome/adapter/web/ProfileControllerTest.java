package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidAgeException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
@Import(ProfileControllerAdvice.class)
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
    void should_create_profile_successfully() throws Exception {
        // Given
        String email = "user@example.com";
        String name = "測試使用者";
        int age = 25;
        String newId = UUID.randomUUID().toString();

        ProfileId profileId = new ProfileId(newId);
        Email emailValue = new Email(email);
        Name nameValue = new Name(name);
        Age ageValue = new Age(age);
        Profile profile = new Profile(profileId, emailValue, nameValue, ageValue);

        when(createProfileUseCase.execute(any())).thenReturn(profile);

        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "user@example.com",
                          "name": "測試使用者",
                          "age": 25
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void should_create_profile_with_duplicate_email() throws Exception {
        // Given
        String email = "duplicate@example.com";
        doThrow(new DuplicateEmailException(email)).when(createProfileUseCase).execute(any());

        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "duplicate@example.com",
                          "name": "重複使用者",
                          "age": 30
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_create_profile_with_invalid_email() throws Exception {
        // Given
        String email = "invalid-email";
        doThrow(new InvalidEmailException("Invalid email format")).when(createProfileUseCase).execute(any());

        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "invalid-email",
                          "name": "測試使用者",
                          "age": 25
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_create_profile_with_invalid_age() throws Exception {
        // Given
        String email = "new@example.com";
        doThrow(new InvalidAgeException("Age must be a positive integer")).when(createProfileUseCase).execute(any());

        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "new@example.com",
                          "name": "測試使用者",
                          "age": -5
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_get_profile_successfully() throws Exception {
        // Given
        String profileId = "profile-123";
        String email = "user@example.com";
        String name = "測試使用者";
        int age = 25;

        ProfileId id = new ProfileId(profileId);
        Email emailValue = new Email(email);
        Name nameValue = new Name(name);
        Age ageValue = new Age(age);
        Profile profile = new Profile(id, emailValue, nameValue, ageValue);

        when(getProfileUseCase.execute(profileId)).thenReturn(profile);

        // When & Then
        mockMvc.perform(get("/profiles/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    void should_get_profile_not_found() throws Exception {
        // Given
        String profileId = "non-existent-id";
        doThrow(new ProfileNotFoundException(profileId)).when(getProfileUseCase).execute(eq(profileId));

        // When & Then
        mockMvc.perform(get("/profiles/{id}", profileId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_update_profile_successfully() throws Exception {
        // Given
        String profileId = "profile-123";
        String newEmail = "new@example.com";
        String newName = "新名稱";
        int newAge = 26;

        ProfileId id = new ProfileId(profileId);
        Email emailValue = new Email(newEmail);
        Name nameValue = new Name(newName);
        Age ageValue = new Age(newAge);
        Profile profile = new Profile(id, emailValue, nameValue, ageValue);

        when(updateProfileUseCase.execute(eq(profileId), any())).thenReturn(profile);

        // When & Then
        mockMvc.perform(put("/profiles/{id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "new@example.com",
                          "name": "新名稱",
                          "age": 26
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.age").value(newAge));
    }

    @Test
    void should_update_profile_not_found() throws Exception {
        // Given
        String profileId = "non-existent-id";
        doThrow(new ProfileNotFoundException(profileId)).when(updateProfileUseCase).execute(eq(profileId), any());

        // When & Then
        mockMvc.perform(put("/profiles/{id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "user@example.com",
                          "name": "新名稱",
                          "age": 26
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_update_profile_with_duplicate_email() throws Exception {
        // Given
        String profileId = "profile-123";
        String duplicateEmail = "duplicate@example.com";
        doThrow(new DuplicateEmailException(duplicateEmail)).when(updateProfileUseCase).execute(eq(profileId), any());

        // When & Then
        mockMvc.perform(put("/profiles/{id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "duplicate@example.com",
                          "name": "新名稱",
                          "age": 26
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_delete_profile_successfully() throws Exception {
        // Given
        String profileId = "profile-123";
        doNothing().when(deleteProfileUseCase).execute(eq(profileId));

        // When & Then
        mockMvc.perform(delete("/profiles/{id}", profileId))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_delete_profile_not_found() throws Exception {
        // Given
        String profileId = "non-existent-id";
        doThrow(new ProfileNotFoundException(profileId)).when(deleteProfileUseCase).execute(eq(profileId));

        // When & Then
        mockMvc.perform(delete("/profiles/{id}", profileId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
