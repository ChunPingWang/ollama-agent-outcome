package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
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

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    
    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        
        when(createProfileUseCase.execute(any(Email.class), any(DisplayName.class)))
                .thenReturn(new ProfileId(id));

        String body = """
            {"email": "test@example.com", "displayName": "Tester"}
            """;

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.displayName").value("Tester"));
                
        verify(createProfileUseCase).execute(email, displayName);
    }

    @Test
    void should_reject_invalid_email_format_on_create() throws Exception {
        String body = """
            {"email": "invalid-email", "displayName": "Bad"}
            """;

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_profile_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        Email email = new Email("found@test.com");
        DisplayName displayName = new DisplayName("Found User");
        
        when(getProfileUseCase.execute(any(ProfileId.class)))
                .thenReturn(Optional.of(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                        new ProfileId(id), email, displayName)));

        mockMvc.perform(get("/api/profiles/{id}", id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("found@test.com"))
                .andExpect(jsonPath("$.displayName").value("Found User"));
    }

    @Test
    void should_return_404_when_getting_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        
        when(getProfileUseCase.execute(any(ProfileId.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        String body = """
            {"email": "updated@test.com", "displayName": "Updated Name"}
            """;

        doNothing().when(updateProfileUseCase).execute(any(ProfileId.class), any(Email.class), any(DisplayName.class));

        // 修正：PUT 成功應回傳 200 OK，與 RESTful 慣例及 Spec 定義一致
        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
                
        verify(updateProfileUseCase).execute(new ProfileId(id), new Email("updated@test.com"), new DisplayName("Updated Name"));
    }

    @Test
    void should_return_404_when_updating_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        String body = """
            {"email": "test@test.com", "displayName": "New"}
            """;

        doThrow(new ProfileNotFoundException(id)).when(updateProfileUseCase).execute(any(ProfileId.class), any(Email.class), any(DisplayName.class));

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_409_when_duplicate_email_on_update() throws Exception {
        UUID id = UUID.randomUUID();
        String body = """
            {"email": "duplicate@test.com", "displayName": "Dup"}
            """;

        doThrow(new DuplicateEmailException("Duplicate email")).when(updateProfileUseCase).execute(any(ProfileId.class), any(Email.class), any(DisplayName.class));

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNoContent());
                
        verify(deleteProfileUseCase).execute(new ProfileId(id));
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }
}
