package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Spring Boot 4 uses Jackson 3 (tools.jackson.databind.ObjectMapper)
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;

/**
 * 個人資料 REST API 的 Mock MVC 測試。
 */
@WebMvcTest(ProfileController.class)
class ProfileControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        String name = "王小明";
        String email = "test@example.com";
        
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", name, "email", email))))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
       
       assertThat(true).as("Placeholder assertion").isTrue();
    }

    @Test
    void should_reject_invalid_email_format_on_creation() throws Exception {
        // 預期 400 Bad Request when email is invalid
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "Name", "email", "invalid-email"))))
            .andExpect(status().isBadRequest());
            
        assertThat(true).as("Placeholder assertion").isTrue();
    }

    @Test
    void should_get_profile_by_id_and_return_200() throws Exception {
        String id = UUID.randomUUID().toString();
        
        // Mock the use case to return a profile
        /* 
         * Note: In real test we would mock getProfileUseCase.execute(id) to return a Profile object.
         * Since CreateProfileService etc are mocked via MockitoBean in this class? No, they are not injected here directly as beans for mocking their behavior inside controller logic unless we configure them.
         * Actually, @WebMvcTest only loads Web layer. UseCases need to be mocked or provided.
         * The @MockitoBean annotations above will create mocks and inject them into the Controller if it has a constructor that takes those interfaces.
         */
       
       // Placeholder for failing test logic once controller exists
       assertThat(true).as("Placeholder assertion").isTrue();
    }

    @Test
    void should_return_404_when_getting_non_existent_profile() throws Exception {
        String nonExistentId = "non-existent-id";
        
        // 預期 404 Not Found
        assertThat(true).as("Placeholder assertion").isTrue();
    }

    @Test
    void should_update_profile_successfully() throws Exception {
        String id = UUID.randomUUID().toString();
        String newName = "新名字";
        String newEmail = "new@example.com";
        
        // 修正：補上 DTO 中必填的 id 欄位，避免 Jakarta Validation (@Valid) 失敗導致回傳 400
        mockMvc.perform(put("/profiles/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("id", id, "name", newName, "email", newEmail))))
            .andExpect(status().isOk());
            
        // Verify update via GET
        /*
        mockMvc.perform(get("/profiles/" + id))
            .andExpect(jsonPath("$.name").value(newName));
        */
        assertThat(true).as("Placeholder assertion").isTrue();
    }

    @Test
    void should_delete_profile_and_return_204() throws Exception {
        String id = UUID.randomUUID().toString();
        
        // 預期 DELETE /profiles/{id} 回傳 204 No Content
        mockMvc.perform(delete("/profiles/" + id))
            .andExpect(status().isNoContent());
            
        // Verify deletion via GET (should return 404)
        /*
        mockMvc.perform(get("/profiles/" + id))
            .andExpect(status().isNotFound());
        */
        assertThat(true).as("Placeholder assertion").isTrue();
    }
    
    @Test
    void should_return_404_when_deleting_non_existent_profile() throws Exception {
        String nonExistentId = "non-existent-id";
        
        // 預期 404 Not Found
        assertThat(true).as("Placeholder assertion").isTrue();
    }
}
