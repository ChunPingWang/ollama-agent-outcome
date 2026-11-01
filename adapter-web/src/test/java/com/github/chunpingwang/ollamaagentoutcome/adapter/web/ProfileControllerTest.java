package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

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

    @Test
    void should_create_profile_with_201() throws Exception {
        String email = "user@example.com";
        String displayName = "John Doe";
        
        doNothing().when(createProfileUseCase).execute(email, displayName);

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"displayName\":\"" + displayName + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void should_reject_invalid_email_format_with_400() throws Exception {
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalid-email\",\"displayName\":\"Bad Format\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists()); 
    }

    @Test
    void should_get_existing_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        Profile profile = Profile.create(
            new ProfileId(id),
            new Email("user@example.com"),
            new DisplayName("User Name")
        );
        
        when(getProfileUseCase.execute(id)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/profiles/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.displayName").value("User Name"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(getProfileUseCase.execute(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_existing_profile_with_200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(updateProfileUseCase).execute(any(UUID.class), anyString(), anyString());

        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new@example.com\",\"displayName\":\"New Name\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void should_delete_existing_profile_with_204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteProfileUseCase).execute(id);

        mockMvc.perform(delete("/api/profiles/{id}", id))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void should_return_409_when_duplicate_email() throws Exception {
        String email = "dup@example.com";
        
        doThrow(new DuplicateEmailException("email already exists"))
            .when(createProfileUseCase).execute(eq(email), anyString());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"displayName\":\"Dup User\"}"))
                .andExpect(status().isConflict());
    }
}
