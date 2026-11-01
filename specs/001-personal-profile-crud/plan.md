# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計六角形架構的實現細節。所有程式碼將嚴格遵循 Hexagonal Architecture，依賴方向由外向內（Adapter -> Application -> Domain），並使用 JDK 17+ 特性。

## 領域模型 (Domain Model)

### Value Objects & Entities

由於系統要求「硬刪除」且識別碼由外部指定或 UUID 生成，我們採用 **UUID** 作為主鍵。為符合 JPA 最佳實踐與 Spec 中的 ID 格式，我們將識別碼封裝為 Value Object。

*   **`ProfileId`** (`record`)
    *   屬性：`String value` (儲存 UUID String)。
    *   不變條件：值必須是有效的 UUID 格式。
*   **`Email`** (`record`)
    *   屬性：`String address`。
    *   不變條件：不能為空白，需符合基本 Email 正規表示式。
*   **`DisplayName`** (`record`)
    *   屬性：`String name`。
    *   不變條件：允許 null 或空字串（視為無顯示名稱）。
*   **`Profile`** (`class`, Immutable after creation except for mutable fields managed by persistence, but logically treated as aggregate root)
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`。
    *   行為：
        *   `update(newEmail, newDisplayName)`: 驗證新資料並更新內部狀態。若 email 重複則拋出 Domain Exception。
        *   `delete()`: 標記為刪除狀態或直接由 Repository 處理物理刪除（本設計採用直接刪除實體的方式，故此方法可能僅作為業務邏輯檢查點，實際刪除由 Port 執行）。

### Domain Exceptions

*   **`InvalidEmailException`**: 當 Email 格式不符合規範時拋出。
*   **`DuplicateEmailException`**: 當嘗試建立或更新已存在的 Email 時拋出。
*   **`ProfileNotFoundException`**: 當查詢或操作不存在的 ID 時拋出（在 Application/Adapter 層捕捉並轉換為 HTTP 404）。

## Use Cases & Ports (Application Layer)

所有 Inbound Ports 以 `UseCase` 結尾，Outbound Ports 以 `Port` 結尾。

### 1. CreateProfileUseCase
*   **Inbound Port (`CreateProfileUseCase`)**:
    ```java
    void execute(CreateProfileCommand command); // Command contains email and displayName strings
    ```
*   **Outbound Port (`SaveProfilePort`)**:
    ```java
    Profile save(Profile profile);
    boolean existsByEmail(String email); // 用於唯一性檢查前置動作
    ```
*   **Implementation (`CreateProfileService`)**:
    *   驗證輸入 DTO。
    *   呼叫 `existsByEmail` 檢查衝突。若存在則拋出 `DuplicateEmailException`。
    *   建立新的 `Profile` 實體。
    *   呼叫 `save` 持久化。

### 2. GetProfileUseCase
*   **Inbound Port (`GetProfileUseCase`)**:
    ```java
    Optional<Profile> execute(String id);
    ```
*   **Outbound Port (`LoadProfilePort`)**:
    ```java
    Optional<Profile> findById(String id);
    ```
*   **Implementation (`GetProfileService`)**:
    *   呼叫 `findById`。
    *   若不存在，可選擇拋出 `ProfileNotFoundException` 或回傳 Empty（由 Controller 決定如何回應）。建議在此層回傳 Optional，讓 Adapter 處理 404 邏輯以符合 RESTful 慣例，或在 Service 中直接拋出例外以便統一錯誤處理。根據 Spec "查詢不存在的個人資料返回 404"，在 Application 層拋出特定例外較易於測試與追蹤。

### 3. UpdateProfileUseCase
*   **Inbound Port (`UpdateProfileUseCase`)**:
    ```java
    void execute(UpdateProfileCommand command); // Contains id, email, displayName
    ```
*   **Outbound Ports**:
    *   `LoadProfilePort`: `Optional<Profile> findById(String id)`
    *   `SaveProfilePort`: `Profile save(Profile profile)`
    *   (複用 Save/Load)
*   **Implementation (`UpdateProfileService`)**:
    *   呼叫 `findById(id)`。若無結果，拋出 `ProfileNotFoundException`。
    *   檢查新 Email 是否與其他現有記錄衝突（需排除自身 ID 的檢查，但由於是硬刪除且唯一約束在 DB 層，Application 層可依賴 DB 異常或額外查詢。為簡化並確保一致性，這裡建議：先 Load，再嘗試更新。若在 Domain 層驗證時發現重複則拋出例外；或者由 Persistence Adapter 捕捉 ConstraintViolationException 轉發）。
    *   呼叫 Profile 實體的 `update()` 方法。
    *   呼叫 `save` 持久化。

### 4. DeleteProfileUseCase
*   **Inbound Port (`DeleteProfileUseCase`)**:
    ```java
    void execute(String id);
    ```
*   **Outbound Ports**:
    *   `LoadProfilePort`: `boolean existsById(String id)` 或直接使用 Repository delete 並處理異常。
    *   `DeleteProfilePort`: `void deleteById(String id)`
*   **Implementation (`DeleteProfileService`)**:
    *   為了返回正確的 404，需要先確認存在性。
    *   呼叫 `existsById` (或 try-catch load)。若不存在，拋出 `ProfileNotFoundException`。
    *   呼叫 `deleteById`。

## Web Adapter (REST API)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

### Controller: `ProfileController`
路徑基礎：`/api/profiles`

1.  **POST `/api/profiles`**
    *   Method: `create(@Valid @RequestBody CreateProfileRequest request)`
    *   DTO: `CreateProfileRequest { String email; String displayName; }`
    *   Response: HTTP 201, Header `Location`.
    *   Error Mapping:
        *   Validation Fail -> 400 Bad Request.
        *   Duplicate Email -> 409 Conflict.
        *   Invalid Format -> 400 Bad Request.

2.  **GET `/api/profiles/{id}`**
    *   Method: `get(@PathVariable("id") String id)`
    *   Response: HTTP 200, Body `{ "email": "...", "displayName": "..." }`.
    *   DTO: `GetProfileResponse { String email; String displayName; }`
    *   Error Mapping: Not Found -> 404.

3.  **PUT `/api/profiles/{id}`**
    *   Method: `update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
    *   DTO: `UpdateProfileRequest { String email; String displayName; }` (注意：Spec 中 PUT 是全量更新，包含 email)
    *   Response: HTTP 200.
    *   Error Mapping:
        *   Not Found -> 404.
        *   Duplicate Email (other than self) -> 409.
        *   Validation Fail -> 400.

