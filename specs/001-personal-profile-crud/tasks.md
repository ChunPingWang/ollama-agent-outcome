# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 領域模型與例外定義
- 說明：建立 Profile 聚合根及其 Value Objects (`ProfileId`, `Email`, `DisplayName`, `Bio`) 與 Domain Exceptions。VO 需包含不變條件驗證邏輯，Entity 需實作 `Persistable<UUID>` 以支援 JPA 持久化狀態追蹤。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/ProfileTest.java`、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/ValueObjectValidationTest.java`。驗證 VO 的建構規則（如 Email 格式、長度限制）及 Entity 的狀態轉換。

## T2 Application Ports 與 Use Case 介面
- 說明：定義 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`LoadProfilePort`, `SaveProfilePort`, `RemoveProfilePort`)，以及相關的 Command/Query DTOs。
- 測試：無獨立執行測試，僅確保編譯通過並符合套件依賴規則（ArchUnit）。此階段主要為後續 Service 實作提供契約。

## T3 Use Case 實作 (Service Layer)
- 說明：實作所有 Use Case Services (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。處理業務邏輯，包括呼叫 Outbound Ports、檢查重複 Email、轉換 Domain Exception。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../GetProfileServiceTest.java`、`.../UpdateProfileServiceTest.java`、`.../DeleteProfileServiceTest.java`。使用 Mockito 模擬 Port，驗證業務流程正確性與例外拋出時機。

## T4 Persistence Adapter 與 JPA Entity
- 說明：建立 `ProfileEntity`、`JpaProfileRepository` 及 Port 實作類別 (`JpaLoadProfilePort`, `JpaSaveProfilePort`, `JpaRemoveProfilePort`)。包含 Domain <-> Entity 的映射邏輯。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaProfilePortsIntegrationTest.java`。使用 Testcontainers PostgreSQL 與 `@DataJpaTest`，驗證實體映射、唯一約束觸發（轉為 `DuplicateEmailException`）及基本 CRUD 操作。

## T5 Bootstrap 組裝與 Context 啟動驗證
- 說明：在 `bootstrap` 模組中建立 `ProfileUseCaseConfiguration`，以 `@Bean` 方式組裝所有 Ports 與 Use Cases。確保 Spring Boot 應用程式上下文能成功啟動且無 Bean 衝突或找不到依賴的問題。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/profile/ProfileContextStartupTest.java`。使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)` 驗證整個應用層與持久化層的整合上下文可正常載入。

## T6 Web Adapter (Controller & Exception Handling)
- 說明：建立 `ProfileController`、Request/Response DTOs (`CreateProfileRequest`, `GetProfileResponse`, etc.) 以及 `GlobalExceptionHandler`。實現 RESTful API 端點並處理 HTTP 狀態碼轉換。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/profile/ProfileControllerTest.java`。使用 `@WebMvcTest` 與 `@MockitoBean` for UseCases，驗證路由映射、輸入驗證（400）、業務例外對應的狀態碼（404, 409）及回應格式。

## T7 Cucumber BDD 驗收測試
- 說明：撰寫 Gherkin Feature 檔 (`profile-crud.feature`) 與 Step Definitions (`ProfileSteps`)。透過 `TestRestTemplate` 呼叫實際 API，驗證完整的使用者旅程。
- 測試：`bootstrap/src/test/resources/features/profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`。執行 Cucumber 場景，確保所有 Spec 需求（建立、查詢、更新、刪除、錯誤處理）均通過整合測試。
