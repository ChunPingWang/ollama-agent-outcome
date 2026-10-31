package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @MockitoBean
    private LoadProfileUseCase loadProfileUseCase;

    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        
        when(createProfileUseCase.execute(any()))
            .thenReturn(new ProfileId(id));

        String requestBody = """
            {
                "name": "Tester",
                "email": "test@example.com"
            }
            """;

        // when & then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
            
        verify(createProfileUseCase).execute(any());
    }

    @Test
    void should_reject_invalid_json_on_create() throws Exception {
        // given
        String invalidJson = "{ invalid json }";

        // when & then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_reject_missing_fields_on_create() throws Exception {
        // given
        String missingFields = """
            {
                "name": ""
            }
            """;

        // when & then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(missingFields))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_409_when_duplicate_email_during_creation() throws Exception {
        // given
        doThrow(new DuplicateEmailException()).when(createProfileUseCase).execute(any());

        String requestBody = """
            {
                "name": "Tester",
                "email": "duplicate@example.com"
            }
            """;

        // when & then
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("email already exists"));
    }

    @Test
    void should_load_profile_and_return_200_with_json_body() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        
        ProfileResult result = new ProfileResult(
            new Name("Finder"), 
            new Email("find@example.com")
        );
        when(loadProfileUseCase.execute(any())).thenReturn(result);

        // when & then
        mockMvc.perform(get("/profiles/" + id.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("Finder"))
            .andExpect(jsonPath("$.email").value("find@example.com"));
            
        verify(loadProfileUseCase).execute(new ProfileId(id));
    }

    @Test
    void should_return_404_when_profile_not_found_on_get() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        when(loadProfileUseCase.execute(any()))
            .thenThrow(new ProfileNotFoundException(nonExistentId));

        // when & then
        mockMvc.perform(get("/profiles/" + nonExistentId.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        String requestBody = """
            {
                "name": "Updated Name",
                "email": "updated@example.com"
            }
            """;

        // when & then
        mockMvc.perform(put("/profiles/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
            
        verify(updateProfileUseCase).execute(any());
    }

    @Test
    void should_reject_invalid_email_format_on_update() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        String invalidEmailRequest = """
            {
                "name": "Valid Name",
                "email": "not-an-email"
            }
            """;

        // when & then
        mockMvc.perform(put("/profiles/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmailRequest))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/profiles/" + id.toString()))
            .andExpect(status().isNoContent());
            
        verify(deleteProfileUseCase).execute(new ProfileId(id));
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ProfileNotFoundException(nonExistentId)).when(deleteProfileUseCase).execute(any());

        // when & then
        mockMvc.perform(delete("/profiles/" + nonExistentId.toString()))
            .andExpect(status().isNotFound());
    }
}
