package com.github.chunpingwang.ollamaagentoutcome.adapter.web.profile;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResponse;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.InvalidDisplayNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.web.dto.CreateProfileRequest;

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
        // given
        var request = new CreateProfileRequest("user@example.com", "Test User", "Hello");
        
        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    }

    @Test
    void should_reject_invalid_email_format_with_400() throws Exception {
        // given
        var request = new CreateProfileRequest("invalid-email", "User", null);
        
        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_reject_too_long_display_name_with_400() throws Exception {
        // given
        String longName = "A".repeat(51);
        var request = new CreateProfileRequest("valid@example.com", longName, null);
        
        // when & then
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_profile_when_exists() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        ProfileResponse response = new ProfileResponse(id.toString(), "user@example.com", "Tester", "Bio");
        
        org.mockito.Mockito.when(getProfileUseCase.execute(any()))
            .thenReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.displayName").value("Tester"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new ProfileNotFoundException(id)).when(getProfileUseCase).execute(any());
        
        // when & then
        mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        var request = new CreateProfileRequest("new@test.com", "New Name", "Updated Bio");
        
        // when & then
        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        
        // when & then
        mockMvc.perform(delete("/api/profiles/{id}", id))
            .andExpect(status().isNoContent());
    }
}
