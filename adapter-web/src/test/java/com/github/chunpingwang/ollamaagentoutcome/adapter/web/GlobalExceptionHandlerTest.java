package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@WebMvcTest(ProfileRestController.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean CreateProfileUseCase createPort;
    @MockitoBean GetProfileUseCase getPort;
    @MockitoBean UpdateProfileUseCase updatePort;
    @MockitoBean DeleteProfileUseCase deletePort;

    @Test
    void should_handle_duplicate_email_returns_409() throws Exception {
        doThrow(new DuplicateEmailException("email already exists")).when(createPort).create(anyString(), anyString());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"dup@test.com","displayName":"Dup"}"""))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    void should_handle_profile_not_found_get_returns_404() throws Exception {
        UUID id = UUID.randomUUID();
        
        org.mockito.Mockito.when(getPort.getById(org.mockito.ArgumentMatchers.any(com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId.class)))
            .thenThrow(new ProfileNotFoundException(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id)));

        mockMvc.perform(get("/api/profiles/{id}", id.toString()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void should_handle_invalid_format_returns_400() throws Exception {
        doThrow(new InvalidEmailFormatException("invalid format")).when(createPort).create(anyString(), anyString());

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"bad-email","displayName":"Bad"}"""))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
