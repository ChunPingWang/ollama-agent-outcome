package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.GetProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.usecase.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 測試 ProfileController 的 HTTP 端點、DTO 轉換與例外處理。
 * 使用 @WebMvcTest 僅載入 Web 層，並模擬 Application 層的 Use Case Ports。
 */
@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
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
    void should_create_profile_and_return_201_with_location() throws Exception {
        // Given
        UUID newId = UUID.randomUUID();
        String name = "張三";
        String email = "zhangsan@example.com";
        String requestBody = """
                {
                    "name": "%s",
                    "email": "%s"
                }
                """.formatted(name, email);

        Profile createdProfile = new Profile(new ProfileId(newId), name, new Email(email));
        when(createProfileUseCase.execute(any(CreateProfileCommand.class))).thenReturn(createdProfile);

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/api/profiles/" + newId));
    }

    @Test
    void should_return_400_when_request_body_is_invalid() throws Exception {
        // Given
        String requestBody = """
                {
                    "name": "",
                    "email": "invalid-email"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), "張三", new Email("zhangsan@example.com"));
        when(getProfileUseCase.execute(any(GetProfileCommand.class))).thenReturn(profile);

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("張三"))
                .andExpect(jsonPath("$.email").value("zhangsan@example.com"));
    }

    @Test
    void should_return_404_when_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(getProfileUseCase.execute(any(GetProfileCommand.class)))
                .thenThrow(new ProfileNotFoundException("Profile not found: " + id));

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String name = "張三豐";
        String email = "zhangsan_new@example.com";
        String requestBody = """
                {
                    "name": "%s",
                    "email": "%s"
                }
                """.formatted(name, email);
        Profile updatedProfile = new Profile(new ProfileId(id), name, new Email(email));
        when(updateProfileUseCase.execute(any(UpdateProfileCommand.class))).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/api/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void should_return_409_when_email_duplicate_on_update() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String requestBody = """
                {
                    "name": "張三",
                    "email": "duplicate@example.com"
                }
                """;
        when(updateProfileUseCase.execute(any(UpdateProfileCommand.class)))
                .thenThrow(new DuplicateEmailException("Email already exists: duplicate@example.com"));

        // When & Then
        mockMvc.perform(put("/api/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        // DeleteProfileUseCase.execute 回傳 void，因此使用 doNothing().when()
        doNothing().when(deleteProfileUseCase).execute(any(DeleteProfileCommand.class));

        // When & Then
        mockMvc.perform(delete("/api/profiles/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_404_when_delete_profile_not_found() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(deleteProfileUseCase.execute(any(DeleteProfileCommand.class)))
                .thenThrow(new ProfileNotFoundException("Profile not found: " + id));

        // When & Then
        mockMvc.perform(delete("/api/profiles/{id}", id))
                .andExpect(status().isNotFound());
    }
}
