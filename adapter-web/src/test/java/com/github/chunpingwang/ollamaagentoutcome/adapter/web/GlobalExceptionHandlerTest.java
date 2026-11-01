package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_map_duplicate_email_to_409_with_error_body() throws Exception {
        doThrow(new DuplicateEmailException("email already exists"))
            .when(createProfileUseCase).execute(any());

        var response = mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dup@test.com\",\"displayName\":\"Dup\"}"))
            .andExpect(status().isConflict())
            .andReturn();

        JsonNode body = objectMapper.readTree(response.getResponse().getContentAsString());
        assertThat(body.has("status")).isTrue();
        // 修正：訊息內容應包含 "already exists"，而非僅 "duplicate"
        assertThat(body.get("message").asText()).containsIgnoringCase("exists");
    }
    
    @Test
    void should_map_profile_not_found_to_404_with_error_body() throws Exception {
        String id = UUID.randomUUID().toString();
        when(getProfileUseCase.findById(id)).thenThrow(new ProfileNotFoundException("profile not found"));

        var response = mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isNotFound())
            .andReturn();

        JsonNode body = objectMapper.readTree(response.getResponse().getContentAsString());
        assertThat(body.has("status")).isTrue();
        assertThat(body.get("message").asText()).containsIgnoringCase("not found");
    }
}
