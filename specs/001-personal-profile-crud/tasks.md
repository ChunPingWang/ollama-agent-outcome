# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心模型與例外類別。包含 Value Objects (`ProfileId`, `Email`, `DisplayName`)、Domain Entity (`Profile`) 以及業務規則相關的例外 (`DuplicateEmailException`, `InvalidEmailFormatException`, `ProfileNotFoundException`)。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/`
    - `ProfileIdTest`: 驗證 UUID 格式檢查與不可變性。
    - `EmailTest`: 驗證 Email 格式正規表示式檢查（有效/無效輸入）。
    - `DisplayNameTest`: 驗證空白字串處理與 null 安全性。
    - `ProfileTest`: 驗證靜態工廠方法 `create()` 的建構邏輯，以及 `update()` 對狀態變更的影響。

## T2 Application Ports (Inbound & Outbound)
- 說明：定義應用層的介面（Ports）。包括 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort`)。這些介面僅定義契約，不含實作。位置在 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `port/out/`。
- 測試：此階段為純介面定義，無需撰寫執行測試，但需確保編譯通過且符合命名規範（in 結尾 UseCase, out 結尾 Port）。

## T3 Application Services Implementation
- 說明：實作 Use Case Service 類別，協調 Domain Entity 與 Outbound Ports。包含 `CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`。服務層負責呼叫 Port、轉換例外、並維持業務流程順序。位置在 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`
    - `CreateProfileServiceTest`: Mock `SaveProfilePort`，驗證成功時呼叫 save 一次；模擬 port 拋出 `DuplicateEmailException` 時向上傳播。
    - `GetProfileServiceTest`: Mock `LoadProfilePort`，驗證找到資料回傳 Optional；未找到時拋出 `ProfileNotFoundException`。
    - `UpdateProfileServiceTest`: Mock Load/Save ports，驗證先 load 再 update domain state 最後 save 的流程；處理 not found 情況。
    - `DeleteProfileServiceTest`: Mock Load/Delete ports，驗證存在時呼叫 delete；不存在時拋出例外。

## T4 Persistence Adapter (Entity, Repository & Port Impl)
- 說明：建立 JPA Entity (`ProfileEntity`)、Spring Data Repository (`ProfileRepository`)、Converter (`ProfileConverter`) 以及 Outbound Ports 的實作類別 (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort`)。需處理 UUID 主鍵與 `Persistable` 介面以確保正確的新增/更新行為。位置在 `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`（直接放在套件根目錄，不使用子目錄）。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/`
    - `ProfileEntityTest`: 使用 `@DataJpaTest` + Testcontainers，驗證 Entity mapping、Unique Constraint on email、以及 `isNew()` 狀態轉換邏輯。
    - `JpaSaveProfilePortTest`: 測試儲存新資料成功、更新現有資料、以及重複 Email 觸發 `DuplicateEmailException`。
    - `JpaLoadProfilePortTest`: 測試 findById 成功與失敗回傳 empty Optional。
    - `JpaDeleteProfilePortTest`: 測試 deleteById 成功執行。

## T5 Bootstrap Configuration & Assembly
- 說明：修改 `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java`，將 Application Services (`CreateProfileService`, etc.) 註冊為 Spring Beans。同時確保 Persistence Adapter 的 Port Implementations 被掃描或註冊（若未標註 `@Repository`/`@Component` 則需在此宣告，但依慣例建議在 adapter 模組加註解由 Component Scan 發現，此任務重點在於 Use Case Bean 的組裝）。**不新增新的 Context 啟動測試**。
- 測試：依賴骨架提供的 `ApplicationContextTest` (位於 `bootstrap/src/test/java`) 來驗證整個 Spring Context 能否正常啟動且無 BeanDefinitionOverrideException。

## T6 Web Adapter (Controller, DTOs & Exception Handler)
- 說明：建立 REST Controller (`ProfileController`)、Request/Response DTOs (`CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResponse`) 以及全域異常處理器 (`GlobalExceptionHandler`)。實現 HTTP 方法對應到 Use Case Ports，並處理 Jakarta Validation 錯誤與 Domain Exceptions 轉譯為適當的 HTTP Status Code (201, 200, 204, 400, 404, 409)。位置在 `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/`
    - `ProfileControllerTest`: 使用 `@WebMvcTest` + MockMvc。Mock 所有 Inbound Port。
        - 驗證 POST /api/profiles: 201 Created + Location Header; 400 on validation fail; 409 on duplicate email.
        - 驗證 GET /api/profiles/{id}: 200 OK with JSON body; 404 if not found.
        - 驗證 PUT /api/profiles/{id}: 200 OK; 404 if not found.
        - 驗證 DELETE /api/profiles/{id}: 204 No Content; 404 if not found.
    - `GlobalExceptionHandlerTest`: 驗證特定例外映射到正確的狀態碼與錯誤訊息格式（若需要詳細檢查 error body）。

## T7 BDD Acceptance Tests (Cucumber)
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與步驟定義類別 (`PersonalProfileSteps.java`)，位於 bootstrap 模組的 bdd 套件與 resources/features。使用繁體中文 Gherkin，透過 HTTP 呼叫實際 API 來驗收 CRUD 功能。
- 測試：執行 Cucumber Runner。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileStepsTest` (或依賴 Cucumber JUnit Platform Engine)。
    - 驗證 Scenario: Create Profile, Get Created Profile, Update Profile, Delete Profile, Verify Deletion (404), Handle Duplicate Email (409).
