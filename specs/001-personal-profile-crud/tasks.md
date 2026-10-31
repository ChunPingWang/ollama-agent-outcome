# Tasks 001 — Personal Profile CRUD

## T1 Domain Layer: Value Objects & Entity Exceptions
- 說明：建立領域層的核心值物件與例外類別。包含 `ProfileId`、`Email`、`DisplayName`、`Bio` 四個 Value Object (record)，以及對應的驗證邏輯與例外 (`InvalidEmailException`, `InvalidDisplayNameException`)。同時定義 `DuplicateEmailException` 用於後續 Port 層的轉換。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/EmailTest.java` (驗證合法/非法 Email)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayNameTest.java` (驗證長度限制)。

## T2 Application Layer: Ports Definition
- 說明：在 application 模組中定義 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`)，並定義相關的 Command records。此階段僅有介面與資料結構，無實作。
- 測試：編譯通過即可，確保套件依賴方向正確（application 不依賴 adapter）。可加入簡單的 ArchUnit 測試確認 port 命名規則符合規範。

## T3 Domain Entity & Business Logic
- 說明：建立 `Profile` 領域實體類別。包含建構子、更新方法 (`updateDisplayName`, `updateBio`) 及狀態管理。由於 PUT 是全量替換，設計上需能從 DTO/Command 重建或更新狀態。同時處理 ID 生成邏輯的入口（通常由 Use Case 層決定是否預先產生 UUID，此處保留 ID 欄位）。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileTest.java` (驗證 Profile 的建立、更新行為、不可變性約束)。

## T4 Application Services: Use Cases Implementation
- 說明：實作所有 Use Case Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。這些服務注入 Outbound Ports，負責協調業務流程：例如 Create 時呼叫 Save Port，Update 時先 Load 再 Validate 最後 Save。使用 Mockito 模擬 Port 進行單元測試。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`

## T5 Persistence Adapter: Entity, Repository & Port Implementations
- 說明：在 adapter-persistence 模組中建立 JPA Entity (`ProfileEntity`)，需實作 `Persistable<UUID>` 以處理 ID 生成與 isNew 狀態。建立 Spring Data Repository (`JpaProfileRepository`)。實作 Outbound Ports (`JpaSaveProfilePort`, `JpaLoadProfilePort`)，包含 Domain <-> Entity 的轉換邏輯，並捕捉 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfileRepositoryTest.java` (基礎 CRUD)
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePortTest.java` (驗證重複 Email 拋出正確例外、isNew 狀態管理)。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePortTest.java` (驗證載入行為)。

## T6 Bootstrap Configuration & Context Assembly
- 說明：在 bootstrap 模組中建立 `UseCaseConfiguration`，將 Application Services 與 Persistence Port Implementations 組裝成 Spring Beans。確保所有依賴注入正確，且應用程式上下文可正常啟動（含 Testcontainers PostgreSQL）。此步驟不寫新功能碼，主要確認架構整合無誤。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/BootstrapContextTest.java` (`@SpringBootTest` + `@Import(TestcontainersConfiguration.class)`，驗證 Context 啟動成功且所有 Bean 存在)。

## T7 Web Adapter: Controller, DTOs & Exception Handler
- 說明：在 adapter-web 模組中建立 REST Controller (`ProfileController`)、Request/Response DTOs 以及 `GlobalExceptionHandler`。實作 HTTP 端點映射，處理輸入驗證錯誤轉為 400，Domain 例外轉為對應的 HTTP Status Code (404, 409)。使用 `@WebMvcTest` 進行單元測試，Mock 所有的 Inbound Ports。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (涵蓋 POST 201, GET 200/404, PUT 200/404/409, DELETE 204/404)。
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandlerTest.java` (驗證例外轉換邏輯)。

## T8 BDD Acceptance Tests with Cucumber
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions。Feature 檔描述個人資料 CRUD 的行為（繁體中文 Gherkin）。Step Definitions 位於 bootstrap 的 bdd 套件，透過 `TestRestTemplate` 呼叫實際 API，並依賴完整的 Spring Context (含 DB)。這是最終驗收階段。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
    - 執行 Cucumber Runner 確認所有 Scenario Passed。
