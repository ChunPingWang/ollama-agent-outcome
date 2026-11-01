package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileController.class)
class WebExceptionHandlerTest {

    @MockitoBean private CreateProfileUseCase createProfileUseCase;
    @MockitoBean private GetProfileUseCase getProfileUseCase;
    @MockitoBean private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired private MockMvc mockMvc;

    @Test
    void should_handle_duplicate_email_exception_return_409() throws Exception {
        String json = """
            {"email": "dup@example.com", "displayName": "Dup User"}
            """;
        doThrow(new DuplicateEmailException("dup@example.com")).when(createProfileUseCase).execute(any(), any(), any());

        var response = mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(409);
    }

    @Test
    void should_handle_profile_not_found_exception_on_get_return_404() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new ProfileNotFoundException(id)).when(getProfileUseCase).execute(any());

        var response = mockMvc.perform(get("/api/profiles/{id}", id))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    void should_handle_profile_not_found_exception_on_delete_return_404() throws Exception {
        String id = UUID.randomUUID().toString();
        doThrow(new ProfileNotFoundException(id)).when(deleteProfileUseCase).execute(any());

        var response = mockMvc.perform(delete("/api/profiles/{id}", id))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(404);
    }
}
