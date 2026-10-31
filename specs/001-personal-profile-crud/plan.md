# Spec 001 — 個人資料管理 (Personal Profile CRUD) 技術設計 Plan

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，不依賴任何框架。

### Value Objects & Entities

*   **`ProfileId`** (`record`)
    *   類型：`java.util.UUID`
    *   描述：個人資料的唯一識別碼。
    *   不變條件：不可為 null。

*   **`Email`** (`record`)
    *   類型：`String`
    *   描述：電子郵件地址。
    *   不變條件：符合 RFC 5322 基本格式（由 Web Adapter 驗證後傳入，或在此做基礎檢查），不可為空。

*   **`Name`** (`record`)
    *   類型：`String`
    *   描述：用戶姓名。
    *   不變條件：不可為空，長度限制依業務需求（例如 1-50 字元）。

*   **`Bio`** (`record`)
    *   類型：`String`
    *   描述：個人簡介。
    *   不變條件：可為空或空白字串。

*   **`Profile`** (`class`, Domain Entity)
    *   屬性：
        *   `id: ProfileId` (唯讀，建立時設定)
        *   `name: Name`
        *   `email: Email`
        *   `bio: Bio`
    *   行為：
        *   `update(name, email, bio)`：更新資料。若新 email 與現有不同且重複，應在 Application/Adapter 層處理，Domain 僅負責狀態變更。
    *   不變條件：Email 在系統中必須唯一（此約束由 Persistence Adapter 的資料庫唯一索引 + Port 實作確保）。

### Domain Exceptions

*   **`ProfileNotFoundException`**
    *   觸發情境：查詢、更新、刪除時找不到對應 ID 的個人資料。
*   **`DuplicateEmailException`**
    *   觸發情境：建立或更新時，電子郵件已存在於系統中。
*   **`InvalidInputException`**
    *   觸發情境：輸入數據不符合業務規則（如格式錯誤，雖主要由 Web 層攔截，但 Domain 可保留此例外供 Use Case 拋出）。

## 2. Use Cases 與 Ports (Application Layer)

本層級定義介面 (`port`) 與具體邏輯 (`usecase/service`)。

### Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileCommand command);`
    *   參數 `CreateProfileCommand`: 包含 name, email, bio。
2.  **`GetProfileUseCase`**
    *   `ProfileResult execute(GetProfileQuery query);`
    *   參數 `GetProfileQuery`: 包含 id。
    *   回傳 `ProfileResult`: 包含 id, name, email, bio。
3.  **`UpdateProfileUseCase`**
    *   `void execute(UpdateProfileCommand command);`
    *   參數 `UpdateProfileCommand`: 包含 id, name, email, bio。
4.  **`DeleteProfileUseCase`**
    *   `void execute(DeleteProfileCommand command);`
    *   參數 `DeleteProfileCommand`: 包含 id。

### Outbound Ports (Repository Interfaces)

*   **`SaveProfilePort`**
    *   `Profile save(Profile profile);`
*   **`LoadProfilePort`**
    *   `Optional<Profile> findById(ProfileId id);`
*   **`ExistsByEmailPort`**
    *   `boolean existsByEmail(String email);`
*   **`DeleteProfileByIdPort`**
    *   `void deleteById(ProfileId id);`

*(註：為符合介面分離原則，將讀寫與查詢拆分為多個 Port 介面)*

### Use Case Implementations

1.  **`CreateProfileService`**: 實現 `CreateProfileUseCase`。檢查 Email 唯一性 -> 建立 Domain Entity -> 呼叫 `saveProfilePort.save()`。
2.  **`GetProfileService`**: 實現 `GetProfileUseCase`。呼叫 `loadProfilePort.findById()` -> 若無則拋出 `ProfileNotFoundException` -> 轉換為 Result DTO。
3.  **`UpdateProfileService`**: 實現 `UpdateProfileUseCase`。先 `findById` 確認存在 -> 檢查新 Email 是否與其他記錄重複（排除自身）-> 呼叫 Entity 的 update 方法 -> 呼叫 `saveProfilePort.save()`。
4.  **`DeleteProfileService`**: 實現 `DeleteProfileUseCase`。先 `findById` 確認存在 (或依賴 DB constraint) -> 呼叫 `deleteProfileByIdPort.deleteById()`。

## 3. Web Adapter (adapter-web)

Spring MVC Controller，負責 HTTP 請求/回應映射、輸入驗證、例外轉換。

