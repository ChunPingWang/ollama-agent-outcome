package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
@Import(ProfileControllerTest.TestConfig.class)
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

    @TestConfiguration
    static class TestConfig {
        @Bean
        ProfileController profileController(CreateProfileUseCase createProfileUseCase, GetProfileUseCase getProfileUseCase,
                UpdateProfileUseCase updateProfileUseCase, DeleteProfileUseCase deleteProfileUseCase) {
            return new ProfileController(createProfileUseCase, getProfileUseCase, updateProfileUseCase, deleteProfileUseCase);
        }
    }

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(createProfileUseCase.execute(any(com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest.class))).thenReturn(profileId);

        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "王小明",
                          "email": "xiaoming@example.com",
                          "note": "測試"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/profiles/" + id.toString()));
    }

    @Test
    void should_return_400_when_request_body_is_invalid() throws Exception {
        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "",
                          "email": "invalid-email"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_request_body_is_missing() throws Exception {
        // When & Then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_200_when_get_profile_exists() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile profile = new Profile(profileId, "王小明", "xiaoming@example.com", "測試");
        when(getProfileUseCase.execute(any(ProfileId.class))).thenReturn(profile);

        // When & Then
        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("王小明"))
                .andExpect(jsonPath("$.email").value("xiaoming@example.com"))
                .andExpect(jsonPath("$.note").value("測試"));
    }

    @Test
    void should_return_404_when_get_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(getProfileUseCase.execute(any(ProfileId.class))).thenThrow(new ProfileNotFoundException("Profile not found"));

        // When & Then
        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile profile = new Profile(profileId, "王大明", "new@example.com", "新備註");
        when(updateProfileUseCase.execute(any(ProfileId.class), any(com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest.class))).thenReturn(profile);

        // When & Then
        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "王大明",
                          "email": "new@example.com",
                          "note": "新備註"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("王大明"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void should_return_404_when_update_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(updateProfileUseCase.execute(any(ProfileId.class), any(com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest.class)))
                .thenThrow(new ProfileNotFoundException("Profile not found"));

        // When & Then
        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "王大明",
                          "email": "new@example.com",
                          "note": "新備註"
                        }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_409_when_update_profile_duplicate_email() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(updateProfileUseCase.execute(any(ProfileId.class), any(com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        // When & Then
        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "王大明",
                          "email": "duplicate@example.com",
                          "note": "新備註"
                        }
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        doNothing().when(deleteProfileUseCase).execute(any(ProfileId.class));

        // When & Then
        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_404_when_delete_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        doThrow(new ProfileNotFoundException("Profile not found")).when(deleteProfileUseCase).execute(any(ProfileId.class));

        // When & Then
        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }
}
