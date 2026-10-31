# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId`、`Name`、`Email` 值物件（含不變條件驗證）、`Profile` 領域實體（不可變狀態管理），以及 `ProfileNotFoundException`、`DuplicateEmailException`、`InvalidEmailException`、`InvalidNameException` 等自訂例外。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileTest.java`（驗證建構邏輯與不變條件）、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ValueObjectValidationTest.java`（驗證 Name 與 Email 的無效輸入拋出對應例外）。

## T2 Application Ports (Inbound & Outbound)
- 說明：定義應用層的介面。Inbound ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 及其相關的 Command/Query/Result records；Outbound ports (`LoadProfilePort`, `SaveProfilePort`) 用於資料存取抽象化。此階段僅產生介面與 DTO，不含實作。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCaseContractTest.java`（確認介面簽名正確性，雖為空測試但確保編譯通過）、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/dto/DtoStructureTest.java`（驗證 Record 結構與欄位類型符合預期）。

## T3 Use Case Implementations (Services)
- 說明：實作四個 Use Case Service 類別。包含業務規則邏輯：建立時檢查重複、更新時排除自身檢查重複、查詢不存在時拋出例外、刪除前確認存在等。依賴 Mock 後的 Port 進行單元測試。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`（成功建立、Email 重複異常）
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`（找到資料、找不到拋出例外）
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`（成功更新、ID 不存在、新 Email 與其他記錄衝突）
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`（成功刪除、ID 不存在）

## T4 Persistence Adapter & Ports Implementation
- 說明：建立 JPA Entity (`ProfileEntity`)，實現 `Persistable<UUID>`；建立 Spring Data Repository (`JpaProfileRepository`)；實作 Outbound Ports (`JpaLoadProfilePort`, `JpaSaveProfilePort`)，負責 Domain 與 Entity 的轉換及資料庫操作，並處理 `DataIntegrityViolationException` 轉為 Domain Exception。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaPersistenceIntegrationTest.java`（使用 Testcontainers PostgreSQL 驗證 Entity Mapping、Port 的 CRUD 行為、唯一性約束觸發的異常轉換）。

## T5 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap` 模組中建立 `UseCaseConfiguration`，以 `@Bean` 方式組裝所有 Use Case Service 與 Port 實作類別。確保 Spring Context 能正確啟動且依賴注入無誤。此步驟必須在 Web Adapter 之前完成，以便後續 Controller 能找到 Bean。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/ApplicationContextStartupTest.java`（使用 `@SpringBootTest` 加載完整上下文，驗證所有相關 Bean 存在且可取得，無 `BeanDefinitionOverrideException` 或找不到 Bean 錯誤）。

## T6 Web Adapter (Controller, DTOs, Handler)
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs、以及 `GlobalExceptionHandler`。實現 HTTP 端點映射、輸入驗證（Jakarta Validation）、Domain Exception 到 HTTP Status Code 的轉換（400, 404, 409），並處理標準 HTTP 語義（201 Created with Location, 204 No Content 等）。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`（使用 `@WebMvcTest` + Mock Ports 驗證路由、狀態碼、回應格式）
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandlerTest.java`（驗證例外轉譯邏輯正確）

## T7 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions。Feature 檔描述個人資料 CRUD 的行為場景（繁體中文 Gherkin）；Step Definition 透過 `TestRestTemplate` 呼叫實際 API，驗證端到端的業務規則與 HTTP 行為。確保每個 Scenario 獨立且可重複執行。
- 測試：`bootstrap/src/test/features/personal-profile-crud.feature`（定義場景）、`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/ProfileSteps.java`（實作步驟邏輯，包含前置條件設置與結果斷言），並通過 Cucumber JUnit Platform Runner 執行驗證所有場景 Pass。
