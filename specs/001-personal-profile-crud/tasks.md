# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、聚合根與自訂例外。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本格式驗證)、`DisplayName` (空白處理)、`Profile` (Aggregate Root, immutable state via methods) 以及 `DuplicateEmailException`, `ProfileNotFoundException`, `InvalidEmailException`。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證合法/非法 Email), `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java` (驗證 null/blank 轉換為 ""), `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證建構、更新邏輯、狀態不可變性)。

## T2 Application Ports & Use Case Implementations
- 說明：定義 Application 層的 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `FindProfileByIdPort`, `ExistsByEmailPort`, `DeleteProfileByIdPort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。使用 Mockito 模擬 Outbound Port。路徑位於 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/out/usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java` (Happy path, Duplicate email via mock), `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java` (Found, Not Found throws exception), `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java` (Update existing, Email conflict with other ID, Not Found update), `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java` (Delete existing, Not Found delete).

## T3 Persistence Adapter & JPA Entity
- 說明：建立 JPA Entity `ProfileEntity` (implement `Persistable<UUID>`)、Spring Data Repository `ProfileRepository` 以及四個 Port 的 JPA 實作類別 (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaExistsByEmailPort`, `JpaDeleteProfileByIdPort`)。處理 Domain 與 JPA Entity 之間的轉換，並確保唯一性約束觸發時拋出正確的例外。路徑位於 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java` (Verify insert/update logic and duplicate email constraint translation), `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPortTest.java` (Verify findById mapping), `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPortTest.java` (Verify exists check), `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPortTest.java` (Verify delete behavior).

## T4 Bootstrap Configuration & Context Assembly
- 說明：在 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` 中宣告所有 Use Case Service Bean，注入對應的 Outbound Port Bean（由 adapter-persistence 模組提供）。此任務僅修改組態檔，確保 Spring Context 能正確啟動並綁定依賴。骨架已提供 `ApplicationContextTest` 驗證 context 啟動，故不需新增新的 Context 測試類別，但需確保現有測試通過。
- 測試：執行現有的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 以驗證 Context 啟動無誤；執行 Application 層與 Persistence 層的單元測試以確認整合無誤。

## T5 Web Adapter: Controller, DTOs & Exception Handler
- 說明：建立 REST Controller `ProfileController`、Request/Response DTO (`CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`) 以及 `GlobalExceptionHandler`。實作 CRUD endpoints 並呼叫 Application Inbound Ports。路徑位於 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (使用 `@WebMvcTest` + Mock Use Cases)。驗證 POST 201+Location, GET 200, PUT 200, DELETE 204, 400 Validation Error, 404 Not Found, 409 Conflict。

## T6 Cucumber BDD Integration Tests
- 說明：撰寫 Cucumber Feature 檔與 Step Definitions，透過 HTTP 實際呼叫 API 進行端到端測試。Feature 檔位於 `bootstrap/src/test/resources/features/personal-profile-crud.feature`，Step Definitions 位於 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`。使用 `TestRestTemplate` 搭配 Testcontainers PostgreSQL。
- 測試：執行 Cucumber Runner，驗證所有 Gherkin scenarios 通過（包含 Happy Path、邊界條件、錯誤處理）。
