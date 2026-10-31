# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心模型與例外處理。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 以及相關的 Domain Exceptions (`InvalidEmailException`, `DuplicateEmailException`, `ProfileNotFoundException`)。所有邏輯需符合 Spec 中的不變條件與業務規則。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/EmailTest.java` (驗證格式)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayNameTest.java` (驗證清洗與長度)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileTest.java` (驗證建構與狀態)。

## T2 Application Layer Ports & Use Cases
- 說明：定義 Application 層的 Inbound Port 介面 (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port 介面 (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)，並實作對應的 Service (`*Service`)。Service 負責協調 Port 呼叫與異常轉換（如將 DB 重複鍵錯誤轉為 Domain Exception）。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/usecase/CreateProfileServiceTest.java`、`.../usecase/GetProfileServiceTest.java`、`.../usecase/UpdateProfileServiceTest.java`、`.../usecase/DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Ports，驗證業務邏輯流程與例外拋出。

## T3 Persistence Adapter Implementation
- 說明：建立 JPA Entity (`ProfileEntity`)、Repository (`ProfileRepository`) 以及 Outbound Port 的具體實作類別 (`SaveProfilePortImpl`, `LoadProfilePortImpl`, `DeleteProfilePortImpl`)。處理 Domain Object 與 JPA Entity 之間的轉換，並確保唯一性約束檢查與 `saveAndFlush` 的使用以觸發資料庫層級的驗證。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/persistence/port/SaveProfilePortImplTest.java` (含 Testcontainers PostgreSQL)、`.../persistence/port/LoadProfilePortImplTest.java`、`.../persistence/port/DeleteProfilePortImplTest.java`。驗證資料持久化行為、轉換正確性及 DB 約束異常映射。

## T4 Bootstrap Configuration & Context Verification
- 說明：在 `bootstrap` 模組中建立 `UseCaseConfiguration`，將 Application 層的 Services 與 Persistence 層的 Ports 實作註冊為 Spring Beans。此任務不寫新功能碼，而是組裝前述所有元件，並透過啟動 Spring Context 來驗證依賴注入無誤。
- 測試：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/BootstrapContextTest.java`。使用 `@SpringBootTest` 搭配 `@Import(TestcontainersConfiguration.class)` 驗證應用程式上下文能否成功啟動且 Bean 可被解析。

## T5 Web Adapter (REST API)
- 說明：建立 REST Controller (`ProfileController`)、DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 以及全域例外處理器 (`GlobalExceptionHandler`)。實現 HTTP 方法對應的 CRUD 操作，處理輸入驗證（Jakarta Validation）並將 Domain Exceptions 轉為正確的 HTTP Status Code (201, 200, 204, 400, 404, 409)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/web/controller/ProfileControllerTest.java`。使用 `@WebMvcTest` 與 `MockitoBean` 模擬 Inbound Ports，驗證請求路由、回應狀態碼、JSON 序列化及例外映射邏輯。

## T6 Cucumber BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與 Step Definitions (`ProfileSteps`)。透過 `TestRestTemplate` 呼叫實際運作的 API (需依賴 Bootstrap 組裝好的 Context)，以行為驅動開發的方式驗收完整的 CRUD 流程，包含正常路徑與錯誤情境。
- 測試：`bootstrap/src/test/resources/features/personal-profile-crud.feature` 與 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`。執行 Cucumber Runner 確保所有 Scenario Pass。
