# Tasks 001 — 個人資料管理 (Personal Profile CRUD)

## T1 領域模型與例外定義
- 說明：建立 Domain 層的 Value Objects、Entity 以及 Domain Exceptions。包含 `ProfileId`、`Email`、`Name` 值物件，`Profile` 實體類，以及 `DuplicateEmailException`、`InvalidEmailFormatException`、`ProfileNotFoundException` 例外類別。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：在 `domain/src/test/java/.../domain/` 下建立 `DomainModelTest.java`，使用純 JUnit + AssertJ 驗證值物件的不可變性、格式驗證規則（如 Email 格式），以及 Entity 的基本狀態一致性。

## T2 Application Port 介面定義
- 說明：建立 Application 層的 Inbound Ports (`CreateProfileUseCase`, `LoadProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `FindProfileByIdPort`, `DeleteProfilePort`)。這些介面僅定義方法簽章，不包含實作邏輯。路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `port/out/`。
- 測試：此階段無獨立可執行的行為測試，主要確保編譯通過並符合架構依賴方向（Application 不依賴 Adapter）。

## T3 Use Case 實作 (Create & Load)
- 說明：實作 `CreateProfileService` 與 `LoadProfileService`。`CreateProfileService` 負責將 Command 轉換為 Domain Object 並呼叫 `SaveProfilePort`；`LoadProfileService` 負責查詢並處理找不到的情況。路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`。
- 測試：在 `application/src/test/java/.../usecase/` 下建立 `CreateProfileServiceTest.java` 與 `LoadProfileServiceTest.java`。使用 Mockito 模擬 Outbound Port，驗證業務流程、例外拋出時機（如找不到資料時拋出 `ProfileNotFoundException`）以及對 Port 的呼叫次數。

## T4 Use Case 實作 (Update & Delete)
- 說明：實作 `UpdateProfileService` 與 `DeleteProfileService`。`UpdateProfileService` 需先載入現有 Profile，更新欄位後儲存，並處理唯一性衝突；`DeleteProfileService` 需確認存在後刪除。路徑同 T3。
- 測試：在 `application/src/test/java/.../usecase/` 下建立 `UpdateProfileServiceTest.java` 與 `DeleteProfileServiceTest.java`。驗證更新邏輯、刪除前的存在性檢查，以及重複 Email 或不存在 ID 時的例外行為。

## T5 Persistence Adapter 實作
- 說明：建立 JPA Entity (`ProfileEntity`)、Repository (`JpaProfileRepository`) 以及 Outbound Ports 的具體實作 (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaDeleteProfilePort`)。負責 Domain Object 與 JPA Entity 之間的轉換，並處理資料庫約束異常轉譯為 Domain Exception。路徑為 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：在 `adapter-persistence/src/test/java/.../persistence/` 下建立 `PersistenceAdapterTest.java`（或使用多個類別）。使用 `@DataJpaTest` + Testcontainers PostgreSQL，驗證 Entity 映射正確性、Port 實作的 CRUD 功能、唯一性約束觸發時是否正確拋出 `DuplicateEmailException`，以及刪除不存在的資料時的行为。

## T6 Bootstrap 組裝 (UseCaseConfiguration)
- 說明：修改 `bootstrap/src/main/java/.../bootstrap/UseCaseConfiguration.java`，以 `@Bean` 方法手動組裝所有 Use Case Service 與其依賴的 Port 實作。確保 Spring Context 能正確啟動並注入所有 Bean。**不要**新增其他 Configuration 或 Context 測試。
- 測試：執行骨架提供的 `bootstrap.ApplicationContextTest.java`，確認應用程式上下文無錯誤啟動且所有 Bean 可被解析。

## T7 Web Adapter 實作
- 說明：建立 REST Controller (`ProfileController`)、DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 與全域異常處理器 (`GlobalExceptionHandler`)。處理 HTTP 請求回應轉換、Jakarta Validation 註解標記，並將 Domain Exception 映射為正確的 HTTP Status Code (201, 400, 404, 409, 204)。路徑為 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：在 `adapter-web/src/test/java/.../web/` 下建立 `WebAdapterTest.java`（或使用多個類別）。使用 `@WebMvcTest(ProfileController.class)` + `@MockitoBean` for Use Cases，驗證各端點的 HTTP 狀態碼、Header (Location)、Response Body 結構以及例外處理邏輯。

## T8 Cucumber BDD 驗收測試
- 說明：建立 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與步驟定義類 (`PersonalProfileSteps`)。Feature 檔使用繁體中文 Gherkin 語法，涵蓋 CRUD 所有正常與異常情境。步驟定義位於 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/`，透過 `TestRestTemplate` 呼叫實際 API 進行端到端驗證。
- 測試：執行 Cucumber Runner，確保所有 Scenario 通過，驗證從 Web 層到資料庫層的完整整合流程。
