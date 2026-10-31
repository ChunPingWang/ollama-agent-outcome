# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於基礎設施，並支援 TDD/BDD 開發流程。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

- **ProfileId** (`record`)
    - 內容：`UUID value`
    - 不變條件：不可為 null。
    - 角色：作為 Domain Entity 的唯一識別碼，也用於 JPA Entity 的主鍵映射。

- **Email** (`record`)
    - 內容：`String address`
    - 不變條件：符合 RFC 5322 基本格式（非空、包含 @、有有效域名）。在 Web Adapter 層進行初步格式驗證，若無效則拋出 `InvalidEmailException`；在 Domain 層可進一步強化驗證或僅作為資料容器。
    - 角色：代表用戶聯絡資訊的值物件。

- **Name** (`record`)
    - 內容：`String text`
    - 不變條件：非空、長度介於 1~50 字元之間。
    - 角色：代表用戶姓名的值物件。

- **Profile** (`class`, immutable state via builder or constructor, but mutable for updates in persistence context if needed, though we prefer immutable domain objects updated by factory methods)
    - *修正策略*：為了簡化持久化更新邏輯並遵循「先 findById 再更新」的陷阱提示，Domain Entity 將採用**不可變狀態 + Factory/Builder 模式**來產生新實例，或者提供明確的 `update(...)` 方法回傳新實例。但在 JPA 情境下，通常 Entity 需要被管理。
    - *最終決定*：為符合 System Prompt 中關於 `Persistable<UUID>` 和 `isNew()` 的要求，以及避免樂觀鎖衝突，我們將 `Profile` 設計為一個包含業務規則的領域模型。在 Application Service 層處理轉換。
    - 屬性：
        - `id: ProfileId` (只讀)
        - `name: Name`
        - `email: Email`
        - `createdAt: Instant` (建立時間，由 Domain 或 Adapter 設定，此處假設由系統生成)
    - 不變條件：
        - `email` 在全域必須唯一（這通常是資料庫約束，但 Domain Exception 需反映此錯誤）。
        - `name` 與 `email` 不得為 null 或空白。

### Domain Exceptions

- **ProfileNotFoundException**: 當查詢指定 ID 的個人資料不存在時拋出。
- **DuplicateEmailException**: 當嘗試建立或更新資料時，發現電子郵件已存在於其他記錄中時拋出。
- **InvalidEmailException**: 當提供的電子郵件格式不符合 RFC 規範時拋出。
- **InvalidNameException**: 當姓名無效時拋出。

## 2. Use Cases & Ports

### Inbound Ports (Use Case Interfaces)

1.  **CreateProfileUseCase**
    ```java
    void execute(CreateProfileCommand command);
    // Command class: record CreateProfileCommand(Name name, Email email) {}
    ```

2.  **GetProfileUseCase**
    ```java
    ProfileResult execute(GetProfileQuery query);
    // Query class: record GetProfileQuery(ProfileId id) {}
    // Result class: record ProfileResult(ProfileId id, Name name, Email email) {}
    ```

3.  **UpdateProfileUseCase**
    ```java
    void execute(UpdateProfileCommand command);
    // Command class: record UpdateProfileCommand(ProfileId id, Name name, Email email) {}
    ```

4.  **DeleteProfileUseCase**
    ```java
    void execute(DeleteProfileCommand command);
    // Command class: record DeleteProfileCommand(ProfileId id) {}
    ```

### Outbound Ports (Repository/Storage Interfaces)

*注意：為符合 SOLID 介面分離原則，我們將操作拆分為細粒度的 Port。*

1.  **LoadProfilePort**
    ```java
    Optional<Profile> findById(ProfileId id);
    boolean existsByEmail(Email email); // 用於唯一性檢查
    ```

2.  **SaveProfilePort**
    ```java
    <T extends Profile> T save(T profile);
    void deleteById(ProfileId id);
    ```

### Use Case Implementations (Services)

- `CreateProfileService`: 實現 `CreateProfileUseCase`。驗證輸入 → 檢查電子郵件重複性 (`existsByEmail`) → 建立新 `Profile` 實例 → 呼叫 `save()`。
- `GetProfileService`: 實現 `GetProfileUseCase`。呼叫 `findById()` → 若不存在拋出 `ProfileNotFoundException` → 回傳結果 DTO。
- `UpdateProfileService`: 實現 `UpdateProfileUseCase`。先 `findById()` 確認存在 → 檢查新電子郵件是否與其他記錄衝突（排除自身）→ 更新實體狀態或建立新實例並保存 → 呼叫 `save()`。
- `DeleteProfileService`: 實現 `DeleteProfileUseCase`。先 `findById()` 確認存在（可選，但建議在刪除前檢查以提供明確錯誤訊息，或直接依賴 DB 約束/異常處理）→ 呼叫 `deleteById()`。*根據 spec，刪除不存在的 ID 應返回 404，因此需先查詢是否存在。*

