# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心型別與例外類別。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 及其靜態工廠方法與業務規則驗證邏輯；定義三個 Domain Exception (`InvalidEmailException`, `DuplicateEmailException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：在 `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/` 下建立測試類別，使用 JUnit + AssertJ 驗證 VO 的不可變性、格式檢查（如 Email 正則）、Entity 的建立與更新行為，以及例外拋出的正確性。不依賴 Spring。

## T2 Application Ports (Inbound & Outbound)
- 說明：定義應用層的 Port 介面。Inbound ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 位於 `application/port/in/`；Outbound ports (`SaveProfilePort`, `FindProfilePort`, `RemoveProfilePort`) 位於 `application/port/out/`。這些介面僅描述契約，不含實作。
- 測試：此階段主要確保介面簽名正確且無編譯錯誤。由於是純介面，可透過後續 Use Case 實作的單元測試間接驗證其可用性，或撰寫簡單的 Stub 測試確認依賴方向正確。重點在於確立架構邊界。

## T3 Application Use Cases Implementation
- 說明：實現所有 Use Case Service 類別，位於 `application/usecase/`。每個 Service 注入對應的 Outbound Ports，執行業務邏輯（如 DTO 轉 Domain Object、呼叫 Port、處理例外）。例如 `CreateProfileService` 負責建立 Profile 並調用 `saveProfilePort`。
- 測試：在 `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/` 下為每個 Service 建立測試類別。使用 Mockito 模擬 Outbound Ports，驗證 Service 是否正確調用了 Port 方法、參數是否正確轉換，以及在異常情況下（如 Port 拋出例外）是否正確傳播或轉換例外。不啟動 Spring Context。

## T4 Persistence Adapter (Entity, Repository & Port Implementations)
- 說明：建立持久化適配器。包含 JPA Entity (`ProfileEntity`) 實作 `Persistable<String>`、Spring Data Repository (`JpaProfileRepository`)，以及三個 Port 實作類別 (`JpaSaveProfilePort`, `JpaFindProfilePort`, `JpaRemoveProfilePort`) 和 Converter (`ProfileConverter`)。這些類別位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。注意處理唯一性約束與例外轉換。
- 測試：在 `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/` 下建立測試類別。使用 `@DataJpaTest` + Testcontainers PostgreSQL 進行整合測試。驗證 Entity 映射、Converter 的雙向轉換正確性，以及 Port 實作在正常儲存、查詢、刪除時的行為，特別是捕捉 `DataIntegrityViolationException` 並轉為 `DuplicateEmailException` 的邏輯。

## T5 Bootstrap Configuration Assembly
- 說明：修改 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java`。在此配置類中，以 `@Bean` 方法定義所有 Application Layer 的 Use Case Service (如 `createProfileService`, `getProfileService` 等)，讓 Spring 容器管理它們的生命週期。確保依賴注入鏈完整（Service -> Port Impl）。不新增其他 Configuration 或 Context 測試。
- 測試：利用骨架已提供的 `bootstrap.ApplicationContextTest` 驗證整個 Spring Boot 應用上下文能否成功啟動且無 Bean 衝突或缺失錯誤。此任務重點在於組裝正確性，而非撰寫新測試。

## T6 Web Adapter (Controller, DTOs & Exception Handler)
- 說明：建立 Web 適配器。包含 Request/Response DTO (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`)、REST Controller (`ProfileController`) 處理 HTTP 請求與回應映射，以及 `GlobalExceptionHandler` 將 Domain/Application 例外轉換為對應的 HTTP 狀態碼 (400, 404, 409)。這些類別位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：在 `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/` 下建立測試類別。使用 `@WebMvcTest(ProfileController.class)` + `@MockitoBean` 模擬 Use Case Ports。驗證各端點的路徑、HTTP Method、狀態碼 (201 Created for POST, 200 OK for GET, etc.)、Response Body JSON 結構，以及異常情況下的錯誤訊息格式。不依賴真實資料庫或完整 Context。

## T7 Cucumber BDD Acceptance Tests
- 說明：定義並實現行為驅動開發測試。在 `bootstrap/src/test/resources/features/` 下創建 `personal-profile-crud.feature`（繁體中文 Gherkin），描述 CRUD 操作的成功與失敗場景。在 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/` 下創建步驟定義類 (`ProfileSteps`)，使用 `TestRestTemplate` 呼叫實際運行的 API 端點，並斷言回應內容。確保使用正確的 Cucumber Expression 型別 (`{string}`, `{int}`)。
- 測試：執行 Cucumber Runner，確認所有 Feature 文件中的 Scenario 均通過。這是最終的端到端驗收，涵蓋了從 HTTP 請求到資料庫存取的完整流程，且依賴 Testcontainers PostgreSQL。
