# Tasks 001 — Personal Profile CRUD

## T1 Domain Layer: Value Objects & Exceptions
- 說明：建立領域層的核心值物件與例外類別。包含 `ProfileId`、`Email`、`DisplayName` 三個 Value Object，以及對應的業務規則驗證邏輯。同時定義四個 Domain Exception (`InvalidEmailException`, `InvalidDisplayNameException`, `DuplicateEmailException`, `ProfileNotFoundException`)。這些類別將作為後續 Use Case 與 Entity 的基礎。
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`: 驗證有效 Email 可建立，無效格式拋出 `InvalidEmailException`。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java`: 驗證非空白 DisplayName 可建立，全空白或 null 拋出 `InvalidDisplayNameException`。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`: 驗證 UUID 型別的基本行為。

## T2 Application Layer: Ports, Commands & Services (Create & Get)
- 說明：實作 Create 與 Get 相關的 Inbound Port 介面、Command DTO、Outbound Port 介面及 Service 實作。
    - Inbound Ports: `CreateProfileUseCase`, `GetProfileUseCase`。
    - Outbound Ports: `SaveProfilePort`, `FindProfileByIdPort`。
    - Command/Query DTOs: `CreateProfileCommand`, `GetProfileQuery`。
    - Services: `CreateProfileService` (呼叫 Save), `GetProfileService` (呼叫 Find)。
    - *注意*：此階段僅定義介面與純 Java 邏輯，不依賴 Spring。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`: Mock `SaveProfilePort`，驗證成功保存時無例外，重複 Email 異常正確傳播。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`: Mock `FindProfileByIdPort`，驗證找到資料回傳 Profile，找不到拋出 `ProfileNotFoundException`。

## T3 Application Layer: Ports, Commands & Services (Update & Delete)
- 說明：實作 Update 與 Delete 相關的 Inbound Port 介面、Command DTO、Outbound Port 介面及 Service 實作。
    - Inbound Ports: `UpdateProfileUseCase`, `DeleteProfileUseCase`。
    - Outbound Ports: 延續 T2 的 `SaveProfilePort` 與新增 `DeleteProfilePort`（或合併於現有 Port，依 SOLID 原則建議分離或統一管理，此處為簡化依賴，若 Save 已涵蓋更新則可共用，但刪除需獨立操作）。根據 Plan，我們定義 `DeleteProfilePort`。
    - Command DTOs: `UpdateProfileCommand`, `DeleteProfileCommand`。
    - Services: `UpdateProfileService` (先 Find 再 Save), `DeleteProfileService` (先 Find 確認存在，再 Delete)。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`: Mock `Find...` 與 `Save...`，驗證成功更新、不存在拋出例外、重複 Email 異常傳播。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`: Mock `Find...` 與 `Delete...`，驗證成功刪除、不存在拋出 `ProfileNotFoundException`。

## T4 Persistence Adapter: Entity, Repository & Converter
- 說明：建立 JPA Entity、Spring Data Repository Interface 以及 Domain <-> Entity 的轉換器。
    - Entity: `ProfileEntity` (實作 `Persistable<UUID>`)。
    - Repository: `ProfileJpaRepository`。
    - Converter: `ProfileConverter` (靜態方法或類別)，負責將 `Domain.Profile` 轉為 `ProfileEntity` 及反向轉換。需處理 `isNew()` 狀態管理以符合 Prompt 陷阱提示。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntityTest.java`: 使用 `@DataJpaTest` + Testcontainers，驗證 Entity 映射正確性、唯一約束 (`unique=true`) 是否生效。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverterTest.java`: 純單元測試（不需 Spring），驗證 Domain Object 與 JPA Entity 之間的欄位對應與狀態轉移邏輯。

## T5 Bootstrap Configuration & Context Assembly
- 說明：在 Bootstrap 模組中組裝 Bean。修改現有的 `UseCaseConfiguration`，透過 `@Bean` 註解註冊所有 Application Layer 的 Service 與 Persistence Adapter 的 Port Implementation (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `DeleteProfilePort` 實作等)。確保依賴注入鏈完整。此任務不新增新的 Configuration 類別，僅擴充現有骨架。
- 測試：
    - 利用系統已提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證整個 Spring Context 能否成功啟動且無 BeanDefinitionOverrideException。若需額外驗證特定 Bean 是否存在，可在此階段補充簡單的斷言，但主要依靠既有測試確保上下文完整性。

## T6 Web Adapter: Controller, DTOs & Exception Handler
- 說明：建立 REST API 端點、Request/Response DTO 及全域異常處理器。
    - Controller: `ProfileRestController` (映射 `/api/profiles`)。
    - DTOs: `CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse`。
    - Exception Handler: `GlobalExceptionHandler`，將 Domain Exceptions 轉換為對應 HTTP 狀態碼 (400, 404, 409)。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestControllerTest.java`: 使用 `@WebMvcTest` + `@MockitoBean` Mock Use Cases。驗證 CRUD 各動作的 HTTP 方法、路徑、請求體解析、回應 JSON 結構及正確的状态碼（201, 200, 204, 400, 404, 409）。

## T7 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature File 與 Step Definitions，進行端到端的整合測試。
    - Feature: `personal-profile-crud.feature` (zh-TW)，涵蓋 Create, Get, Update, Delete 及其錯誤場景。
    - Steps: `PersonalProfileSteps`，使用 `TestRestTemplate` 發送真實 HTTP 請求至啟動的應用程式上下文。
- 測試：
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`: Gherkin 語法定義的行為規格。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/PersonalProfileSteps.java`: 執行步驟並斷言結果。確保所有 Scenario 通過，證明系統整體行為符合需求。
