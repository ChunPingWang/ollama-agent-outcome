# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本格式驗證)、`DisplayName` (長度限制)，以及 `Profile` 聚合根（封裝業務邏輯如更新顯示名稱）。同時定義 `InvalidEmailException`、`DuplicateEmailException`、`ProfileNotFoundException` 等領域例外。所有類別位於 `domain` 套件，無子目錄。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證格式與長度)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java` (驗證長度)、`src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證建構不變條件與行為)。使用純 JUnit + AssertJ。

## T2 Application Ports Definition
- 說明：在 `application.port.in` 定義四個 Use Case 介面 (`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`)；在 `application.port.out` 定義四個 Port 介面 (`SaveProfilePort`, `LoadProfilePort`, `ExistsByEmailPort`, `DeleteProfilePort`)。建立對應的 Request/Result Record DTOs 於 application 層或 domain 層（此處為簡化依賴，DTOs 可放在 application port in/out 相關位置或直接由 use case 實作類內部處理，但根據規範，inbound port 方法簽名需明確，故將 Request/Response records 放在 `application.port.in` 或作為獨立 record 放在 `domain` 若屬領域概念。鑑於這些是傳輸物件，建議放在 `application.port.in` 套件下作為介面的伴隨類型，或直接在 Service 實作中處理轉換。為符合「port 小而專一」且避免循環，我們將 Request/Response Records 直接定義在 `application.port.in` 包內的 UseCase 介面旁，或使用獨立的 simple records）。*修正策略*：為了保持 clean architecture，Request/Response 通常屬於 Application Layer。我們將在 `application/port/in` 和 `application/port/out` 中定義必要的 Record 類別，或者更乾淨的方式是：Use Case Interface 只接受 Domain Entity 或簡單參數？不，Web Adapter 需要 DTO。因此，Application Port In 應該接收 Web 層的 DTO 或是中間格式。這裡採用標準做法：Port In 介面接收簡單的 Value Objects 或 Domain Entities，而 Web Adapter 負責將 HTTP DTO 轉為 Port Input。但 Plan 中提到 `CreateProfileRequest: record { Email, DisplayName }`，這已經是 Domain 級別的輸入了。所以我们将 `CreateProfileRequest`, `UpdateProfileRequest`, `GetProfileResult` (即 Profile) 等記錄類放在 `application.port.in` 包內，或者直接讓 UseCase 方法簽名使用這些 Record。
- 測試：無獨立邏輯可測，僅編譯檢查。此任務主要確保介面定義正確，供後續實作引用。

## T3 Application Services Implementation
- 說明：實作四個 Use Case Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`) 於 `application.usecase` 套件。每個 Service 依賴對應的 Outbound Ports。`CreateProfileService` 需調用 `ExistsByEmailPort` 檢查重複並拋出 `DuplicateEmailException`；`Update/Delete` 服務需先調用 `LoadProfilePort` 確認存在性再執行操作。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`.../GetProfileServiceTest.java`、`.../UpdateProfileServiceTest.java`、`.../DeleteProfileServiceTest.java`。使用 Mockito 模擬 Outbound Ports，驗證業務流程與例外拋出時機（如重複 Email、找不到 ID）。

## T4 Persistence Adapter (Entity & Repository)
- 說明：建立 JPA Entity `ProfileEntity` 位於 `adapter.persistence` 套件，實現 `Persistable<UUID>` 以支援 Domain 產生的 UUID。建立 Spring Data Repository `ProfileRepository`。此階段不實作 Port，僅完成數據映射層基礎設施。注意 `@GeneratedValue` 不可用於已手動生成 ID 的情況，需配合 `isNew()` 邏輯。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java` (若需測實體行為)、主要依賴後續的 Port 測試來驗證持久化能力。此任務確保 Entity 定義正確且可被掃描。

## T5 Persistence Adapter (Port Implementations)
- 說明：在 `adapter.persistence` 套件實作四個 Port (`JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaExistsByEmailPort`, `JpaDeleteProfilePort`)。負責 Domain Profile 與 JPA Entity 之間的轉換。`JpaSaveProfilePort` 使用 `saveAndFlush` 並捕獲 `DataIntegrityViolationException` 轉為 `DuplicateEmailException`。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`、`.../JpaLoadProfilePortTest.java`、`.../JpaExistsByEmailPortTest.java`、`.../JpaDeleteProfilePortTest.java`。使用 `@DataJpaTest` + Testcontainers PostgreSQL，注入真實 Repository 進行端到端（相對於 DB）的 Port 邏輯測試，驗證唯一性約束觸發與數據轉換正確性。

## T6 Bootstrap Configuration & Context Assembly
- 說明：修改 `bootstrap` 模組中的 `UseCaseConfiguration`。以 `@Bean` 方式註冊所有 Application Services 和 Persistence Ports。確保依賴注入鏈完整（Service -> Port -> Repository）。不新增其他 Configuration 類別或 Context 測試。
- 測試：利用骨架提供的 `ApplicationContextTest` (位於 bootstrap) 啟動 Spring Context，驗證所有 Bean 能否正常裝載無錯誤。若需額外驗證，可寫一個簡單的 `IntegrationContextTest` 僅檢查 Bean 存在性，但建議優先依賴現有骨架測試。此任務是 Web Adapter 能啟動的前提。

## T7 Web Adapter (Controller, DTOs & Exception Handler)
- 說明：在 `adapter.web` 套件建立 `ProfileController` (`@RestController`)，映射 `/profiles` CRUD 路由。定義 Request/Response Dtos (`CreateProfileRequestDto`, etc.) 並使用 Jakarta Validation。實作 `GlobalExceptionHandler` (`@RestControllerAdvice`) 處理領域例外與驗證異常，返回正確的 HTTP Status Code (201, 400, 404, 409)。
- 測試：`src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`。使用 `@WebMvcTest` + `@MockitoBean` 模擬 Application Use Cases，驗證 URL 映射、參數綁定、HTTP 狀態碼及 Header（如 Location）。

## T8 Cucumber BDD Acceptance Tests
- 說明：在 `bootstrap/src/test/resources/features` 創建 `personal-profile-crud.feature` (zh-TW)，描述完整的使用者旅程。在 `com.github.chunpingwang.ollamaagentoutcome.bdd` 包中實現步驟定義，使用 `TestRestTemplate` 呼叫實際 API。確保測試能通過整個堆疊（DB, Web, App, Domain）。
- 測試：執行 Cucumber Runner，驗證所有 Scenario 通過。這是最終的驗收任務，確認系統整體行為符合 Spec。
