# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心模型與例外處理。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Aggregate Root (`Profile`) 以及相關的 Domain Exception (`ProfileNotFoundException`, `DuplicateEmailException`, `InvalidEmailFormatException`)。所有類別直接位於 `domain` 套件下。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證 Entity 行為與不變條件)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectTest.java` (驗證 VO 的不可變性、equals/hashCode 及格式驗證)。

## T2 Application Ports & Use Cases Implementation
- 說明：定義 Inbound Port 介面 (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port 介面 (`SaveProfilePort`, `FindProfileByIdPort`, `ExistsByEmailPort`, `DeleteProfileByIdPort`)，並實作對應的 Service (`...Service`)。Service 需依賴 Port 介面進行業務邏輯控制（如重複 Email 檢查、存在性確認）。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`GetProfileServiceTest.java`、`UpdateProfileServiceTest.java`、`DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Ports，驗證 Use Case 的執行流程與例外拋出時機。

## T3 Persistence Adapter: Entity, Repository & Converter
- 說明：建立 JPA Entity (`ProfileEntity`)，需處理 ID 生成策略（根據 Spec 分析，POST 由 Server 生成 UUID，故使用 `@GeneratedValue(strategy = GenerationType.UUID)`；若後續發現需 Client Provided ID 則調整為 Persistable，此處先採標準生成以簡化，或依 Prompt 提示採用 Persistable 模式以支援硬刪除與明確 ID 管理，這裡決定採用 **Server Generated UUID** 配合標準 JPA，因為 POST body 無 ID 欄位）。建立 Spring Data Repository (`ProfileRepository`) 與 Domain <-> Entity 轉換器 (`ProfileConverter`)。注意：Port 實作類別在此階段暫不建立，僅建立基礎結構與轉換邏輯測試準備。*修正*：為了讓 Application 層能獨立測試 Port 介面，我們先在 adapter-persistence 模組定義 Entity 和 Repository，並撰寫對應的單元測試來確保映射正確。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntityTest.java` (驗證 JPA 註解、建構子)、`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverterTest.java` (驗證雙向轉換)。

## T4 Persistence Adapter: Port Implementations & Integration Test
- 說明：在 `adapter-persistence` 模組中實作 Outbound Ports (`SaveProfilePortImpl`, `FindProfileByIdPortImpl`, `ExistsByEmailPortImpl`, `DeleteProfileByIdPortImpl`)。這些實作類將依賴 `ProfileRepository` 和 `ProfileConverter`。使用 Testcontainers PostgreSQL 進行整合測試，確保唯一性約束（Duplicate Email）能正確觸發例外。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/SaveProfilePortIntegrationTest.java`、`FindProfileByIdPortIntegrationTest.java` 等。驗證資料庫操作與 Domain Exception 的轉換。

## T5 Bootstrap Configuration
- 說明：在 `bootstrap` 模組的 `UseCaseConfiguration` 中組裝 Bean。定義所有 Application Service 為 Spring Bean，並注入對應的 Port 介面（Spring 會自動尋找 adapter-persistence 中的實現類別，或在此顯式宣告）。**僅修改現有的 UseCaseConfiguration，不新增新的 @Configuration 類別**。
- 測試：利用骨架提供的 `bootstrap.ApplicationContextTest` 驗證 Context 啟動成功。無需新增額外的 Context 測試。

## T6 Web Adapter: Controller, DTOs & Error Handling
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 以及全域異常處理器 (`GlobalExceptionHandler`)。將 HTTP 請求映射到 Application Inbound Ports。處理 Jakarta Validation 錯誤與 Domain Exceptions 轉為適當的 HTTP Status Code (201, 400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`。使用 `@WebMvcTest` + `MockitoBean` 模擬 Use Case，驗證路由、狀態碼、JSON 序列化與驗證訊息。

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`profile-crud.feature`) 與步驟定義 (`ProfileSteps`)。在 bootstrap 模組中執行端到端測試，使用 TestRestTemplate 呼叫實際 API，並透過 Testcontainers PostgreSQL 提供真實資料庫環境。涵蓋 CRUD 正常流程、重複 Email 衝突、資源不存在等情境。
- 測試：`bootstrap/src/test/resources/features/profile-crud.feature` 配合 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`。確保所有 Gherkin 場景通過。
