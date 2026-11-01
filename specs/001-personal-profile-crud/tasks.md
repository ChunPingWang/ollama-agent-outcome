# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本格式驗證)、`DisplayName` (長度限制)，以及 `Profile` 聚合根（封裝業務邏輯如更新顯示名稱）。同時定義 `InvalidEmailException`、`DuplicateEmailException`、`ProfileNotFoundException`。所有類別位於 `domain` 套件，無子目錄。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證格式與長度)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java` (驗證長度)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證建構不變條件與行為)。使用純 JUnit + AssertJ。

## T2 Application Ports Definition
- 說明：在 `application.port.in` 定義四個 Use Case 介面 (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`)；在 `application.port.out` 定義四個 Port 介面 (`SaveProfilePort`, `LoadProfilePort`, `ExistsByEmailPort`, `DeleteProfilePort`)。建立對應的 Request/Result Record DTOs 於 application 層或 domain 層（此處為簡化依賴，DTOs 可放在 application port in/out 相關位置或直接作為參數，依系統提示，inbound port 接收 request object，outbound port 操作 domain entity）。根據規則，Request/Response DTO 通常在 Web Adapter，但 Use Case 簽名需要輸入輸出型別。我們將 Create/Update/Delete 的請求物件定義在 `application.port.in` 包內或作為獨立 record 放在 application 根目錄？系統提示說 "adapter-web... Request/Response DTO"，但 Use Case 接口需要參數。通常 Use Case 接受 Domain Object 或專用的 Command Object。為了符合 "Command/DTO 不可單獨成為任務"，我們在此階段僅定義 Interface 和必要的 Value Objects (如 ProfileId)。若 Need specific command objects for use case inputs, we can define them here or let the Service implementation handle conversion from a generic structure. However, clean hexagonal usually has specific input models for use cases. Let's put simple Input Records in `application.port.in` if they are tightly coupled to that use case, or just pass Domain Entities where possible. For CreateProfile, we need Email and DisplayName strings -> convert to VOs inside Service? Or pass VO? Passing VOs is cleaner. So `CreateProfileUseCase.execute(CreateProfileInput)` where `CreateProfileInput` contains `Email` and `DisplayName`. We will place these small input records in their respective port packages or a common application package. To keep it simple and strictly follow "no sub-packages except port.in/out/usecase", we'll put `CreateProfileRequest`, `UpdateProfileRequest` etc. directly in `com.github.chunpingwang.ollamaagentoutcome.application.port.in` and result records in `port.out` or same package. Actually, better: Put Request/Response DTOs needed by Use Cases in `application.port.in` and `application.port.out` respectively as inner classes or top-level files in those packages.
- 測試：無編譯期測試，僅確保介面定義正確。此階段主要為後續實作鋪路，可透過 T2 的應用層測試間接驗證接口可用性（若使用 mock）。但通常 Interface 本身不需單元測試，除非有預設方法。我們將此任務視為「定義契約」。

## T3 Application Services (Inbound) & Port Implementations Stubbing Logic
- 說明：在 `application.usecase` 包中實作四個 Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)。這些類別依賴 outbound ports。同時，為了讓測試能運行，我們不需要在此時寫 Persistence Adapter，只需關注 Application Layer 的邏輯與 Mocking。
    - `CreateProfileService`: 檢查 email exists -> save profile.
    - `GetProfileService`: load profile -> throw if not found.
    - `UpdateProfileService`: load profile -> update display name -> save.
    - `DeleteProfileService`: load profile -> delete.
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../GetProfileServiceTest.java`、`.../UpdateProfileServiceTest.java`、`.../DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Ports，驗證 Use Case 的行為順序與例外拋出（如 `DuplicateEmailException`）。

## T4 Persistence Adapter: Entity, Repository & Port Implementations
- 說明：在 `adapter-persistence` 模組建立 JPA Entity `ProfileEntity` (實現 `Persistable<UUID>`)、Spring Data Repository `ProfileRepository`。實作四個 Outbound Port (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaExistsByEmailPort`, `JpaDeleteProfilePort`)，負責 Domain Object 與 JPA Entity 之間的轉換，並處理資料庫操作細節（如唯一性約束異常捕獲）。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`、`.../JpaLoadProfilePortTest.java`、`.../JpaExistsByEmailPortTest.java`、`.../JpaDeleteProfilePortTest.java`。使用 `@DataJpaTest` + Testcontainers PostgreSQL，驗證資料持久化、查詢正確性及唯一性約束觸發的例外。

## T5 Bootstrap Configuration Assembly
- 說明：修改 `bootstrap` 模組中的 `UseCaseConfiguration`。以 `@Bean` 方式組裝所有 Application Services 和 Persistence Port Implementations。確保依賴注入鏈完整（Service -> Port Impl -> Repository）。不新增其他 Configuration 類別或 Context 測試。
- 測試：利用骨架提供的 `ApplicationContextTest` (位於 bootstrap) 啟動 Spring Context，驗證所有 Bean 能否成功載入且無衝突。若需額外驗證，可寫一個簡單的 `IntegrationConfigTest` 但系統提示說「不要再寫新的 context 測試」，故僅依賴現有骨架測試即可確認組裝成功。

## T6 Web Adapter: Controller, DTOs & Exception Handler
- 說明：在 `adapter-web` 模組建立 REST API。包含 `ProfileController` (`POST /profiles`, `GET /profiles/{id}`, `PUT /profiles/{id}`, `DELETE /profiles/{id}`)、對應的 Request/Response Dtos (`CreateProfileRequestDto`, etc.) 以及 `GlobalExceptionHandler`。Handler 將 HTTP 狀態碼映射到 Domain Exceptions。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`。使用 `@WebMvcTest` + `@MockitoBean` 模擬 Use Case Interfaces，驗證路由映射、請求參數綁定、回應格式及例外轉換為正確的 HTTP 狀態碼（201, 400, 404, 409）。

## T7 Cucumber BDD Acceptance Tests
- 說明：在 `bootstrap/src/test/resources/features` 建立 `personal-profile-crud.feature` (繁體中文 Gherkin)。在 `com.github.chunpingwang.ollamaagentoutcome.bdd` 套件中實作步驟定義。使用 `TestRestTemplate` 呼叫實際啟動的 Spring Boot Context (含 Testcontainers DB)，驗收完整的 CRUD 流程與業務規則。
- 測試：執行 Cucumber Runner，確保所有 Scenario Passed。涵蓋正常創建、查詢、更新、刪除，以及重複 Email、找不到 ID 等錯誤情境。
