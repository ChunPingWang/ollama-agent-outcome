package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.mockito.ArgumentMatchers.anyString;
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

import java.util.Optional;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@WebMvcTest(ProfileRestController.class)
@AutoConfigureMockMvc
class ProfileRestControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean CreateProfileUseCase createPort;
    @MockitoBean GetProfileUseCase getPort;
    @MockitoBean UpdateProfileUseCase updatePort;
    @MockitoBean DeleteProfileUseCase deletePort;

    @Test
    void should_create_profile_returns_201_and_location_header() throws Exception {
        UUID newId = UUID.randomUUID();
        String email = "new@example.com";
        String displayName = "New User";
        var requestJson = """
            {"email":"%s","displayName":"%s"}""".formatted(email, displayName);

        when(createPort.create(anyString(), anyString())).thenReturn(new ProfileId(newId));

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(print());

        verify(createPort).create(email, displayName);
    }

    @Test
    void should_get_profile_returns_200_with_json_body() throws Exception {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String expectedEmail = "user@example.com";
        String expectedName = "Existing User";

        when(getPort.getById(new ProfileId(id)))
                .thenReturn(Optional.of(new Profile(
                        new ProfileId(id),
                        new Email(expectedEmail),
                        new DisplayName(expectedName)
                )));

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(expectedEmail))
                .andExpect(jsonPath("$.displayName").value(expectedName))
                .andDo(print());
    }

    @Test
    void should_update_profile_returns_200() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "updated@example.com";
        String displayName = "Updated Name";
        var requestJson = """
            {"email":"%s","displayName":"%s"}""".formatted(email, displayName);

        mockMvc.perform(put("/api/profiles/{id}", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print());

        verify(updatePort).update(id.toString(), email, displayName);
    }

    @Test
    void should_delete_profile_returns_204_no_content() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(deletePort).deleteById(new ProfileId(id));
    }
}