4.  **DELETE `/api/profiles/{id}`**
    *   Method: `delete(@PathVariable("id") String id)`
    *   Response: HTTP 204 No Content.
    *   Error Mapping: Not Found -> 404.

### Exception Handler: `GlobalExceptionHandler` (`@RestControllerAdvice`)
*   `MethodArgumentNotValidException` -> 400.
*   `HttpMessageNotReadableException` -> 400.
*   `DuplicateEmailException` -> 409 Conflict.
*   `ProfileNotFoundException` -> 404 Not Found.
*   *(Note: Do NOT catch generic Exception to avoid masking framework 404s)*.

## Persistence Adapter (JPA)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

### Entity: `ProfileEntity`
```java
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> { // Use String for UUID compatibility with simple ID handling or custom converter
    
    @Id
    private String id; // UUID string
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String displayName; // Nullable
    
    @Transient
    private boolean isNew = true;

    // Constructors, Getters, Setters
    
    @PostLoad
    @PostPersist
    public void markAsLoaded() {
        this.isNew = false;
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
```
*註：由於 Spec 中 ID 由外部提供（如 Cucumber 場景中的固定 UUID），我們不使用 `@GeneratedValue`。實作 `Persistable` 以正確處理 Insert vs Update 邏輯，避免 OptimisticLockingFailureException。*

### Repository: `ProfileRepository`
繼承 `JpaRepository<ProfileEntity, String>`。
額外方法：`boolean existsByEmail(String email);` (Spring Data JPA 自動生成)。

