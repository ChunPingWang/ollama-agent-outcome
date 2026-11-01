package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void should_create_profile_with_201() throws Exception {
        String json = "{\"email\": \"user@example.com\", \"displayName\": \"John Doe\"}";

        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void should_reject_invalid_email_with_400() throws Exception {
        String json = "{\"email\": \"invalid-email\", \"displayName\": \"John\"}";

        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_profile_with_200() throws Exception {
        when(getProfileUseCase.execute(any())).thenReturn(
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(java.util.UUID.randomUUID()),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Tester")
            )
        );

        mockMvc.perform(get("/profiles/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        // Use a valid UUID format to ensure the controller parses it correctly and calls the use case,
        // which then throws ProfileNotFoundException as mocked.
        java.util.UUID nonExistentUuid = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
        
        when(getProfileUseCase.execute(any()))
                .thenThrow(new ProfileNotFoundException(nonExistentUuid));

        mockMvc.perform(get("/profiles/" + nonExistentUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_with_200() throws Exception {
        String json = "{\"displayName\": \"New Name\"}";

        doNothing().when(updateProfileUseCase).execute(any());

        mockMvc.perform(put("/profiles/123e4567-e89b-12d3-a456-426614174000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void should_delete_profile_with_204() throws Exception {
        doNothing().when(deleteProfileUseCase).execute(any());

        mockMvc.perform(delete("/profiles/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNoContent());
    }
}
