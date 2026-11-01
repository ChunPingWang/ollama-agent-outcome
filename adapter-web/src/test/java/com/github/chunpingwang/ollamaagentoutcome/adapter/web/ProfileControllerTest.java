package com.github.chunpingwang.ollamaagentoutcome.adapter.web;





import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        String json = """
            {"email": "new@example.com", "displayName": "New User"}
            """;

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        
        verify(createProfileUseCase).execute(any(Email.class), any(DisplayName.class));
    }

    @Test
    void should_reject_duplicate_email_with_409() throws Exception {
        String json = """
            {"email": "dup@example.com", "displayName": "Dup User"}
            """;

        doThrow(new DuplicateEmailException()).when(createProfileUseCase).execute(any(), any());

        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void should_get_profile_by_id() throws Exception {
        UUID id = UUID.randomUUID();
        String profileJson = """
            {"id": "%s", "email": "user@test.com", "displayName": "User Name"}
            """.formatted(id);

        var profileId = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id);
        var email = new Email("user@test.com");
        var displayName = new DisplayName("User Name");
        var profile = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(profileId, email, displayName);

        org.mockito.Mockito.when(getProfileUseCase.findById(org.mockito.ArgumentMatchers.any(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId.class)))
            .thenReturn(java.util.Optional.of(profile));

        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.displayName").value("User Name"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        UUID id = UUID.randomUUID();

        org.mockito.Mockito.when(getProfileUseCase.findById(org.mockito.ArgumentMatchers.any(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId.class)))
            .thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/profiles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists()); 
    }

    @Test
    void should_update_display_name_and_return_200() throws Exception {
        UUID id = UUID.randomUUID();
        String json = """
            {"displayName": "Updated Name"}
            """;

        var profileId = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id);
        var email = new Email("old@example.com");
        var oldName = new DisplayName("Old Name");
        var profile = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(profileId, email, oldName);

        org.mockito.Mockito.when(getProfileUseCase.findById(org.mockito.ArgumentMatchers.any(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId.class)))
            .thenReturn(java.util.Optional.of(profile));

        mockMvc.perform(put("/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
                
        verify(updateProfileUseCase).execute(any(ProfileId.class), any(DisplayName.class));
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        UUID id = UUID.randomUUID();

        // No need to mock anything for success case of void method
        
        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNoContent());
        
        verify(deleteProfileUseCase).execute(any(ProfileId.class));
    }
    
    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();

        // Must stub the delete use case to throw exception when ID is not found
        doThrow(new ProfileNotFoundException(new ProfileId(id))).when(deleteProfileUseCase).execute(any(ProfileId.class));

        mockMvc.perform(delete("/profiles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