*   **Controller: `ProfileController`** (`@RestController`, path `/profiles`)
    *   `POST /`: `create(@Valid @RequestBody CreateProfileRequest request)`
        *   回傳 201 + Location Header。
    *   `GET /{id}`: `get(@PathVariable("id") UUID id)`
        *   回傳 ProfileResponse (200)。
    *   `PUT /{id}`: `update(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)`
        *   回傳 200。
    *   `DELETE /{id}`: `delete(@PathVariable("id") UUID id)`
        *   回傳 204 No Content。

*   **DTOs:**
    *   `CreateProfileRequest`: fields `name` (String), `email` (String), `bio` (String). Jakarta Validation annotations.
    *   `UpdateProfileRequest`: same as Create.
    *   `ProfileResponse`: fields `id` (UUID), `name` (String), `email` (String), `bio` (String).

*   **Error Handling: `GlobalExceptionHandler`** (`@RestControllerAdvice`)
    *   `handle(ProfileNotFoundException)`: 404 Not Found.
    *   `handle(DuplicateEmailException)`: 409 Conflict.
    *   `handle(MethodArgumentNotValidException)`: 400 Bad Request.
    *   `handle(HttpMessageNotReadableException)`: 400 Bad Request.

## 4. Persistence Adapter (adapter-persistence)

JPA Entity、Spring Data Repository、Port 實作。

*   **Entity: `ProfileEntity`**
    *   Implements `Persistable<UUID>` (處理 ID 生成與 isNew 狀態)。
    *   Fields: `id` (@Id, @GeneratedValue 不適用，因由 Domain 產生 UUID? 或者讓 DB 產生？Spec 說 "系統自動產生"，通常 UUID 在 App 層產生較好控制或 DB 側。考慮到 Spec 提到 "識別碼 (ID): 系統自動產生的唯一識別符"，且測試場景中有預設 UUID，這裡採用 **Domain 層產生 UUID** 的方式，Entity 使用 `@Transient boolean isNew = true` 模式來避免 JPA 將其視為更新操作導致衝突，除非是從 DB 載入的實體。)
    *   *修正策略*: 為簡化並符合常見實踐，若 ID 由 Client 提供則不需 Persistable。但 Spec 暗示系統產生。若由 System 產生，通常在 Create Use Case 中 new Profile(UUID.randomUUID(), ...)。此時 Entity 不需要 `@GeneratedValue`。為了確保 JPA 正確執行 INSERT vs UPDATE，我們將 Entity 設計為：
        *   `id`: @Id, @Column(unique=true)
        *   `isNew`: @Transient, default true. `@PostLoad`, `@PrePersist` 管理此旗標。
        *   其他欄位對應 Name, Email, Bio。