## 3. Web Adapter

### Controller: `ProfileController`

- **路徑**: `/api/profiles`
- **方法與映射**:
    - `POST /{id}` (錯，是 `/profiles/{id}` for GET/PUT/DELETE, POST is `/profiles`)
        - `@PostMapping("/")` or `@PostMapping("")` -> `createProfile(@Valid @RequestBody CreateProfileRequest request)`
            - 回應：201 Created + Location Header。
        - `@GetMapping("/{id}")` -> `getProfile(@PathVariable("id") UUID id)`
            - 回應：200 OK with JSON body.
        - `@PutMapping("/{id}")` -> `updateProfile(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)`
            - 回應：200 OK with JSON body.
        - `@DeleteMapping("/{id}")` -> `deleteProfile(@PathVariable("id") UUID id)`
            - 回應：204 No Content.

### DTOs (Data Transfer Objects)

- **CreateProfileRequest** (`record`)
    - `String name`
    - `String email`
    - Validation: `@NotBlank`, `@Email`.

- **UpdateProfileRequest** (`record`)
    - `String name`
    - `String email`
    - Validation: `@NotBlank`, `@Email`.

- **ProfileResponse** (`record`)
    - `UUID id`
    - `String name`
    - `String email`

### Error Handling: `GlobalExceptionHandler`

- `InvalidInputException` (由 Web Adapter 驗證失敗或 Domain Exception 轉換而來): 400 Bad Request。
- `DuplicateEmailException`: 409 Conflict。
- `ProfileNotFoundException`: 404 Not Found。
- *注意*: 不使用 `@ExceptionHandler(Exception.class)`，以免攔截到 Spring Framework 的 404 (NoResourceFoundException)。

## 4. Persistence Adapter

### JPA Entity: `ProfileEntity`

- 套件: `com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`
- 實現 `Persistable<UUID>` 以處理 ID 生成與 isNew 狀態。
- 欄位：
    - `@Id UUID id`
    - `@Column(unique = true) String email`
    - `String name`
    - `Instant createdAt`
    - `@Transient boolean isNew = true;`
    - `isNew()` method returns `isNew`.
    - `@PostLoad`, `@PrePersist` 用於管理 `isNew` 旗標（若使用自動生成的 ID，這裡我們假設客戶端不傳 ID，而是由系統生成？Spec 說 "識別碼...通常採用 UUID 格式，不可由使用者自訂"。這意味著 ID 應在 Domain 或 Application 層生成，然後傳遞給 Port。因此 Entity 不需要 `@GeneratedValue(strategy=IDENTITY)` 或 `AUTO`，而是直接設定 UUID。為了符合 `Persistable` 模式，我們在建立新 Profile 時設置 `id` 為新生成的 UUID，並讓 `isNew=true`）。

*修正*: System Prompt 提到「Domain 若自行產生識別碼... JPA Entity **不可**同時使用 `@GeneratedValue`」。我們將讓 `CreateProfileService` 生成新的 `UUID` 作為 `ProfileId`，並在轉換為 Entity 時將其賦值給 `entity.id`，且保持 `isNew=true`。

### Repository: `JpaProfileRepository` (Spring Data)

- 介面：`extends JpaRepository<ProfileEntity, UUID>`
- 額外方法：`boolean existsByEmail(String email);`

### Port Implementation: `JpaLoadProfilePort` & `JpaSaveProfilePort`

- **JpaLoadProfilePort**:
    - 注入 `JpaProfileRepository`。
    - `findById`: 查詢 Entity，若存在則透過 Converter 轉為 Domain `Profile`，否則回傳 `Optional.empty()`。
    - `existsByEmail`: 呼叫 repository 的自訂方法。

- **JpaSaveProfilePort**:
    - 注入 `JpaProfileRepository`。
    - `save`: 
        - 將 Domain `Profile` 轉為 `ProfileEntity`。
        - 呼叫 `repository.saveAndFlush(entity)`。*關鍵*: 使用 `saveAndFlush` 以確保在測試事務中唯一性約束立即生效並拋出異常（如果重複）。
        - 捕捉 `DataIntegrityViolationException`，檢查是否為 Email 衝突，若是則轉換為 `DuplicateEmailException`，否則重新拋出或包裝為通用持久化例外。
    - `deleteById`:
        - 先檢查是否存在？不，直接調用 `repository.deleteById(id)`。但根據 Use Case 邏輯，DeleteUseCase 應該先確認存在。如果在 Service 層已經確認存在，這裡可以直接刪除。若在 Repository 層刪除後發現沒影響行數，可考慮拋出例外，但通常由 Service 層控制流程更清晰。我們將讓 Service 層負責「先查後刪」以滿足 404 需求。

