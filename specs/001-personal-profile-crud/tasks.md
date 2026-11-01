# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Value Objects
- 說明：建立領域層的核心值物件與實體，包含 ID、Email、DisplayName 的不可變結構與驗證邏輯，以及相關的 Domain Exception。
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailFormatException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`：驗證 RFC5322 格式、空白字串拒絕、null 處理。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java`：驗證 null、空字串、純空白字串的處理邏輯。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證 UUID 不可為 null。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileFactoryTest.java`：驗證 Profile 建立時的基本不變條件（如 Email 格式檢查）。

## T2 Application Ports & Use Cases (Create & Get)
- 說明：定義 Inbound Port 介面與 Service 實作，並完成 Outbound Port 介面的定義。優先實現「建立」與「查詢」的應用邏輯。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：模擬 SaveProfilePort，驗證成功呼叫、重複 Email 異常傳遞。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：模擬 LoadProfilePort，驗證存在時回傳 Optional.of、不存在時回傳 Optional.empty。

## T3 Application Use Cases (Update & Delete)
- 說明：完成剩餘的 Inbound Port 與 Service 實作（更新與刪除），確保業務規則（如先載入再修改）正確執行。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證找到資料後呼叫 save、找不到資料拋出 ProfileNotFoundException。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證找到資料後呼叫 deleteById、找不到資料拋出 ProfileNotFoundException。

## T4 Persistence Adapter (JPA Entity & Repository)
- 說明：建立 JPA Entity，實作 Persistable<UUID> 以處理 isNew 狀態，並定義 Spring Data Repository。
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepositoryTest.java`：使用 Testcontainers PostgreSQL，驗證基本 CRUD、唯一性約束（DuplicateKeyException/DataIntegrityViolationException）。

## T5 Persistence Port Implementations
- 說明：實作 Application 層定義的 Outbound Ports，負責 Domain Object 與 JPA Entity 之間的轉換及持久化邏輯。
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java`
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`：驗證載入資料並正確轉換為 Domain Profile。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證新增（isNew=true）與更新（merge）邏輯，以及 DataIntegrityViolationException 轉為 DuplicateEmailException。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`：驗證刪除操作。

## T6 Bootstrap Configuration & Assembly
- 說明：在 bootstrap 模組中組裝 Use Case Bean。修改現有的 `UseCaseConfiguration` 以注入 Ports 實作並宣告所有 Use Case Beans。確保 Spring Context 能正常啟動。
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` (修改)
- 測試：
    - 使用骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 驗證 Context 啟動無誤（不需新增新測試類別，只需確保配置正確）。

## T7 Web Adapter (Controller, DTOs & Exception Handling)
- 說明：建立 REST Controller、Request/Response DTOs 與全域異常處理器。將 HTTP 請求映射至 Application Inbound Ports。
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/dto/ProfileResponseDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandler.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest` + Mock Use Cases，驗證各端點狀態碼（201, 400, 404, 409）與 JSON 結構。

## T8 BDD/Cucumber Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與步驟定義，透過 HTTP 呼叫實際 API 進行端到端驗收。
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java` (或放在 test 套件依骨架而定，通常 steps 在 main 以便於執行，但系統提示說 bdd 套件在 bootstrap，且測試用 TestRestTemplate，故步驟定義需能訪問 TestRestTemplate) -> *修正*：根據系統提示 "Cucumber 步驟定義放在 bootstrap 的 bdd 套件"，且需要 HTTP 呼叫，通常步驟定義類別會注入 TestRestTemplate。若 TestRestTemplate 僅在測試環境可用，步驟定義應位於 `src/test/java` 還是 `src/main/java`？系統提示表格顯示 `bdd` 套件屬於 `bootstrap` 模組，未明確區分 src/main 或 src/test。但 Cucumber JUnit Platform Engine 通常在測試階段運行。為了讓 `TestRestTemplate` 可用（它依賴 Web Server），步驟定義類別最好放在 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/` 並使用 `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)` 啟動完整 Context。然而，系統提示提到 "bootstrap...組裝use case bean"，且 "adapter-web...只能呼叫 application"。
    - 讓我們調整 T8 的說明以符合架構：步驟定義類別將放置在 `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`，因為它需要與資料庫互動（透過 TestContainer）和 HTTP 端點。這需要 `UseCaseConfiguration` 已正確組裝 Bean。
- 測試：
    - 執行 Cucumber Runner (`bootstrap/src/test/java/.../CucumberRunner.java` 由骨架提供) 驗證所有 Scenario 通過。