*   **Repository: `ProfileJpaRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   `Optional<ProfileEntity> findByEmail(String email);`
    *   `boolean existsByEmail(String email);` (Spring Data 可自動生成)

*   **Port Implementations:**
    *   `JpaSaveProfilePort`: 實現 `SaveProfilePort`。將 Domain `Profile` 轉為 `ProfileEntity` -> `repository.saveAndFlush(entity)`。捕捉 `DataIntegrityViolationException` 拋出 `DuplicateEmailException`。
    *   `JpaLoadProfilePort`: 實現 `LoadProfilePort`。`repository.findById(id)` -> 若有則轉回 Domain `Profile`，否則回傳 Optional.empty()。
    *   `JpaExistsByEmailPort`: 實現 `ExistsByEmailPort`。呼叫 `repository.existsByEmail(email)`。
    *   `JpaDeleteProfileByIdPort`: 實現 `DeleteProfileByIdPort`。先 `findById` 確認存在（若不存在可選拋錯或忽略，視 Use Case 需求，通常 Delete Use Case 會先檢查 Existence）。然後 `deleteById(id)`。

## 5. Bootstrap

*   **Configuration: `UseCaseConfiguration`** (`@Configuration`)
    *   `@Bean CreateProfileService createProfileService(...)`
    *   `@Bean GetProfileService getProfileService(...)`
    *   `@Bean UpdateProfileService updateProfileService(...)`
    *   `@Bean DeleteProfileService deleteProfileService(...)`
    *   *(所有 Service Bean 依賴於 Outbound Ports)*
    *   Port Beans (如 `jpaSaveProfilePort`, `jpaLoadProfilePort` 等) 也在此定義或由 Spring Data 自動掃描 Repository 後由 Adapter 模組的 Configuration 提供？
    *   *架構調整*: 根據規則 "bootstrap 組裝 use case bean"，且 "adapter-persistence ... 實作 application 的 outbound port"。通常 Port 介面在 Application 層，實現在 Persistence 層。Bootstrap 需要將這些 Bean 注入到 Use Cases。
    *   因此，`UseCaseConfiguration` 應宣告所有 Inbound Port Services 為 @Beans。而 Outbound Ports 作為依賴被注入。Outbound Ports 本身也是 Beans，它們可以在 `adapter-persistence` 模組中通過 `@SpringBootApplication` 或 `@ComponentScan` 發現，或者在 Bootstrap 中明確 Import/Define。
    *   為了清晰與解耦，建議在 `adapter-persistence` 中建立一個 `PersistenceAdapterConfiguration` (`@Configuration`) 來定義所有的 Port 實現 Beans。然後在 `bootstrap.UseCaseConfiguration` 中使用 `@Import(PersistenceAdapterConfiguration.class)` 或直接讓 Component Scan 覆蓋整個專案（若結構允許）。
    *   *遵循 System Prompt*: "bootstrap 的 @SpringBootTest 測試必須... bootstrap 組裝任務不要再新增 context 測試... use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次；不要建立其他 @Configuration"。這暗示我們**不應該**在 adapter-persistence 裡放額外的 Configuration 類別來註冊 Port beans，除非骨架已經處理了 Repository 掃描。
    *   *最佳實踐調整*: Spring Data JPA Repositories 通常自動偵測。Port 實作類如果是 `@Service` 或 `@Component` 則會被掃描。但規則說 "application 禁止 Spring annotation"，所以 Port 實作在 `adapter.persistence` 可以加 `@Service` 嗎？規則沒禁止 Adapter 用 Spring Annotation。
    *   *然而*，System Prompt 強調："use case bean **只能**在 `UseCaseConfiguration` 以 `@Bean` 定義一次；不要建立其他 `@Configuration`"。這可能意味著所有 Bean (包括 Ports) 都應由 Bootstrap 統一管理，或者 Adapter 模組僅提供實現類，Bootstrap 負責將它們註冊為 Beans 並注入到 Services。
    *   為了嚴格遵守 "不要建立其他 @Configuration"，我們將：
        1.  Port 實作類別 (`JpaSaveProfilePort` etc.) 標記為 `@Service` 或在 `bootstrap.UseCaseConfiguration` 中明確宣告為 `@Bean`。考慮到可測試性與解耦，通常在 Hexagonal 架構中，Adapter 的具體實現是 Infrastructure concern。若 Bootstrap 是唯一配置點，我們可以在 `UseCaseConfiguration` 中定義這些 Port Beans。
        2.  但是，如果 Port 依賴 JPA Repository，而 Repository 是由 Spring Data 自動建立的，那麼 Port 實作需要注入 Repository。
        3.  方案：在 `adapter-persistence` 中的 Port 實作類別加上 `@Component` (或無註解但被 Component Scan 覆蓋)。由於 `bootstrap.ApplicationContextTest` 啟動整個 Context，且 `@SpringBootApplication` 預設掃描基底套件，只要 `adapter.persistence` 下的類別有 Spring 註解或被 Configuration 顯式導入即可。
        4.  **關鍵約束**: "不要建立其他 @Configuration"。這意味著我不能在 `adapter-persistence` 裡寫一個 `PersistenceConfig.java`。我可以將 Port 實作類標記為 `@Service`，讓它們被自動偵測。或者，更乾淨的方式是：Port 實作類不加註解，但在 `UseCaseConfiguration` 中通過 `@Bean` 方法手動創建並返回它們？不，這樣會導致代碼重複和緊耦合。
        5.  **重新解讀規則**: "use case bean 只能在 UseCaseConfiguration 以 @Bean 定義一次"。這主要限制的是 Inbound Services。對於 Outbound Ports，如果它們依賴 Infrastructure (JPA)，通常由 Adapter 模組負責註冊。既然不能建新 Config，我們假設骨架的 `@SpringBootApplication` 會掃描所有子包。因此，我們在 `adapter.persistence` 中的 Port 實現類加上 `@Service` 註解。這是允許的，因為它不是 Application 層（Application 層禁止），而是 Adapter 層。

*   **Cucumber Steps: `ProfileStepDefinitions`** (`@SpringBootTest`, `@Import(TestcontainersConfiguration.class)`)
    *   使用 `TestRestTemplate` 呼叫 API。
    *   管理測試數據狀態（透過 DB 清理或唯一性保證）。

## 6. 測試策略

1.  **Domain Tests** (`domain/src/test/java/...`)
    *   JUnit + AssertJ。
    *   測試 `Profile` 實體的建立、更新邏輯、不變條件檢查。
2.  **Application Tests** (`application/src/test/java/...`)
    *   JUnit + Mockito。
    *   Mock 所有的 Outbound Ports (`SaveProfilePort`, `LoadProfilePort` etc.)。
    *   測試 Use Case 業務邏輯：
        *   `CreateProfileService`: 驗證 Email 重複檢查、調用 Save Port。
        *   `GetProfileService`: 驗證 NotFoundException 拋出、Result 轉換。
        *   `UpdateProfileService`: 驗證 Existence check、Email 衝突處理 (排除自身)、Update 流程。
        *   `DeleteProfileService`: 驗證 Existence check、Delete 調用。
3.  **Web Adapter Tests** (`adapter-web/src/test/java/...`)
    *   `@WebMvcTest(ProfileController.class)` + `@MockitoBean` for Services.
    *   測試 HTTP 狀態碼映射：
        *   Valid Input -> 201 / 200 / 204.
        *   Invalid Format -> 400.
        *   Service throws ProfileNotFoundException -> 404.
        *   Service throws DuplicateEmailException -> 409.
    *   測試 Request Body Binding 與 Validation。
4.  **Persistence Adapter Tests** (`adapter-persistence/src/test/java/...`)
    *   `@DataJpaTest` + Testcontainers PostgreSQL.
    *   測試 JPA Entity 的映射、ID 生成策略（isNew 邏輯）。
    *   測試 Port Implementations:
        *   Mock Repository? No, usually test the integration of Port with Repo and DB constraints if possible, or mock Repo to isolate Port logic. Given "Port實作", we can unit test the conversion and exception mapping by mocking the Repository inside the Port implementation, OR integration test the whole stack.
        *   Strategy: Unit test Port implementations using Mockito for Repositories to verify correct translation between Domain and JPA Entities and Exception handling (e.g., DataIntegrityViolationException -> DuplicateEmailException). This keeps tests fast and isolated from DB schema changes in this specific layer's scope, though an Integration test is also valuable. Let's stick to Unit testing Ports against mocked Repos for speed and clarity, as per standard TDD flow before full integration.
5.  **BDD / Cucumber Tests** (`bootstrap/src/test/resources/features/`, steps in `bdd` package)
    *   Full Stack Integration via HTTP.
    *   Feature File: `personal-profile-crud.feature`.
    *   Scenarios cover all Acceptance Criteria in Spec.

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object: Profile UUID identifier. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | Value Object: User name string with validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object: Email address string with validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Bio.java` | Value Object: Biography text. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity: Core business object for a profile. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Exception: Thrown when profile is not found. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Exception: Thrown on duplicate email constraint violation. |
| **Application (In)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface: Create new profile command. |
| **Application (In)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface: Get profile by ID query. |
| **Application (In)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface: Update existing profile command. |
| **Application (In)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface: Delete profile command. |
| **Application (Out)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface: Save a Profile entity. |
| **Application (Out)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface: Load a Profile by ID. |
| **Application (Out)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Outbound Port Interface: Check email uniqueness. |
| **Application (Out)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | Outbound Port Interface: Delete profile by ID. |
| **Application (UC)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Impl: Orchestrates creation logic and validation. |
| **Application (UC)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Impl: Retrieves and converts profile data. |
| **Application (UC)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Impl: Updates existing profile with checks. |
| **Application (UC)** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Impl: Deletes profile after existence check. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller: Maps HTTP requests to Use Cases. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO: Input for creating a profile. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO: Input for updating a profile. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO: Output response containing profile details. |
| **Web Adapter** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Exception Handler: Converts exceptions to HTTP status codes. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity: Database mapping for Profile, implements Persistable. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java` | Spring Data Repository: Interface for DB access operations. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Port Impl: Saves Domain Profile via JPA Repo, handles constraints. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Port Impl: Loads Domain Profile from JPA Repo. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaExistsByEmailPort.java` | Port Impl: Checks email existence in DB. |
| **Persistence Adapter** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfileByIdPort.java` | Port Impl: Deletes profile by ID from DB. |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Configuration: Wires Use Case Services with their Ports. |
| **BDD Steps** | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/stepdefs/ProfileStepDefinitions.java` | Cucumber Step Definitions: Executes scenarios against HTTP API. |
