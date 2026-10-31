# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心模型與例外處理。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 以及相關的 Domain Exceptions (`DuplicateEmailException`, `InvalidEmailFormatException`, `ProfileNotFoundException`)。所有邏輯為純 Java，無 Spring/JPA 依賴。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`（驗證實體行為）、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectValidationTest.java`（驗證 Email 格式、DisplayName trim 等不變條件）。

## T2 Application Ports & Use Cases (Create, Get, Update, Delete)
- 說明：定義 Application Layer 的 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。此階段僅含介面與純 Java 實作，不依賴具體持久化技術。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../usecase/GetProfileServiceTest.java`、`.../usecase/UpdateProfileServiceTest.java`、`.../usecase/DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Port，驗證業務流程與例外拋出邏輯。

## T3 Persistence Adapter (JPA Entity, Repository, Port Implementations)
- 說明：建立 JPA Entity (`ProfileEntity`) 實現 `Persistable<String>`，Spring Data Repository (`ProfileRepository`)，以及 Converter (`ProfileConverter`)。實作 Outbound Ports (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)，處理 Domain 與 JPA Entity 之間的轉換及資料庫異常映射（如將 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`）。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`、`.../JpaLoadProfilePortTest.java`、`.../JpaDeleteProfilePortTest.java`、`.../PersistenceIntegrationTest.java`（驗證 Entity Mapping 與 Unique Constraint）。使用 Testcontainers PostgreSQL。

## T4 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap.UseCaseConfiguration` 中註冊所有 Application Service Beans 與 Persistence Port Implementation Beans。確保 Spring Context 能正確組裝依賴鏈。此任務不新增新的 Context 啟動測試（由骨架 `ApplicationContextTest` 覆蓋），但需確保 Bean 定義無衝突且可被掃描或明確宣告。
- 測試：執行現有的 `bootstrap.ApplicationContextTest` 以驗證整個應用程式上下文能否成功啟動並注入所有 Bean。若失敗則修正 `UseCaseConfiguration`。

## T5 Web Adapter (Controller, DTOs, Exception Handler)
- 說明：建立 REST Controller (`ProfileController`)，對應四個 HTTP 端點。定義 Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`) 並加入 Jakarta Validation 註解。實作 `GlobalExceptionHandler` 將 Domain Exceptions 映射為正確的 HTTP 狀態碼 (201, 200, 204, 400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`。使用 `@WebMvcTest` + `MockitoBean` 模擬 Use Case，驗證請求路由、JSON 序列化/反序列化、驗證錯誤回應及例外處理邏輯。

## T6 BDD Acceptance Tests (Cucumber E2E)
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，透過真實的 Spring Context（含 Testcontainers DB）進行端到端測試。涵蓋創建、查詢、更新、刪除個人資料的成功路徑以及各種異常情況（重複 Email、找不到 ID 等）。
- 測試：`bootstrap/src/test/resources/features/001-personal-profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`。執行 Cucumber Runner 確保所有 Scenario Pass。
