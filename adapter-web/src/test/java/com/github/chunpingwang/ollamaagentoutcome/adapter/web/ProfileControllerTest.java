package com.github.chunpingwang.ollamaagentoutcome.adapter.web;




import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProfileController.class)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    
    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;
    
    @Autowired private ObjectMapper objectMapper;

    @Test
    void should_create_profile_with_201_and_location_header() throws Exception {
        String json = """
            {"email": "user@example.com", "displayName": "Display Name"}
            """;
        
        UUID generatedId = UUID.randomUUID();
        
        // 模擬 Use Case 成功執行（不拋例外）
        doNothing().when(createProfileUseCase).execute(any(UUID.class), anyString(), any());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
            
        verify(createProfileUseCase, times(1)).execute(any(UUID.class), eq("user@example.com"), eq("Display Name"));
    }

    @Test
    void should_return_409_when_duplicate_email() throws Exception {
        String json = """
            {"email": "dup@example.com", "displayName": "Dup User"}
            """;
            
        doThrow(new com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException("duplicate email"))
            .when(createProfileUseCase).execute(any(UUID.class), anyString(), any());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict());
    }

    @Test
    void should_return_400_for_invalid_json_structure() throws Exception {
        // 缺少必填欄位或格式錯誤，Jakarta Validation 會攔截並拋出 MethodArgumentNotValidException
        String invalidJson = "{\"email\": \"invalid-email\"}"; 
        
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        
        Profile profile = new Profile(
            new ProfileId(id), 
            new Email("user@example.com"), 
            new DisplayName("Display Name")
        );
        
        when(getProfileUseCase.findById(any(ProfileId.class))).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()));
            
        verify(getProfileUseCase, times(1)).findById(new ProfileId(id));
        verifyNoInteractions(createProfileUseCase, updateProfileUseCase, deleteProfileUseCase);
    }

    @Test
    void should_return_404_when_get_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        
        doThrow(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id))
            .when(getProfileUseCase).findById(any(ProfileId.class));

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        String json = """
            {"email": "new@example.com", "displayName": "New Name"}
            """;
            
        // 模擬 Use Case 成功執行（不拋例外）
        doNothing().when(updateProfileUseCase).execute(any(UUID.class), anyString(), any());

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk());
            
        verify(updateProfileUseCase, times(1)).execute(eq(id), eq("new@example.com"), eq("New Name"));
        verifyNoInteractions(createProfileUseCase, getProfileUseCase, deleteProfileUseCase);
    }

    @Test
    void should_return_404_when_update_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        String json = """
            {"email": "test@test.com", "displayName": "Name"}
            """;
            
        doThrow(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id))
            .when(updateProfileUseCase).execute(any(UUID.class), anyString(), any());

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_profile_with_204() throws Exception {
        UUID id = UUID.randomUUID();
        
        // 模擬 Use Case 成功執行（不拋例外）
        doNothing().when(deleteProfileUseCase).execute(any(UUID.class));

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
            .andExpect(status().isNoContent());
            
        verify(deleteProfileUseCase, times(1)).execute(eq(id));
        verifyNoInteractions(createProfileUseCase, getProfileUseCase, updateProfileUseCase);
    }

    @Test
    void should_return_404_when_delete_non_existent_profile() throws Exception {
        UUID id = UUID.randomUUID();
        
        doThrow(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id))
            .when(deleteProfileUseCase).execute(any(UUID.class));

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
            .andExpect(status().isNotFound());
    }
}
