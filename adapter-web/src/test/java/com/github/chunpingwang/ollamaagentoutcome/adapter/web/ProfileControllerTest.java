package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
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

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_with_201() throws Exception {
        String email = "user@example.com";
        String displayName = "Test User";
        UUID id = UUID.randomUUID();

        // Use Case 回傳的是 Domain Model (Profile)，不是 DTO
        when(createProfileUseCase.execute(any(Email.class), any(DisplayName.class)))
                .thenReturn(new Profile(new ProfileId(id), new Email(email), new DisplayName(displayName)));

        CreateProfileRequest request = new CreateProfileRequest();
        request.setEmail(email);
        request.setDisplayName(displayName);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.displayName").value(displayName));
    }

    @Test
    void should_return_409_on_duplicate_email() throws Exception {
        String email = "dup@test.com";
        String displayName = "Dup User";

        doThrow(new DuplicateEmailException("duplicate")).when(createProfileUseCase).execute(any(Email.class), any(DisplayName.class));

        CreateProfileRequest request = new CreateProfileRequest();
        request.setEmail(email);
        request.setDisplayName(displayName);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void should_get_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        
        // Use Case 回傳的是 Domain Model (Profile)，不是 DTO
        Profile profile = new Profile(new ProfileId(id), new Email("user@example.com"), new DisplayName("User"));
        when(getProfileUseCase.execute(any())).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/profiles/{id}", id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(getProfileUseCase.execute(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "new@test.com";
        String displayName = "New Name";

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail(email);
        request.setDisplayName(displayName);

        // updateProfileUseCase.execute is void, so we just verify status and interaction
        
        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(updateProfileUseCase).execute(any(), any(Email.class), any(DisplayName.class));
    }

    @Test
    void should_return_404_on_update_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(id)).when(updateProfileUseCase).execute(any(), any(), any());

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("test@test.com");
        request.setDisplayName("Name");

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"displayName\":\"Name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_profile_with_204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNoContent());

        verify(deleteProfileUseCase).execute(any());
    }

    @Test
    void should_return_404_on_delete_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).execute(any());

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_reject_invalid_email_format_in_request() throws Exception {
        String invalidEmail = "invalid-email";
        
        CreateProfileRequest request = new CreateProfileRequest();
        request.setEmail(invalidEmail);
        request.setDisplayName("User");

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // 修正：GlobalExceptionHandler.handleValidationErrors 回傳的 JSON 結構為 {"errors": {...}}，非單一 "error" 欄位
                .andExpect(jsonPath("$.errors.email").value("invalid email format"));
    }
}
