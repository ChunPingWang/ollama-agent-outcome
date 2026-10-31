# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心 Value Objects、Entity 以及 Domain Exceptions。包含 `ProfileId`, `Name`, `Email`, `Bio` (records), `Profile` (class with update logic)，以及 `ProfileNotFoundException`, `DuplicateEmailException`, `InvalidInputException`。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (測試 Entity 的建立、更新行為及不變條件)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectsTest.java` (測試 VO 的不可變性與基本驗證)。

## T2 Application Ports Definition
- 說明：定義 Application 層的 Inbound Port Interfaces (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port Interfaces (`SaveProfilePort`, `LoadProfilePort`, `ExistsByEmailPort`, `DeleteProfileByIdPort`)，以及相關的 Command/Query/Result DTOs (`CreateProfileCommand`, `GetProfileQuery`, `ProfileResult`, etc.)。這些介面與資料結構位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 和 `application/port/out/` 及 `usecase/`。
- 測試：無獨立執行測試（僅定義介面），但需確保編譯通過並符合命名規範。

## T3 Use Case Implementations & Unit Tests
- 說明：實作四個 Use Case Services (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。邏輯包含業務規則檢查（如 Email 唯一性、存在性檢查）並呼叫對應的 Outbound Ports。使用 Mockito Mock Outbound Ports 進行單元測試。類別位於 `application/usecase/`。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java` (驗證 Email 重複檢查、成功建立)
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java` (驗證找到/找不到情境)
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java` (驗證更新邏輯與衝突處理)
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java` (驗證刪除前存在性檢查)

## T4 Persistence Adapter: Entity, Repository & Port Implementation
- 說明：建立 JPA Entity (`ProfileEntity` implementing `Persistable<UUID>`)，Spring Data Repository (`ProfileJpaRepository`)，以及四個 Port 實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaExistsByEmailPort`, `JpaDeleteProfileByIdPort`)。處理 Domain 到 JPA Entity 的轉換、例外映射（如 `DataIntegrityViolationException` -> `DuplicateEmailException`）及 `isNew` 狀態管理。類別位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntityTest.java` (測試 Entity 欄位映射與 Persistable 行為)
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePortTest.java` (Mock Repository 測試轉換邏輯與例外捕獲)
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePortTest.java` (Mock Repository 測試載入與轉換)
    - *(註：為保持任務獨立性且符合 TDD，先以 Mock Repo 測試 Port 實作邏輯；若需整合測試可後續補充，但此階段確保 Port 實現正確)*

## T5 Bootstrap Configuration & Assembly
- 說明：在 `bootstrap.UseCaseConfiguration` (`@Configuration`) 中宣告所有 Use Case Services Bean (`CreateProfileService`, etc.)。由於 Outbound Ports 也在 Adapter 層定義，Bootstrap 需要能夠找到它們（通常透過 Component Scan 或明確 Import）。根據規則，我們在此配置 Use Case Beans，並假設 Spring Boot 的自動掃描能發現 Adapter 層的 Port 實現（若未加註解則需在 Config 中手動注入或標記 Port 類為 @Component/@Service）。*修正策略*：為了嚴格遵守 "use case bean 只能在 UseCaseConfiguration 以 @Bean 定義"，我們將 Port 實作類別標記為 `@Service` (Adapter 層允許)，讓 Spring Context 自動偵測，然後在 `UseCaseConfiguration` 中僅負責將這些已存在的 Bean 注入到 Use Case Service 中並註冊 Use Case Service 為 Bean？不，Spring 會自動處理依賴注入。如果 `UseCaseConfiguration` 只定義了 Service Bean，而 Service 依賴於 Port Bean（由 Component Scan 提供），這是可以的。關鍵是 **不要** 在 `UseCaseConfiguration` 裡重複定義 Port Bean，也不要建立新的 Configuration 類別來定義 Port Bean。
- 測試：利用骨架提供的 `bootstrap.ApplicationContextTest.java` 驗證整個應用程式上下文能否成功啟動且無 BeanDefinitionOverrideException。

## T6 Web Adapter: Controller, DTOs & Exception Handler
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequest`, etc.) 以及 `GlobalExceptionHandler`。實現 HTTP 方法映射、Jakarta Validation 註解、狀態碼轉換（201, 400, 404, 409, 204）。類別位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java` (使用 `@WebMvcTest` + `@MockitoBean` for Services，測試各端點的路徑參數綁定、請求體解析、HTTP 狀態碼與例外處理)

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature File (`personal-profile-crud.feature`) 與 Step Definitions (`ProfileStepDefinitions`)。Step Definitions 使用 `TestRestTemplate` 呼叫實際 API，並透過 Testcontainers PostgreSQL 進行端到端驗證。Feature 檔覆蓋所有 Spec 中的 Acceptance Criteria。檔案位於 `bootstrap/src/test/resources/features/` 和 `bdd/` 套件。
- 測試：執行 Cucumber Runner，確保所有 Scenario 通過。這是最終的整合驗收測試。
