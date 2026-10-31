# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立純 Java 領域物件，包含值物件 `ProfileId`、實體 `Profile`（含工廠方法與驗證邏輯）及四個領域例外類別。此階段不依賴任何框架。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`（驗證 `create` 與 `update` 的 invariant，如空姓名、無效 Email 格式拋出例外；驗證正常建立與更新回傳新實例）。

## T2 Application Ports 與 Use Case 實作
- 說明：建立 `application` 層的 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，以及對應的 Service 實作 (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。包含 Request DTO (`CreateProfileRequest`, `UpdateProfileRequest`)。
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`（驗證成功建立回傳 ID、Port 拋出 `DuplicateEmailException` 時向上拋出）。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`（驗證找到資料回傳 Profile、找不到時拋出 `ProfileNotFoundException`）。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`（驗證成功更新、找不到時拋出例外、Email 重複時拋出例外）。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`（驗證成功刪除、找不到時拋出例外）。

## T3 Persistence Adapter 與 Port 實作
- 說明：建立 `adapter-persistence` 層的 JPA Entity (`ProfileEntity`)、Repository (`ProfileRepository`)、Converter (`ProfileConverter`) 以及 Port 實作 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。處理 Domain 與 JPA Entity 的轉換，並處理唯一性約束的例外轉換。
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`（使用 `@DataJpaTest` + Testcontainers，驗證正常儲存、Email 重複時拋出 `DuplicateEmailException`）。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`（驗證 `findById` 正確轉換並回傳 Domain 物件）。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`（驗證 `deleteById` 正確刪除資料）。

## T4 Bootstrap 組裝與 Context 驗證
- 說明：在 `bootstrap` 模組建立 `UseCaseConfiguration`，以 `@Bean` 定義所有 Use Case Service 與 Port 實作。確保 Spring Context 能正確啟動並注入所有依賴。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfigurationTest.java`（使用 `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)`，驗證 Context 啟動成功，並可透過 `ApplicationContext` 取得所有 Use Case Bean 且無循環依賴或注入失敗）。

## T5 Web Adapter 與錯誤處理
- 說明：建立 `adapter-web` 層的 `ProfileController`、DTOs (`CreateProfileRequestDto`, `UpdateProfileRequestDto`, `ProfileResponseDto`) 與 `ProfileControllerAdvice`。實現 RESTful 端點與 HTTP 狀態碼映射。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`（使用 `@WebMvcTest` + `@MockitoBean` 模擬 Use Cases，驗證各端點的 HTTP 狀態碼、回應內容、驗證錯誤 400、404、409 等）。

## T6 Cucumber BDD 驗收測試
- 說明：建立 Cucumber Feature 檔 (`profile-crud.feature`) 與 Step Definitions (`ProfileStepDefinitions`)。使用 `TestRestTemplate` 對真實 API 進行整合測試，涵蓋所有 Spec 場景。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java`（Cucumber Runner，執行 `profile-crud.feature`，驗證所有場景通過）。
