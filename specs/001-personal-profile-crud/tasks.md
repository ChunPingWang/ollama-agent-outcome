# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層的核心值物件、實體與例外類別。包含 `ProfileId` (UUID wrapper)、`Email` (含 RFC5322 基本驗證)、`DisplayName` (長度限制)，以及主體 `Profile` (支援更新行為)。同時定義 `InvalidEmailException`、`DuplicateEmailException`、`ProfileNotFoundException` 三個領域例外。所有類別位於 `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/`。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/EmailTest.java` (驗證格式與不可變性)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayNameTest.java` (驗證長度與非空)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java` (驗證建構、更新 displayName/email 的業務規則與狀態變更)。

## T2 Application Ports Definition
- 說明：定義應用層的 Inbound Port (Use Case 介面) 與 Outbound Port (Repository 抽象)。Inbound: `CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`，參數使用 Command record (`CreateProfileCommand`, `UpdateProfileCommand`)。Outbound: `SaveProfilePort`, `LoadProfilePort`, `ExistsByEmailPort`, `DeleteProfileByIdPort`。這些介面僅依賴 domain，不含任何框架註解。路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/` 與 `port/out/`。
- 測試：此階段主要確保介面簽名正確且可編譯。由於是純介面，無獨立執行測試，但需確保後續 Use Case 實作能順利實現這些介面。可在 T3 中透過 Mock 這些介面來間接驗證其可用性。

## T3 Application Services Implementation
- 說明：實作所有 Use Case Service，負責協調領域邏輯與外部端口。
    - `CreateProfileService`: 檢查 email 唯一性 -> 建立 Profile -> Save。
    - `GetProfileService`: Load by ID -> return Optional。
    - `UpdateProfileService`: Load by ID (若不存在拋出 NotFoundException) -> 檢查新 email 是否與其他記錄衝突 (排除自身) -> 更新 Profile -> Save。
    - `DeleteProfileService`: Load by ID (若不存在拋出 NotFoundException) -> Delete。
    路徑為 `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/`。
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java` (Mock Ports, verify save called once, check DuplicateEmailException on conflict)。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java` (verify empty optional when not found, valid profile when exists)。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java` (verify load->update->save flow, handle Not Found and Duplicate Email scenarios)。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java` (verify delete called only if exists, throw exception otherwise)。

## T4 Persistence Adapter & Converter
- 說明：建立 JPA Entity、Spring Data Repository 以及 Port 的實作類別與轉換器。
    - `ProfileEntity`: 位於 `adapter.persistence`，使用 `String id` 作為主鍵 (UUID string)，實作 `Persistable<String>` 以手動管理 isNew 狀態 (避免 GeneratedValue 衝突，符合 Prompt 建議的最佳實踐處理 ID 生成策略的變通方案，或若採用 Server-generated UUID 則可使用 @GeneratedValue，但為確保唯一性約束在測試中立即生效且符合 Persistable 精神，此處採用 String ID + Persistable 模式，或在 Create 時由 Application 層傳入 ID 並標記 isNew=true)。*修正決策*：根據 Plan 最終決定，為了簡化且符合常見 REST API，我們將讓 Spring Data JPA 自動生成 UUID (`@GeneratedValue`)。但是，Prompt 強調 "Persistence 常見陷阱" 關於 Persistable。鑑於 Spec 未強制客戶端指定 ID，我們採用 **Server-generated UUID**。因此，Entity **不需要**實作 `Persistable`，而是使用標準的 `@GeneratedValue(strategy = GenerationType.UUID)`。**但是**，Prompt 說："Domain 若自行產生識別碼... Entity 不可同時使用 @GeneratedValue"。這裡 Domain 不產生 ID，是 Infrastructure/Application 層決定。為了安全起見並遵循 Prompt 對「可測試性」和「控制權」的重視，我們將採用 **Application Layer 生成 UUID** (或使用 DB default)，並在 Repository 中使用 `saveAndFlush`。
    - *再次修正以嚴格遵守 Prompt*：Prompt 明確指出 "Port 實作的儲存一律用 repository.saveAndFlush(entity)" 以及關於 Persistable 的警告。為避免複雜性並確保唯一性約束在測試中立即觸發（因為 save() 可能緩衝），我們將採用 **String id** 作為主鍵，並在 Create Use Case 中由 Application 層生成 UUID String 傳入。這樣就不需要 `@GeneratedValue`，從而避開衝突，並完全符合 Persistable 建議的精神（雖然不一定非要實作介面，但手動管理 isNew 狀態更清晰）。或者，更簡單地，直接使用 `UUID` 型別的主鍵，並讓 PostgreSQL/JPA 處理生成 (`GenerationType.IDENTITY` or `UUID`)。若發生樂觀鎖問題才用 Persistable。這裡沒有版本欄位，所以標準 `save()` 即可？不，Prompt 說唯一性約束要用 `saveAndFlush`。
    - *最終執行決策*：Entity 使用 `java.util.UUID` 型別的主鍵，並標註 `@GeneratedValue(strategy = GenerationType.UUID)`。這是最標準的做法。為了滿足 Prompt 對 `saveAndFlush` 的要求，我們在 Port 實作中使用 `saveAndFlush`。這不會導致 OptimisticLockingFailureException，因為我們沒有手動設定 ID 且沒有 @Version。
    - `ProfileJpaRepository`: 繼承 `CrudRepository<ProfileEntity, UUID>` (或 String，視 Entity 定義而定)。為簡化 Web Layer 轉換，假設 Entity ID 為 `String` (存儲為 VARCHAR) 或 `UUID`。鑑於 JSON 傳輸方便，Repository Port 接收 `String`。Entity 內部轉為 `UUID`? 不，直接讓 Entity ID 為 `String` 並在 DB 中作為 UUID 類型存儲可能會有型別匹配問題。最簡單的是 Entity ID 為 `String`，DB column 為 `VARCHAR` 或 `UUID` (PostgreSQL JDBC driver 通常能處理 String to UUID 的自動轉換如果配置正確，或者直接使用 String 存儲 UUID 字串)。為了絕對安全與符合 Spring Data JPA 慣例，我們將使用 `java.util.UUID` 作為 Entity ID。Web Controller 接收 String id，在 Adapter-Web 層解析？不，Adapter-Persistence 應該封裝細節。讓我們讓 Repository Port 接受 `String id`，在 Persistence Adapter 內部分配給 UUID。
    - *簡化方案*：Entity ID 為 `String`。DB Column 為 `VARCHAR(36)` 存儲 UUID 字串。這樣不需要任何轉換，且符合 "Domain 若自行產生識別碼... Entity 不可同時使用 @GeneratedValue" 的反面情況（這裡是外部指定/系統生成但由 App 層控制）。我們將在 Create Use Case 中生成 UUID String 並傳入 Profile。Profile 包含 `id: String`。Entity 映射該 String。這避免了 Persistable 的複雜性，同時滿足唯一性約束檢查的需求（透過 saveAndFlush）。
    - Ports Implementation: `SaveProfilePortImpl`, `LoadProfilePortImpl`, `ExistsByEmailPortImpl`, `DeleteProfileByIdPortImpl`。全部位於 `adapter.persistence`。
    - `ProfileConverter`: 負責 Domain `Profile` (含 String id) 與 JPA `ProfileEntity` 之間的雙向轉換。
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepositoryIntegrationTest.java` (使用 Testcontainers PostgreSQL, 驗證 CRUD, unique constraint violation via DataIntegrityViolationException)。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverterTest.java` (純 Java 單元測試, 驗證 domain <-> entity 屬性映射正確性)。

## T5 Bootstrap Configuration & Assembly
- 說明：在 `bootstrap` 模組中組裝 Bean。修改現有的 `UseCaseConfiguration`，以 `@Bean` 方法實例化所有 Use Case Service (`CreateProfileService`, `GetProfileService`, `UpdateProfileService`, `DeleteProfileService`)，並注入對應的 Port 實現類別（這些 Port 實現類應已在 adapter-persistence 中以 `@Repository` 或 `@Component` 標註，或由 Spring Boot 自動掃描註冊）。確保依賴方向正確：Adapter-Persistence -> Application Ports -> Application Services -> Bootstrap Config。
- 測試：利用骨架提供的 `bootstrap.ApplicationContextTest` 驗證整個 Spring Context 能否成功啟動且無 BeanDefinitionOverrideException。不需新增新的 Context 測試。

## T6 Web Adapter & Exception Handling
- 說明：建立 REST Controller、DTOs 與全局異常處理器。
    - DTOs: `CreateProfileRequest`, `UpdateProfileRequest`, `ProfileResponse` (record)，位於 `adapter.web.dto`? **不**，根據規則 "domain 與 adapter 模組不得使用子套件"，所以 DTOs 必須直接放在 `com.github.chunpingwang.ollamaagentoutcome.adapter.web` 下，例如 `CreateProfileRequest.java`。
    - `ProfileRestController`: 處理 `/api/profiles` 路由，呼叫 Inbound Ports。POST 回 201+Location, GET/PUT 回 200, DELETE 回 204。
    - `GlobalExceptionHandler`: 將 Domain Exceptions 映射為 HTTP 狀態碼 (400, 404, 409)。僅處理特定例外型別，不攔截通用 Exception。
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerUnitTest.java` (`@WebMvcTest`, Mock UseCase Ports, 驗證 HTTP 狀態碼、JSON 序列化/反序列化、錯誤訊息格式)。

## T7 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature File 與 Step Definitions，進行端到端驗收。
    - Feature: `bootstrap/src/test/resources/features/personal-profile-crud.feature` (繁體中文 Gherkin)。
    - Steps: `PersonalProfileSteps.java` (位於 `bdd` 套件)，使用 `TestRestTemplate` 發送實際 HTTP 請求，並斷言回應內容。確保包含成功路徑與各種異常情境（重複 Email、找不到 ID 等）。
- 測試：執行 Cucumber 測試套件，確認所有 Scenario 通過，涵蓋 CRUD 完整生命週期及邊界條件。
