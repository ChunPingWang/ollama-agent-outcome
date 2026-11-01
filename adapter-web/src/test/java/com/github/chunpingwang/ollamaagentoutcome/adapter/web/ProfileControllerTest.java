package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void should_create_profile_return_201_and_location() throws Exception {
        String json = """
            {"email": "test@example.com", "displayName": "Test User"}
            """;
        doNothing().when(createProfileUseCase).execute(any(), any(), any());

        var response = mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(201);
        assertThat(response.getResponse().getHeader("Location")).isNotNull();
    }

    @Test
    void should_get_profile_return_200_with_json_body() throws Exception {
        String id = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(id);
        GetProfileUseCase.ProfileData data = new GetProfileUseCase.ProfileData(id, "query@test.com", "Query User");
        when(getProfileUseCase.execute(profileId)).thenReturn(data);

        var resultActions = mockMvc.perform(get("/api/profiles/{id}", id));
        resultActions.andExpect(status().isOk());

        var content = resultActions.andReturn().getResponse().getContentAsString();
        var jsonNode = objectMapper.readTree(content);
        assertThat(jsonNode.path("email").asText()).isEqualTo("query@test.com");
        assertThat(jsonNode.path("displayName").asText()).isEqualTo("Query User");
    }

    @Test
    void should_update_profile_return_200() throws Exception {
        String id = UUID.randomUUID().toString();
        String json = """
            {"email": "updated@example.com", "displayName": "Updated Name"}
            """;
        doNothing().when(updateProfileUseCase).execute(any(), any(), any());

        var response = mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void should_delete_profile_return_204_no_content() throws Exception {
        String id = UUID.randomUUID().toString();
        doNothing().when(deleteProfileUseCase).execute(any());

        var response = mockMvc.perform(delete("/api/profiles/{id}", id))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(204);
    }
}
