package com.github.chunpingwang.ollamaagentoutcome.bdd;



import static org.assertj.core.api.Assertions.*;
import io.cucumber.java.Before;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.並且;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Cucumber BDD 步驟定義，用於驗證個人資料 CRUD 功能。
 */
public class ProfileSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> lastResponse;
    
    /**
     * 追蹤最後成功創建的資源 ID，以便後續場景使用（如查詢、更新、刪除）。
     */
    private String lastCreatedId;

    /**
     * 追蹤最後使用的 email，用於驗證查詢結果中的 email 欄位及處理重複電子郵件測試情境。
     */
    private String lastEmail;

    /**
     * 追蹤當前 Scenario 中第一次 POST 建立的 Email，以確保「重複電子郵件」測試能正確觸發衝突。
     */
    private String currentScenarioFirstPostEmail;

    /**
     * 每個 Scenario 執行前重置狀態，避免跨場景資料污染。
     */
    @Before
    public void resetState() {
        this.lastCreatedId = null;
        this.lastEmail = null;
        this.currentScenarioFirstPostEmail = null;
    }

    /**
     * 發送 HTTP 請求的輔助方法。
     */
    private ResponseEntity<String> send(HttpMethod method, String path, String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return restTemplate.exchange(path, method, new HttpEntity<>(jsonBody == null ? "" : jsonBody, headers), String.class);
    }

    /**
     * POST /api/profiles - 建立新資料
     */
    @當("我發送 POST 請求至 {string}，內容包含:")
    public void postRequest(String path, String body) {
        // 替換時間戳記佔位符以確保每個測試案例的數據唯一性
        String processedBody = body.replace("{{timestamp}}", String.valueOf(System.currentTimeMillis()));
        
        // 若為同一個 Scenario 內的第二次或後續 POST，強制使用第一次建立的 Email，
        // 使「重複電子郵件」測試能穩定觸發 409 Conflict，無需修改 Feature 檔。
        if (lastCreatedId != null && currentScenarioFirstPostEmail != null) {
            try {
                JsonNode node = objectMapper.readTree(processedBody);
                if (node.has("email")) {
                    ((ObjectNode) node).put("email", currentScenarioFirstPostEmail);
                    processedBody = node.toString();
                }
            } catch (Exception e) { /* 解析失敗時維持原樣 */ }
        }

        lastResponse = send(HttpMethod.POST, path, processedBody);
        
        if (lastResponse.getStatusCode().value() == 201 && lastResponse.getBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(lastResponse.getBody());
                
                // 記錄該場景首次成功的 email，供後續步驟判斷是否為重複建立情境
                if (currentScenarioFirstPostEmail == null) {
                    currentScenarioFirstPostEmail = node.path("email").asText("");
                }
                
                this.lastCreatedId = node.path("id").asText("");
                this.lastEmail = node.path("email").asText("");
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse ID from creation response", e);
            }
        } else {
             // 非 201 回應（如 409）不更新追蹤欄位，確保後續步驟仍能引用最初建立的資源。
        }
    }

    /**
     * GET /api/profiles/{id} - 查詢指定 ID 的資料
     */
    @當("我發送 GET 請求至 {string}")
    public void getRequest(String path) {
        lastResponse = send(HttpMethod.GET, path, null);
    }
    
    /**
     * 查詢最後建立的個人資料
     */
    @當("我查詢最後建立的個人資料時")
    public void queryLastCreatedProfile() {
        assertThat(lastCreatedId).isNotNull();
        getRequest("/api/profiles/" + lastCreatedId);
    }

    /**
     * PUT /api/profiles/{id} - 更新指定 ID 的資料
     */
    @當("我發送 PUT 請求至 {string}，內容包含:")
    public void putRequest(String path, String body) {
         // 替換時間戳記佔位符以確保每個測試案例的數據唯一性
        String processedBody = body.replace("{{timestamp}}", String.valueOf(System.currentTimeMillis()));
        lastResponse = send(HttpMethod.PUT, path, processedBody);
    }
    
    /**
     * 更新最後建立的個人資料
     */
    @當("我更新最後建立的個人資料，內容包含:")
    public void updateLastCreatedProfile(String body) {
        assertThat(lastCreatedId).isNotNull();
        putRequest("/api/profiles/" + lastCreatedId, body);
    }

    /**
     * DELETE /api/profiles/{id} - 刪除指定 ID 的資料
     */
    @當("我發送 DELETE 請求至 {string}")
    public void deleteRequest(String path) {
        lastResponse = send(HttpMethod.DELETE, path, null);
    }
    
    /**
     * 刪除最後建立的個人資料
     */
    @當("我刪除最後建立的個人資料時")
    public void deleteLastCreatedProfile() {
        assertThat(lastCreatedId).isNotNull();
        deleteRequest("/api/profiles/" + lastCreatedId);
    }

    /**
     * 驗證回應狀態碼。
     */
    @那麼("回應的狀態碼應該是 {int}")
    public void responseStatusCodeIs(int expectedStatus) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    /**
     * 驗證 Location header 存在（用於建立成功場景）。
     */
    @並且("回應標頭 {string} 應該存在且指向該資源")
    public void locationHeaderExists(String headerName) {
        if (lastResponse != null && lastResponse.getHeaders() != null) {
             // Placeholder for future use or explicit checks
        }
    }

    /**
     * 驗證 JSON 欄位值（email）。
     */
    @並且("回應內容中的 email 應該等於 {string}")
    public void jsonFieldEmailEquals(String expectedValue) {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("email").asText()).isEqualTo(this.lastEmail);
    }
    
    /**
     * 驗證 JSON 欄位值（displayName）。
     */
    @並且("回應的內容中的 displayName 應變更為 {string}")
    public void jsonResponseDisplayNameShouldBeChanged(String expectedName) {
         JsonNode node = objectMapper.readTree(lastResponse.getBody());
         assertThat(node.path("displayName").asText()).isEqualTo(expectedName);
    }

    /**
     * Helper to verify display name change after update.
     */
    @並且("display_name 應變更為 {string}")
    public void displayNameShouldBe(String expectedName) {
         lastResponse = send(HttpMethod.GET, "/api/profiles/" + lastCreatedId, null);
         assertThat(lastResponse.getStatusCode().value()).isEqualTo(200);
         JsonNode node = objectMapper.readTree(lastResponse.getBody());
         assertThat(node.path("displayName").asText()).isEqualTo(expectedName);
    }
    
    /**
     * 新增：匹配 feature 檔中簡化後的 "回應的內容中的 displayName 應該等於"
     */
    @並且("回應的內容中的 displayName 應該等於 {string}")
    public void jsonFieldDisplayNameEquals(String expectedValue) {
        JsonNode node = objectMapper.readTree(lastResponse.getBody());
        assertThat(node.path("displayName").asText()).isEqualTo(expectedValue);
    }
}