### Port Implementations
1.  **`JpaSaveProfilePort`** (`implements SaveProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `save`: 轉換 Domain Profile -> Entity -> `repository.saveAndFlush(entity)`。捕捉 `DataIntegrityViolationException` 並重新拋出為 `DuplicateEmailException`。
2.  **`JpaLoadProfilePort`** (`implements LoadProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `findById`: 查詢 Entity，若存在則轉換回 Domain Profile。
3.  **`JpaDeleteProfilePort`** (`implements DeleteProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `deleteById`: 直接呼叫 repository delete。需先檢查是否存在（由 Service 層或此處檢查），若不存在且需要精確控制例外，可在此處查詢後刪除，或直接調用 delete 並處理異常。為了符合 Application 層的邏輯分離，建議在 `DeleteProfileService` 中先 Check Existence，這裡只負責執行刪除動作。或者，如果 Repository delete 找不到東西不抛异常，则需要在 Service 层处理 404。

### Converter: `ProfileConverter` (Static Utility or Component)
*   `toDomain(ProfileEntity entity)`: 建立 Immutable Domain Object。
*   `toEntity(Profile profile, String id)`: 將 Domain 轉為 JPA Entity。注意 ID 的設定。

## Bootstrap Configuration

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

類別：`UseCaseConfiguration` (`@Configuration`)

```java
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileUseCase createProfileUseCase(SaveProfilePort savePort) {
        return new CreateProfileService(savePort);
    }

    @Bean
    GetProfileUseCase getProfileUseCase(LoadProfilePort loadPort) {
        return new GetProfileService(loadPort);
    }

    @Bean
    UpdateProfileUseCase updateProfileUseCase(LoadProfilePort loadPort, SaveProfilePort savePort) {
        return new UpdateProfileService(loadPort, savePort);
    }

    @Bean
    DeleteProfileUseCase deleteProfileUseCase(DeleteProfilePort deletePort) { // Or reuse Load/Save if logic allows, but separate port is cleaner for SOLID
         return new DeleteProfileService(deletePort); 
    }
    
    // Ports Beans (if not auto-detected via ComponentScan in Adapter modules, 
    // but typically Adapters are scanned separately or imported. 
    // Since we have specific adapter modules, we might need to import their configs or define beans here if they aren't @Components.
    // However, standard practice with modular structure: 
    // The Application layer defines interfaces. The Persistence module implements them and usually exposes them as Spring Beans via its own Configuration or Component Scan.
    // To keep Bootstrap clean of infrastructure details, we assume the Persistence Module has a configuration that registers these ports, 
    // OR we register them here explicitly if strict separation requires it.
    // Given the prompt's instruction "bootstrap ...組裝 use case bean", and "adapter-persistence ...實作 application 的 outbound port",
    // It is best to let the Persistence Adapter expose its Port implementations as Beans, so this Config only wires UseCases.
}
```
*修正策略*: `adapter-persistence` 模組應包含一個 `PersistenceConfiguration` (`@Configuration`)，將 `JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort` 註冊為 Bean。`UseCaseConfiguration` 僅負責注入這些 Ports 並創建 Use Case Beans。

## 測試策略

### Unit Tests (Domain & Application)
*   **Location**: `domain/src/test/java/...` / `application/src/test/java/...`
*   **Tools**: JUnit 5, AssertJ, Mockito. No Spring Context.
*   **Focus**:
    *   Domain Value Object validation rules.
    *   Use Case business logic:
        *   `CreateProfileService`: Verify duplicate check throws exception; verify save is called once on success.
        *   `GetProfileService`: Verify returns empty/not found correctly.
        *   `UpdateProfileService`: Verify email uniqueness check excludes self; verify update flow.
        *   `DeleteProfileService`: Verify existence check before delete.

### Integration Tests (Adapters)
*   **Web Adapter (`adapter-web`)**:
    *   **Tool**: `@WebMvcTest(ProfileController.class)`, `MockMvc`.
    *   **Mocks**: Mock all Inbound Port interfaces using `@MockitoBean`.
    *   **Focus**: HTTP status codes, JSON serialization/deserialization, Exception handling mapping (400, 404, 409).
*   **Persistence Adapter (`adapter-persistence`)**:
    *   **Tool**: `@DataJpaTest`, Testcontainers PostgreSQL (`@ServiceConnection`).
    *   **Imports**: Import specific Port implementations and Converter classes.
    *   **Focus**: JPA Entity mappings, Unique constraint enforcement, Conversion between Entity and Domain objects, Transactional behavior of ports.

### BDD / E2E Tests (Bootstrap/BDD)
*   **Location**: `bootstrap/src/test/resources/features/`, `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/`
*   **File Name**: `personal-profile-crud.feature`
*   **Runner**: Cucumber with Spring Boot integration.
*   **Tools**: `TestRestTemplate` for HTTP calls against the running application context.
*   **Steps**: Implement Gherkin steps in Traditional Chinese. Use `{string}` and `{int}` parameters.
*   **Setup**: Ensure database is clean before each scenario or use unique emails per run if possible, but Spec implies fixed IDs for testing scenarios, so we might need a "Given" step to seed data via API or direct DB access (via TestEntityManager injected into Step Defs). Seeding via API is cleaner but slower; seeding via DB directly in setup phase is faster for tests. Given the complexity, using `TestRestTemplate` to create test data in a "Before" hook or specific "Given" steps that are internal-only is an option, but standard Cucumber usually relies on public APIs. However, for "Assume exists", we can add a hidden/internal endpoint or simply rely on the fact that the first scenario creates it? No, specs say "Assume exists". 
    *   *Strategy*: Add a `@BeforeEach` in the Step Definition class that uses `TestRestTemplate` to delete any existing records with those specific IDs/Emails to ensure isolation, then let the feature flow handle creation if needed, OR implement a private helper method in Steps to insert directly into DB using `JpaRepository` autowired from context (since BDD runs in full Spring Context) to set up preconditions efficiently without exposing extra endpoints. This is acceptable for BDD layer as it's part of the test infrastructure within Bootstrap/BDD module.

## 類別清單

| 模組 | 完整路徑 (Relative to Root) | 職責描述 |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object: Profile UUID identifier. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object: Email address with validation. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object: Display name string. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Aggregate Root: Core business logic and state management. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | Domain Exception: Invalid email format. |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception: Duplicate email constraint violation. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface for creating profiles. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface for retrieving profiles. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface for updating profiles. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface for deleting profiles. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface for saving profile data. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface for loading profile data. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port Interface for deleting profile data. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase. |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| **Adapter-Web**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| **Adapter-Web**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for creating a new profile. |
| **Adapter-Web**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for updating an existing profile. |
| **Adapter-Web**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/GetProfileResponse.java` | DTO for returning profile data in GET responses. |
| **Adapter-Web**| `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Handles exceptions and maps them to HTTP status codes. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapping to database table. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data JPA Repository interface. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implementation of SaveProfilePort using JPA. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Implementation of LoadProfilePort using JPA. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Implementation of DeleteProfilePort using JPA. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Utility for converting between Domain and Entity models. |
| **Adapter-Persist** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/PersistenceConfiguration.java` | Configuration to register Port implementations as Beans. |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Main Spring Boot Application class. |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration to wire Use Cases with Ports. |
