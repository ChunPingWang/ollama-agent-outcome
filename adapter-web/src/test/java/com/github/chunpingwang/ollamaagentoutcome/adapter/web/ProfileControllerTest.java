package com.github.chunpingwang.ollamaagentoutcome.adapter.web;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_returns_201_and_location_header() throws Exception {
        String email = "user@example.com";
        String displayName = "Test User";
        doNothing().when(createProfileUseCase).execute(any());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"%s","displayName":"%s"}
                    """.formatted(email, displayName)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    }

    @Test
    void should_return_409_on_duplicate_email() throws Exception {
        doThrow(new DuplicateEmailException("duplicate"))
            .when(createProfileUseCase).execute(any());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dup@test.com\",\"displayName\":\"Dup\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    void should_get_profile_returns_200_with_json_body() throws Exception {
        String id = UUID.randomUUID().toString();
        GetProfileUseCase.GetProfileResponse response = new GetProfileUseCase.GetProfileResponse(id, "get@example.com", "Get User");
        when(getProfileUseCase.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value("get@example.com"))
            .andExpect(jsonPath("$.displayName").value("Get User"));
    }

    @Test
    void should_return_404_when_get_not_found() throws Exception {
        String id = UUID.randomUUID().toString();
        when(getProfileUseCase.findById(id)).thenThrow(new ProfileNotFoundException("not found"));

        mockMvc.perform(get("/api/profiles/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_returns_200() throws Exception {
        String id = UUID.randomUUID().toString();
        doNothing().when(updateProfileUseCase).execute(any(), eq(id));

        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"updated@test.com\",\"displayName\":\"Updated\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void should_return_404_on_put_not_found() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new ProfileNotFoundException("not found"))
            .when(updateProfileUseCase).execute(any(), eq(id));

        mockMvc.perform(put("/api/profiles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"u@test.com\",\"displayName\":\"U\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_profile_returns_204() throws Exception {
        String id = UUID.randomUUID().toString();
        doNothing().when(deleteProfileUseCase).execute(id);

        mockMvc.perform(delete("/api/profiles/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    void should_return_404_on_delete_not_found() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new ProfileNotFoundException("not found"))
            .when(deleteProfileUseCase).execute(id);

        mockMvc.perform(delete("/api/profiles/{id}", id))
            .andExpect(status().isNotFound());
    }
}
