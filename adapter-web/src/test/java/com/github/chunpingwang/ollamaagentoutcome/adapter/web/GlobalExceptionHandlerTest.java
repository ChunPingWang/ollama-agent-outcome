package com.github.chunpingwang.ollamaagentoutcome.adapter.web;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileController.class)
class GlobalExceptionHandlerTest {

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;

    @MockitoBean
    private GetProfileUseCase getProfileUseCase;

    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockitoBean
    private DeleteProfileUseCase deleteProfileUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_handle_duplicate_email_exception_with_409() throws Exception {
        var request = new com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest("dup@test.com", "User");
        
        doThrow(new DuplicateEmailException("email already exists"))
            .when(createProfileUseCase).execute(any(com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest.class));

        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("email already exists"));
    }

    @Test
    void should_handle_not_found_exception_with_404() throws Exception {
        String notFoundId = "non-existent-id";
        
        doThrow(new ProfileNotFoundException(notFoundId))
            .when(getProfileUseCase).findById(anyString());

        mockMvc.perform(get("/api/profiles/{id}", notFoundId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(notFoundId)));
    }
}