### Converter: `ProfileConverter` (Static Utility or Component)

- `toDomain(ProfileEntity entity) -> Profile`
- `toEntity(Profile profile, UUID newIdIfNew)` -> *注意*: 由於 ID 可能在 Application 層生成，Converter 可能需要接受目標 ID，或者 Entity 的 ID 與 Domain 的 ID 同步。簡單做法：Application Service 決定新 ID，傳入 Converter 或直接設定到 Entity。為了簡潔，我們可以在 `JpaSavePort.save()` 中處理轉換，並允許传入一個 optional 的新 ID（如果是新建）。或者更單純：Domain Object 包含 ID，Entity 也包含 ID。當建立時，App Layer 創建帶有新生成 ID 的 Domain Object，然後轉為 Entity。

## 5. Bootstrap

### Configuration: `UseCaseConfiguration`

- `@Configuration`
- `@Bean CreateProfileService`: 依賴 `LoadProfilePort`, `SaveProfilePort`.
- `@Bean GetProfileService`: 依賴 `LoadProfilePort`.
- `@Bean UpdateProfileService`: 依賴 `LoadProfilePort`, `SaveProfilePort`.
- `@Bean DeleteProfileService`: 依賴 `LoadProfilePort`, `SaveProfilePort`.
- `@Bean JpaLoadProfilePort`: 依賴 `JpaProfileRepository`.
- `@Bean JpaSaveProfilePort`: 依賴 `JpaProfileRepository`.
- *注意*: Controller, Repository, Entity 由 Spring Boot Auto-configuration 或 Component Scan 自動偵測/註冊，不需要在此手動定義 Bean，除非有特殊的建構子參數需求。但根據規則，Use Case Beans **只能**在 `UseCaseConfiguration` 以 `@Bean` 定義一次。

### Cucumber Step Definitions (in `bdd` package)

- `ProfileSteps.java`: 
    - 使用 `TestRestTemplate` 呼叫 API。
    - 步驟：
        - `當我發送 POST 請求至 "/api/profiles", 內容包含: <json>` -> 執行 POST，儲存回應狀態碼與主體。
        - `那麼回應的狀態碼應該是 {int}` -> Assert 狀態碼。
        - `並且回應標頭 "Location" 應指向...` -> Assert Header。
        - `假設系統中已存在電子郵件為 "{string}" 的個人資料` -> 先透過 API 建立該資料（或在測試 setup 中處理）。*建議*: BDD 場景中的「假設」通常意味著前置條件。為了保持場景獨立性，每個 Scenario 應該自包含。如果一個場景依賴另一個場景建立的數據，這違反了 BDD 的最佳實踐（Scenario 應獨立）。然而，Spec 中的場景是獨立的描述。但在實際 BDD 實現中，我們可能需要一個 `Given` 步驟來初始化資料庫狀態，或者讓每個 Scenario 自己建立所需的初始數據。鑑於這是 CRUD，最乾淨的方式是在每個需要「假設存在」的場景中，先執行一次 Create 操作作為 Setup，或者提供一個專門的 Helper Step。這裡我們將採用：**在 Step Definition 中，對於「假設...已存在」的情況，直接呼叫內部方法或 API 來確保該記錄存在**，因為 Cucumber 場景之間不共享狀態（除非使用 World/Context，但 Spring Boot Test Context 會保留 DB 內容嗎？Testcontainers 在每次測試類運行後可能會重置，取決於配置。 safest bet: Each scenario is self-contained. If a scenario says "Assume X exists", the step definition should create X if it doesn't exist, or we assume the test framework handles cleanup between scenarios. Given the complexity, I will implement steps that perform the action described in 'When' and verify in 'Then'. For 'Given', I will add logic to ensure preconditions are met by calling the service/repository directly within the step context using injected beans, OR simply rely on the fact that previous tests might have left data? No, that's fragile. 
    - *修正策略*: 為了簡化 BDD 步驟定義並保持獨立性，我們將在 `ProfileSteps` 中注入 `JpaSavePort` 和 `LoadProfilePort` (透過 Application Context) 或直接使用 `RestTemplate` 來建立前置數據。考慮到這是 Web Adapter 的集成測試，最佳實踐是使用 HTTP 調用來設置前置條件，或者在每個 Scenario 開頭清理資料庫。由於 Spec 中的「假設」是邏輯上的，我們將實作一個私有方法或 Helper Step，讓開發者在寫 Feature 時可以明確地「先建立一條記錄」。
    - 具體實現：對於 `當我發送...` 等標準步驟，直接使用 `TestRestTemplate`。對於 `假設系統中已存在...`，將實作為一個獨立的步驟 `假設我已建立電子郵件為 "{string}" 的個人資料: <json>`，這樣場景更清晰。

