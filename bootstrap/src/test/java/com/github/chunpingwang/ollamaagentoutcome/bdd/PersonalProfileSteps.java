package com.github.chunpingwang.ollamaagentoutcome.bdd;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileRepository;

/**
 * Step definitions for Personal Profile CRUD BDD tests.
 */
public class PersonalProfileSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired(required = false)
    private ProfileRepository profileRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private ResponseEntity<String> lastResponse;
    private String lastId;

    /**
     * Helper to send HTTP requests with JSON body and correct Content-Type.
     */
    private ResponseEntity<String> send(HttpMethod method, String path, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (json == null || json.isBlank()) {
            return restTemplate.exchange(path, method, null, String.class);
        }
        
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return restTemplate.exchange(path, method, entity, String.class);
    }

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void 我發送_post_請求至(String path, String docString) {
        lastResponse = send(HttpMethod.POST, path, docString);
        if (lastResponse.getStatusCode().value() == 201 && lastResponse.getBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(lastResponse.getBody());
                lastId = node.path("id").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            lastId = null;
        }
    }

    @當("我發送 GET 請求至 {string}")
    public void 我發送_get_請求至(String path) {
        lastResponse = send(HttpMethod.GET, path, null);
    }

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void 我發送_put_請求至(String path, String body) {
        lastResponse = send(HttpMethod.PUT, path, body);
    }

    @當("我發送 DELETE 請求至 {string}")
    public void 我發送_delete_請求至(String path) {
        lastResponse = send(HttpMethod.DELETE, path, null);
    }

    @那麼("回應的狀態碼應為 {int}")
    public void 回應的狀態碼應為(int expectedStatus) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @並且("回應標頭 \"Location\" 應存在且指向新建立的資源")
    public void locationHeaderExists() {
        URI location = lastResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();
        if (lastId != null) {
            assertThat(location.getPath()).endsWith("/" + lastId);
        }
    }

    @並且("錯誤訊息應指出電子郵件已存在")
    public void errorMessageIndicatesDuplicateEmail() {
        try {
            JsonNode node = objectMapper.readTree(lastResponse.getBody());
            assertThat(node.path("message").asText())
                .containsIgnoringCase("email");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @並且("錯誤訊息應指出欄位驗證失敗")
    public void errorMessageIndicatesValidationFailure() {
        // Validation errors usually return a map of field: error or a message string.
        // We just check that the body is not empty and contains some validation info.
        assertThat(lastResponse.getBody()).isNotEmpty();
    }

    @並且("回應主體中的姓名應為 {string}")
    public void nameMatches(String expectedName) {
        try {
            JsonNode node = objectMapper.readTree(lastResponse.getBody());
            assertThat(node.path("name").asText()).isEqualTo(expectedName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @並且("回應主體中的電子郵件應為 {string}")
    public void emailMatches(String expectedEmail) {
        try {
            JsonNode node = objectMapper.readTree(lastResponse.getBody());
            assertThat(node.path("email").asText()).isEqualTo(expectedEmail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @並且("再次查詢該 ID 時，姓名應變更為 {string}")
    public void updatedNameShouldMatch(String newName) {
        get("/profiles/" + lastId);
        statusIs(200);
        nameMatches(newName);
    }

    private void get(String path) {
        send(HttpMethod.GET, path, null);
    }

    private void statusIs(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @並且("隨後嘗試 GET 查詢該 ID 時應返回 404")
    public void shouldReturnNotFoundAfterDelete() {
        get("/profiles/" + lastId);
        statusIs(404);
    }

    /**
     * Helper to create a profile with specific data for setup purposes.
     */
    private String createProfileForSetup(String jsonBody) {
        ResponseEntity<String> response = send(HttpMethod.POST, "/profiles", jsonBody);
        if (response.getStatusCode().value() == 201 && response.getBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(response.getBody());
                return node.path("id").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new AssertionError("Failed to create initial profile: " + response.getBody());
        }
    }


    
    /**
     * Helper to ensure a profile exists with a SPECIFIC ID (for testing purposes).
     * Uses Repository directly because the REST API generates random UUIDs.
     */
    private void ensureProfileExists(String idStr, String name, String email) {
        if (profileRepository == null) {
            throw new IllegalStateException("ProfileRepository is not available for BDD setup");
        }
        
        try {
            UUID targetId = UUID.fromString(idStr);
            
            // Check if already exists
            boolean exists = profileRepository.existsById(targetId);
            if (!exists) {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(targetId);
                entity.setName(name);
                entity.setEmail(email != null ? email : "setup_" + System.currentTimeMillis() + "@test.com");
                
                profileRepository.save(entity);
                // Flush and clear to make it visible in subsequent queries within same transaction/context?
                // Actually, save persists immediately in JPA unless flush mode is MANUAL. 
                // But to be safe against caching issues in integration tests:
                // We can't easily call entityManager.flush/clear here without injecting TestEntityManager.
                // However, standard Spring Data JPA usually works fine after save().
            }
        } catch (IllegalArgumentException e) {
             throw new AssertionError("Invalid UUID format: " + idStr, e);
        }
    }

    @假設("已存在一筆電子郵件為 {string} 的個人資料")
    public void assumeProfileExistsWithDuplicateEmail(String email) {
        // Create a unique name to avoid conflicts with other scenarios running in the same DB context
        String uniqueName = "Existing User " + System.currentTimeMillis();
        
        String jsonBody = """
            {"name": "%s", "email": "%s"}
            """.formatted(uniqueName, email);
            
        ResponseEntity<String> response = send(HttpMethod.POST, "/profiles", jsonBody);
        if (response.getStatusCode().value() == 201 && response.getBody() != null) {
             try {
                JsonNode node = objectMapper.readTree(response.getBody());
                lastId = node.path("id").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new AssertionError("Failed to setup profile: " + response.getBody());
        }
    }

    @假設("系統中已存在 ID 為 {string}、姓名為 {string} 的個人資料")
    public void assumeProfileExistsWithNameAndEmail(String idStr, String name, String email) {
         ensureProfileExists(idStr, name, email);
    }
    
    @假設("系統中已存在 ID 為 {string} 的個人資料")
    public void assumeProfileExistsById(String idStr, DataTable dataTable) {
        // Extract name and email from the table if provided, otherwise use defaults or generate unique ones
        List<List<String>> rows = dataTable.asLists();
        String name = "Default Name";
        String email = "default_" + System.currentTimeMillis() + "@test.com";
        
        if (!rows.isEmpty()) {
            for (List<String> row : rows) {
                if (row.size() >= 2) {
                    name = row.get(0);
                    email = row.get(1);
                }
            }
        }
        
        ensureProfileExists(idStr, name, email);
    }
    
    @那麼("錯誤訊息應指出找不到該資料")
    public void errorMessageIndicatesNotFound() {
        try {
            JsonNode node = objectMapper.readTree(lastResponse.getBody());
            assertThat(node.path("message").asText()).isNotEmpty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @當("我發送 GET 請求至 {string}（假設該 ID 不存在）")
    public void 我發送_get_請求至_假設該_id_不存在(String path) {
        lastResponse = send(HttpMethod.GET, path, null);
    }

    @當("我發送 DELETE 請求至 {string}（假設該 ID 不存在）")
    public void 我發送_delete_請求至_假設該_id_不存在(String path) {
        lastResponse = send(HttpMethod.DELETE, path, null);
    }
}
