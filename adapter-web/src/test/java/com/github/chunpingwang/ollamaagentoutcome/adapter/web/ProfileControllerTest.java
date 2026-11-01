package com.github.chunpingwang.ollamaagentoutcome.adapter.web;





import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean CreateProfileUseCase createProfileUseCase;
    @MockitoBean GetProfileUseCase getProfileUseCase;
    @MockitoBean UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean DeleteProfileUseCase deleteProfileUseCase;

    @Test
    void should_create_profile_and_return_201_with_location() throws Exception {
        String json = """
            {"email": "user@example.com", "displayName": "Test User"}
            """;

        doNothing().when(createProfileUseCase).execute(any());

        var response = mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(response.getResponse().getHeader("Location"))
                .matches("^/api/profiles/[a-f0-9\\-]+$");
    }

    @Test
    void should_reject_invalid_email_format_on_create() throws Exception {
        String json = """
            {"email": "invalid-email", "displayName": "Bad Email"}
            """;

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_handle_duplicate_email_conflict_on_create() throws Exception {
        String json = """
            {"email": "duplicate@test.com", "displayName": "Conflict User"}
            """;

        doThrow(new DuplicateEmailException("Duplicate email address: duplicate@test.com"))
                .when(createProfileUseCase).execute(any());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void should_get_profile_and_return_200_with_json_body() throws Exception {
        // 修正：模擬存在該 profile，而非 empty。若為 empty 則 Controller 會拋出 ProfileNotFoundException (404)。
        when(getProfileUseCase.execute(any()))
                .thenReturn(Optional.of(com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
                    new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId("550e8400-e29b-41d4-a716-446655440000"),
                    new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"),
                    new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Test User")
                )));

        var response = mockMvc.perform(get("/api/profiles/550e8400-e29b-41d4-a716-446655440000")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        
        String content = response.getResponse().getContentAsString();
        assertThat(content).contains("\"id\":\"550e8400-e29b-41d4-a716-446655440000\"");
        assertThat(content).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void should_return_404_when_getting_non_existent_profile() throws Exception {
        when(getProfileUseCase.execute(any()))
                .thenThrow(new ProfileNotFoundException("missing-id"));

        mockMvc.perform(get("/api/profiles/missing-id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_profile_and_return_200() throws Exception {
        String json = """
            {"email": "updated@test.com", "displayName": "New Name"}
            """;

        doNothing().when(updateProfileUseCase).execute(any(), any());

        mockMvc.perform(put("/api/profiles/update-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void should_delete_profile_and_return_204_no_content() throws Exception {
        doNothing().when(deleteProfileUseCase).execute(any());

        var response = mockMvc.perform(delete("/api/profiles/delete-id"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(response.getResponse().getContentLength()).isEqualTo(0);
    }

    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        doThrow(new ProfileNotFoundException("non-existent-delete-id"))
                .when(deleteProfileUseCase).execute(any());

        mockMvc.perform(delete("/api/profiles/non-existent-delete-id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