## 6. 測試策略

### Unit Tests (Domain & Application)

- **Package**: `domain.test`, `application.test`
- **工具**: JUnit Jupiter, AssertJ, Mockito.
- **重點**:
    - `CreateProfileServiceTest`: 驗證輸入合法性、檢查重複 Email (`existsByEmail` mock)、呼叫 `save()`。
    - `GetProfileServiceTest`: 驗證找到資料回傳結果、找不到拋出例外。
    - `UpdateProfileServiceTest`: 驗證更新邏輯、唯一性檢查（排除自身）、404 處理。
    - `DeleteProfileServiceTest`: 驗證刪除前檢查是否存在、調用 `deleteById()`。
    - Domain Value Object/Entity 不變條件測試。

### Integration Tests (Web Adapter)

- **Package**: `adapter.web.test`
- **註解**: `@WebMvcTest(ProfileController.class)`, `@AutoConfigureMockMvc`.
- **依賴注入**: Mock Inbound Ports (`@MockitoBean CreateProfileUseCase`, etc.).
- **重點**: 
    - HTTP Method, Path, Status Code, Response Body structure.
    - Validation errors (400).
    - Mapping of Domain Exceptions to HTTP Errors (via `GlobalExceptionHandler`). *注意*: 由於 Controller 只依賴 Use Case，而 Use Case 被 Mock，所以這裡主要測 Web Layer 的映射和 DTO 轉換。若要測完整的 Exception 傳播，可能需要更深的集成測試或讓 Mock 拋出特定異常來驗證 Handler。

### Persistence Tests

- **Package**: `adapter.persistence.test`
- **註解**: `@DataJpaTest`, `@Import(JpaLoadProfilePort.class, JpaSavePort.class)`, `@ServiceConnection(PostgreSQLContainer.class)`.
- **重點**:
    - Entity mapping correctness.
    - Port implementations: `saveAndFlush` behavior, exception translation for duplicate emails, delete logic.
    - Converter tests.

### BDD / Acceptance Tests

- **Package**: `bootstrap.bdd`
- **工具**: Cucumber JVM + Spring Boot Test Context.
- **Feature File**: `features/personal-profile-crud.feature`
- **步驟定義**: `ProfileSteps.java`
- **執行方式**: 使用 `TestRestTemplate` 呼叫實際啟動的 API（在 Spring Context 中）。
- **重點**: 
    - End-to-end flow of CRUD operations.
    - Validation rules (Email format).
    - Conflict detection (Duplicate Email).
    - Not Found handling.
    - HTTP Status Codes and Headers.

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileId.java` | Value Object: Profile UUID identifier. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Name.java` | Value Object: User's name with validation invariants. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Email.java` | Value Object: User's email address with RFC validation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Profile.java` | Entity: Core business object for personal profile. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/CreateProfileCommand.java` | Command DTO for Create Use Case. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/GetProfileQuery.java` | Query DTO for Get Use Case. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/UpdateProfileCommand.java` | Command DTO for Update Use Case. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/DeleteProfileCommand.java` | Command DTO for Delete Use Case. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/result/ProfileResult.java` | Result DTO for Get Use Case. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Exception: Profile not found by ID. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Exception: Email already exists in system. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | Exception: Invalid email format provided. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface: Create new profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface: Retrieve profile details. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface: Update existing profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface: Delete a profile permanently. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface: Load profiles from storage. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface: Save/delete profiles in storage. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation: Orchestrates creation logic. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation: Orchestrates retrieval logic. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation: Orchestrates update logic. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation: Orchestrates deletion logic. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller: Exposes HTTP endpoints for Profile CRUD. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | Request DTO: Input data for creating a profile. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | Request DTO: Input data for updating a profile. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | Response DTO: Output data structure for profile details. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Exception Handler: Maps exceptions to appropriate HTTP responses. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity: Database mapping for Profile domain object. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/JpaProfileRepository.java` | Spring Data Repository: Interface for database access operations. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Port Implementation: Loads profiles from DB using JPA. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Port Implementation: Saves/deletes profiles in DB with exception translation. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Configuration: Wires up Use Case beans and Ports. |
| BDD | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/steps/ProfileSteps.java` | Step Definitions: Cucumber steps interacting via HTTP API. |
