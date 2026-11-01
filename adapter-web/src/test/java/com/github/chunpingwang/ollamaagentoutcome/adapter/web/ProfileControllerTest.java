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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

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
        UUID newUuid = UUID.randomUUID();
        when(createProfileUseCase.execute(any(Email.class), any(DisplayName.class)))
                .thenReturn(new ProfileId(newUuid));

        String body = """
            {"email": "user@example.com", "displayName": "New User"}
            """;

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        
        verify(createProfileUseCase).execute(any(Email.class), any(DisplayName.class));
    }

    @Test
    void should_reject_invalid_email_format_on_create() throws Exception {
        String body = """
            {"email": "invalid-email", "displayName": "Bad Email"}
            """;

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_handle_duplicate_email_exception_as_409() throws Exception {
        doThrow(new DuplicateEmailException("dup@test.com"))
                .when(createProfileUseCase).execute(any(Email.class), any(DisplayName.class));

        String body = """
            {"email": "dup@test.com", "displayName": "Dup User"}
            """;

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void should_get_profile_and_return_200_when_exists() throws Exception {
        UUID id = UUID.randomUUID();
        var expectedProfile = new Profile(
            new ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Existing User")
        );
        
        when(getProfileUseCase.execute(new ProfileId(id))).thenReturn(Optional.of(expectedProfile));

        mockMvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    
    @Test
    void should_return_404_when_getting_non_existent_profile() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(getProfileUseCase.execute(any(ProfileId.class)))
                .thenThrow(new ProfileNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/profiles/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        String body = """
            {"email": "new@example.com", "displayName": "Updated Name"}
            """;

        mockMvc.perform(put("/api/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
                
        verify(updateProfileUseCase).execute(any(ProfileId.class), any(Email.class), any(DisplayName.class));
    }

    @Test
    void should_return_404_on_update_for_non_existent_profile() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(nonExistentId))
                .when(updateProfileUseCase).execute(any(ProfileId.class), any(Email.class), any(DisplayName.class));

        String body = """
            {"email": "test@test.com", "displayName": "Name"}
            """;

        mockMvc.perform(put("/api/profiles/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/profiles/{id}", id))
                .andExpect(status().isNoContent());
                
        verify(deleteProfileUseCase).execute(any(ProfileId.class));
    }

    @Test
    void should_return_404_on_delete_for_non_existent_profile() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(nonExistentId))
                .when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/api/profiles/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
