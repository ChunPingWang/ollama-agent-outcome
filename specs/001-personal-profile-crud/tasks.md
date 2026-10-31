# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Value Objects
- 說明：建立領域層核心模型與值物件，包含 ID、Email、DisplayName 的不可變結構與驗證邏輯，以及自訂例外類別。
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` (record, UUID wrapper)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` (record, RFC5322 validation via regex)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` (record, null/blank handling)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` (class, state management with value objects)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailFormatException.java`
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java`: 驗證合法/非法 Email 格式、非空白檢查。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java`: 驗證 null、空字串、純空白字串的處理。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`: 驗證 UUID 不可為 null。

## T2 Application Ports & Use Cases (Interfaces + Implementations)
- 說明：定義應用層的 Inbound Port (UseCase) 與 Outbound Port (Port) 介面，並實作具體的 Service 類別。此階段不包含持久化細節，僅定義契約與業務流程邏輯（如呼叫順序）。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` (生成 UUID, 檢查 Email 唯一性, 保存)
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` (檢查存在性, 更新欄位, 再次檢查新 Email 唯一性)
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`: Mock Outbound Ports，驗證建立流程、Email 重複時拋出例外。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`: 驗證找到與找不到資料的行為。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`: 驗證更新邏輯與 Email 變更時的唯一性檢查。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`: 驗證刪除前的存在性檢查。

## T3 Persistence Adapter & Port Implementations
- 說明：實作 JPA Entity、Repository 以及 Application Layer 的 Outbound Port 介面。處理 Domain Object 到 JPA Entity 的轉換，並確保 UUID 主鍵在 INSERT/UPDATE 行為正確（使用 Persistable）。
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` (implements Persistable<UUID>)
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/SaveProfilePortImpl.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/LoadProfilePortImpl.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ExistsByEmailPortImpl.java`
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java`: 驗證 Entity 映射與 Persistable 邏輯。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/SaveProfilePortImplTest.java`: 使用 Testcontainers PostgreSQL，驗證 saveAndFlush、重複 Email 拋出 DataIntegrityViolationException 並轉換為 Domain Exception。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/LoadProfilePortImplTest.java`: 驗證 findById 轉換正確性。

## T4 Bootstrap Configuration & Context Assembly
- 說明：在 Bootstrap 模組中組裝 Bean。將 Persistence Adapter 的 Port 實作注入到 Application Layer 的 Service，並將 Service 註冊為 Spring Bean。修改骨架提供的 `UseCaseConfiguration`。
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` (新增 @Bean 方法)
- 測試：
    - 無需新增測試類別（依賴骨架提供的 `ApplicationContextTest` 確保 Context 啟動成功）。確認所有 Use Case Bean 可被解析。

## T5 Web Adapter (Controller, DTOs & Error Handling)
- 說明：建立 REST Controller、Request/Response DTO 以及全域異常處理器。處理 HTTP 協議細節與 JSON 序列化。
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponseDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestController.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java`
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileRestControllerTest.java`: 使用 @WebMvcTest，Mock Inbound Ports，驗證 POST(201+Location)、GET(200)、PUT(200)、DELETE(204) 狀態碼與回應內容。
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandlerTest.java`: 驗證 Domain Exception 對應的 HTTP 狀態碼 (409, 404, 400)。

## T6 BDD Acceptance Tests (Cucumber)
- 說明：撰寫 Cucumber Feature 檔（繁體中文 Gherkin）與步驟定義，透過 TestRestTemplate 對真實 API 進行端到端測試。
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`
- 測試：
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` (已存在，確保整合測試可運行)
    - 執行 Cucumber Runner，驗證所有 Given/When/Then 場景通過（包含正常流程、錯誤處理如重複 Email）。
