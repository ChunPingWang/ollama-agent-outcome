package com.github.chunpingwang.ollamaagentoutcome.bdd;


import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.After;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 個人資料 CRUD 的 Cucumber 步驟定義。
 */
public class PersonalProfileSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> lastResponse;

    /**
     * 儲存最後一次成功建立或操作相關的 Profile ID (UUID)。
     */
    private String lastId;

    /**
     * 每個 Scenario 結束後清理資料庫，確保測試隔離性。
     */
    @After
    public void cleanupDatabase() {
        try {
            // 使用 TRUNCATE 快速清空表並重置序列（如果有）
            jdbcTemplate.execute("TRUNCATE TABLE profiles RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            // Ignore errors during cleanup to avoid masking test failures
        }
    }

    /**
     * 輔助方法：發送 HTTP 要求，並自動替換路徑中的 <id> 佔位符為實際 UUID。
     */
    private ResponseEntity<String> send(HttpMethod method, String path, String json) {
        if (path.contains("/profiles/")) {
            int prefixIndex = path.lastIndexOf("/profiles/") + "/profiles/".length();
            String idPart = path.substring(prefixIndex);
            
            boolean isValidUuid = false;
            try {
                UUID.fromString(idPart);
                isValidUuid = true;
            } catch (IllegalArgumentException e) {
                // Not a valid UUID
            }

            if (!isValidUuid) {
                 if (lastId != null && !lastId.isEmpty()) {
                    path = path.substring(0, prefixIndex) + lastId;
                } else {
                     throw new IllegalStateException("Path contains invalid ID placeholder but no profile has been created yet. Path: " + path);
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = (json != null) ? new HttpEntity<>(json, headers) : new HttpEntity<>(headers);
        
        return restTemplate.exchange(path, method, entity, String.class);
    }

    // --- POST /profiles Steps ---

    @當("我發送 POST 請求至 {string}，內容包含:")
    public void postCreate(String path, String content) {
        lastResponse = send(HttpMethod.POST, path, content);
        
        if (lastResponse.getStatusCode().value() == 201) {
            String locationHeader = lastResponse.getHeaders().getFirst("Location");
            if (locationHeader != null && !locationHeader.isEmpty()) {
                int lastSlashIndex = locationHeader.lastIndexOf('/');
                if (lastSlashIndex > -1) {
                    this.lastId = locationHeader.substring(lastSlashIndex + 1);
                }
            }
        } else {
            this.lastId = null; 
        }
    }

    // --- GET /profiles/{id} Steps ---

    @當("我發送 GET 請求至 {string}")
    public void getProfileById(String path) {
        lastResponse = send(HttpMethod.GET, path, null);
    }

    // --- PUT /profiles/{id} Steps ---

    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void updateProfileById(String path, String content) {
         lastResponse = send(HttpMethod.PUT, path, content);
         
         // If update fails, we might want to clear lastId or keep it? 
         // Keeping it allows subsequent steps in the same scenario to fail gracefully with correct ID.
    }

    // --- DELETE /profiles/{id} Steps ---

    @當("我發送 DELETE 請求至 {string}")
    public void deleteProfileById(String path) {
        lastResponse = send(HttpMethod.DELETE, path, null);
        
        // 刪除後，該 ID 無效
        this.lastId = null; 
    }

    // --- Assertions (Then/And) ---

    @那麼("回應的狀態碼應該是 {int} \\(Conflict\\)")
    public void statusIsConflict(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @那麼("回應的狀態碼應該是 {int} \\(Bad Request\\)")
    public void statusIsBadRequest(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @那麼("回應的狀態碼應該是 {int} \\(OK\\)")
    public void statusIsOk(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @那麼("回應的狀態碼應該是 {int} \\(Not Found\\)")
    public void statusIsNotFound(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @那麼("回應的狀態碼應該是 {int} \\(No Content\\)")
    public void statusIsNoContent(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @那麼("回應的狀態碼應該是 {int}")
    public void statusIsGeneric(int expectedStatusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatusCode);
    }

    @並且("錯誤訊息指出電子郵件已被使用")
    public void errorEmailExists() {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("error").asText())
                .isNotEmpty()
                .containsIgnoringCase("duplicate"); 
    }

    @並且("錯誤訊息指出電子郵件格式不正確")
    public void errorInvalidFormat() {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("error").asText())
                .isNotEmpty();
    }

    @並且("錯誤訊息指出找不到該資料")
    public void errorNotFound() {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("error").asText())
                .isNotEmpty()
                .containsIgnoringCase("not found");
    }

    @並且("回應主體中的 email 欄位等於 {string}")
    public void responseBodyEmailEquals(String expectedEmail) {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("email").asText()).isEqualTo(expectedEmail);
    }

    @並且("再次查詢該 ID 時，顯示名稱已變更為 \"New Name\"")
    public void verifyDisplayNameChanged() {
        // Use the lastId which should be valid from previous steps
        String path = "/profiles/" + lastId;
        ResponseEntity<String> getResponse = send(HttpMethod.GET, path, null);
        assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
        
        JsonNode node = objectMapper.readTree(getResponse.getBody());
        assertThat(node.path("displayName").asText()).isEqualTo("New Name");
    }

    @並且("隨後嘗試 GET 查詢該 ID 將返回 404")
    public void verifyDeletedReturns404() {
        String path = "/profiles/" + lastId;
        ResponseEntity<String> getResponse = send(HttpMethod.GET, path, null);
        assertThat(getResponse.getStatusCode().value()).isEqualTo(404);
    }

    @並且("回應標頭 \"Location\" 應該存在且指向剛建立的資源")
    public void locationHeaderExists() {
        assertThat(lastResponse.getHeaders().getFirst("Location"))
                .isNotNull();
    }

    // --- Helper Steps for Setup (Given) to handle UUID generation and storage ---

    @假設("系統中已存在電子郵件為 {string} 的個人資料")
    public void givenExistingProfile(String email) {
        String json = "{\"email\": \"" + email + "\", \"displayName\": \"Pre-existing User\"}";
        lastResponse = send(HttpMethod.POST, "/profiles", json);
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(201);
        
        String locationHeader = lastResponse.getHeaders().getFirst("Location");
        int lastSlashIndex = locationHeader.lastIndexOf('/');
        if (lastSlashIndex > -1) {
            this.lastId = locationHeader.substring(lastSlashIndex + 1);
        }
    }

    @假設("系統中存在 ID 為 {string}、電子郵件為 {string} 的個人資料")
    public void givenExistingProfileWithDetails(String idPlaceholder, String email) {
        String json = "{\"email\": \"" + email + "\", \"displayName\": \"Test User\"}";
        lastResponse = send(HttpMethod.POST, "/profiles", json);
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(201);
        
        String locationHeader = lastResponse.getHeaders().getFirst("Location");
        int lastSlashIndex = locationHeader.lastIndexOf('/');
        if (lastSlashIndex > -1) {
            this.lastId = locationHeader.substring(lastSlashIndex + 1);
        }
    }

    @假設("系統中存在 ID 為 {string} 的個人資料，其顯示名稱為 {string}")
    public void givenExistingProfileWithDisplayName(String idPlaceholder, String displayName) {
        String json = "{\"email\": \"oldname@example.com\", \"displayName\": \"" + displayName + "\"}";
        lastResponse = send(HttpMethod.POST, "/profiles", json);
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(201);
        
        String locationHeader = lastResponse.getHeaders().getFirst("Location");
        int lastSlashIndex = locationHeader.lastIndexOf('/');
        if (lastSlashIndex > -1) {
            this.lastId = locationHeader.substring(lastSlashIndex + 1);
        }
    }

    @假設("系統中存在 ID 為 {string} 的個人資料")
    public void givenExistingProfileForDelete(String idPlaceholder) {
        String json = "{\"email\": \"delete@example.com\", \"displayName\": \"To Be Deleted\"}";
        lastResponse = send(HttpMethod.POST, "/profiles", json);
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(201);
        
        String locationHeader = lastResponse.getHeaders().getFirst("Location");
        int lastSlashIndex = locationHeader.lastIndexOf('/');
        if (lastSlashIndex > -1) {
            this.lastId = locationHeader.substring(lastSlashIndex + 1);
        }
    }
}
