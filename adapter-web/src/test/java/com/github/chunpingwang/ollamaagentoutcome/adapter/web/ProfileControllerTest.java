package com.github.chunpingwang.ollamaagentoutcome.adapter.web;




import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // Given
        UUID newId = UUID.randomUUID();
        String email = "new@example.com";
        String displayName = "New User";
        
        when(createProfileUseCase.execute(email, displayName)).thenReturn(new ProfileId(newId));
        
        var profile = Profile.create(
            newId, 
            email.toLowerCase(), 
            displayName
        );
        when(getProfileUseCase.findById(any(ProfileId.class))).thenReturn(profile);

        String requestBody = """
                {
                    "email": "%s",
                    "displayName": "%s"
                }
                """.formatted(email, displayName);

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(print());
                
        verify(createProfileUseCase).execute(email, displayName);
    }

    @Test
    void should_return_409_when_duplicate_email() throws Exception {
        // Given
        String email = "dup@example.com";
        String displayName = "Dup User";
        
        doThrow(new DuplicateEmailException("email already exists")).when(createProfileUseCase).execute(email, displayName);

        String requestBody = """
                {
                    "email": "%s",
                    "displayName": "%s"
                }
                """.formatted(email, displayName);

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    void should_return_400_for_invalid_json_structure() throws Exception {
        // Given: Missing required fields or invalid format
        
        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void should_get_profile_by_id_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        var emailStr = "get@example.com";
        var name = "Get User";
        
        var profile = Profile.create(
            id, 
            emailStr.toLowerCase(), 
            name
        );
        
        when(getProfileUseCase.findById(profileId)).thenReturn(profile);

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(emailStr.toLowerCase()))
                .andExpect(jsonPath("$.displayName").value(name));
                
        verify(getProfileUseCase).findById(profileId);
    }

    @Test
    void should_return_404_when_profile_not_found_for_get() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var profileId = new ProfileId(nonExistentId);
        
        doThrow(new ProfileNotFoundException("Profile not found with id: " + nonExistentId)).when(getProfileUseCase).findById(profileId);

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String email = "updated@example.com";
        String displayName = "Updated Name";
        
        doNothing().when(updateProfileUseCase).execute(any(ProfileId.class), any(String.class), any(String.class));

        String requestBody = """
                {
                    "email": "%s",
                    "displayName": "%s"
                }
                """.formatted(email, displayName);

        // When & Then
        mockMvc.perform(put("/api/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
                
        verify(updateProfileUseCase).execute(any(), org.mockito.ArgumentMatchers.eq(email), org.mockito.ArgumentMatchers.eq(displayName));
    }
    
    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        
        doNothing().when(deleteProfileUseCase).execute(profileId);

        // When & Then
        mockMvc.perform(delete("/api/profiles/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());
                
        verify(deleteProfileUseCase).execute(profileId);
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var profileId = new ProfileId(nonExistentId);
        
        doThrow(new ProfileNotFoundException("Profile not found with id: " + nonExistentId)).when(deleteProfileUseCase).execute(profileId);

        // When & Then
        mockMvc.perform(delete("/api/profiles/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
