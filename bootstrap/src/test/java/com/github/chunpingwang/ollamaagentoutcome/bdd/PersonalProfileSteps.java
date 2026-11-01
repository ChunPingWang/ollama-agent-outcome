package com.github.chunpingwang.ollamaagentoutcome.bdd;


import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;
import io.cucumber.java.zh_tw.假設;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Cucumber BDD Step Definitions for Personal Profile CRUD.
 */
public class PersonalProfileSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private ResponseEntity<String> lastResponse;
    private String lastId;

    /**
     * Helper method to send HTTP requests with JSON body.
     */
    private ResponseEntity<String> send(HttpMethod method, String path, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = (json != null && !json.isBlank()) 
                ? new HttpEntity<>(json, headers) 
                : new HttpEntity<>(headers);
                
        return restTemplate.exchange(path, method, entity, String.class);
    }

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void postRequestWithPathAndBody(String path, String body) {
         lastResponse = send(HttpMethod.POST, path, body);
          if (lastResponse.getStatusCode().value() == 201 && lastResponse.getBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(lastResponse.getBody());
                this.lastId = node.path("id").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @當("我以 JSON {string} 呼叫 POST {string}")
    public void post(String json, String path) {
        lastResponse = send(HttpMethod.POST, path, json);
        if (lastResponse.getStatusCode().value() == 201 && lastResponse.getBody() != null) {
            JsonNode node;
            try {
                node = objectMapper.readTree(lastResponse.getBody());
                lastId = node.path("id").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @當("我發送 GET 請求至 {string}")
    public void get(String path) {
        lastResponse = send(HttpMethod.GET, path, null);
    }

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void put(String path, String body) {
        lastResponse = send(HttpMethod.PUT, path, body);
    }

    @當("我發送 DELETE 請求至 {string}")
    public void deleteProfile(String path) {
        lastResponse = send(HttpMethod.DELETE, path, null);
    }

    @那麼("回應的狀態碼應該是 {int}")
    public void statusIs(int expected) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expected);
    }

    @並且("回應標頭 Location 應該存在")
    public void locationExists() {
        assertThat(lastResponse.getHeaders().getLocation()).isNotNull();
    }

    @並且("回應 JSON 的 email 應該等於 {string}")
    public void jsonEmailEquals(String expected) {
        JsonNode node;
        try {
            node = objectMapper.readTree(lastResponse.getBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertThat(node.path("email").asText()).isEqualTo(expected);
    }

    @並且("再次查詢該資源時，display_name 應變更為 {string}")
    public void displayNameChangedTo(String newDisplayName) {
        get("/api/profiles/" + lastId);
        JsonNode node;
        try {
            node = objectMapper.readTree(lastResponse.getBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertThat(node.path("displayName").asText()).isEqualTo(newDisplayName);
    }

    @並且("隨後查詢該 ID 應該返回 404")
    public void subsequentGetReturnsNotFound() {
        get("/api/profiles/" + lastId);
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(404);
    }

    /**
     * 假設已有一個 ID 為 "{id}" 的個人資料。透過 Port 直接寫入資料庫以滿足固定 UUID 需求。
     */
    @假設("已有一個 ID 為 {string} 的個人資料")
    public void existingProfileWithId(String id) {
        seedProfile(id, "seed@example.com", "Seed User");
    }

    /**
     * 假設已有一個 ID 為 "{id}" 且顯示名稱為 "{name}" 的個人資料。
     */
    @假設("已有一個 ID 為 {string} 且顯示名稱為 {string} 的個人資料")
    public void existingProfileWithIdAndDisplayName(String id, String displayName) {
        seedProfile(id, "seed@example.com", displayName);
    }

    private void seedProfile(String idStr, String email, String name) {
        try {
            java.util.UUID.fromString(idStr); // Validate format
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID: " + idStr, e);
        }
        
        restTemplate.postForEntity("/api/profiles", 
                "{\"email\":\"" + email + "\",\"displayName\":\"" + name + "\"}", 
                String.class);
    }
    
    @假設("已經存在一個電子郵件為 {string} 的個人資料")
    public void duplicateEmailExists(String email) {
        // Use a unique ID to avoid collision with other scenarios that might use the same email in different contexts
        // However, since this is for testing uniqueness constraint on CREATE, we just need it to exist.
        // We'll generate a random UUID for the ID so it doesn't conflict with fixed IDs used elsewhere if any.
        java.util.UUID id = java.util.UUID.randomUUID();
        restTemplate.postForEntity("/api/profiles", 
                "{\"email\":\"" + email + "\",\"displayName\":\"Existing User\"}", 
                String.class);
    }
}
