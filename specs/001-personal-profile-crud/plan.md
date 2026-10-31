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
    *   不變條件：非空白、符合 RFC 5322 基本格式（透過正規表示式驗證）。全域唯一性由 Repository Port 保證。
*   **`DisplayName`** (`record`)
    *   屬性：`String name`。
    *   不變條件：允許 null 或空字串；若僅含空白字符則視為 null/空。長度限制建議設為合理範圍（如 255 字元），但 Spec 未明確規定上限，故暫不強制嚴格長度檢查，僅做 trim 處理。
*   **`Profile`** (`class`, Domain Entity)
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`。
    *   行為：
        *   `create(id, email, displayName)`：靜態工廠方法，建立新實體。
        *   `update(email, displayName)`：更新顯示名稱與電子郵件（需確保業務規則，此處假設 PUT 是全量更新，包含 Email 變更，但若 Email 重複會由 Port 層攔截）。
        *   `delete()`：標記刪除狀態或直接移除（在硬刪除架構下，通常由 Application Service 決定是否呼叫 Repository 的 delete，Domain 物件本身可保留狀態或無狀態）。

### Domain Exceptions

*   **`DuplicateEmailException`**：當嘗試儲存具有已存在電子郵件的個人資料時拋出。
*   **`InvalidEmailFormatException`**：當輸入的電子郵件格式不符合規範時拋出。
*   **`ProfileNotFoundException`**：當查詢或操作不存在的 ID 時拋出（通常在 Application Layer 轉換為 HTTP 404，但在 Domain/Port 層作為內部信號）。

## Use Cases & Ports (Application Layer)

所有 Inbound Ports 以 `UseCase` 結尾，Outbound Ports 以 `Port` 結尾。

### 1. Create Profile
*   **Inbound Port**: `CreateProfileUseCase`
    *   `void execute(CreateProfileRequest request)`
*   **Outbound Port**: `SaveProfilePort`
    *   `void save(Profile profile)`
*   **Implementation**: `CreateProfileService`
    *   驗證 Email 格式 -> 建立 Domain Entity -> 呼叫 SaveProfilePort.save()。

### 2. Get Profile
*   **Inbound Port**: `GetProfileUseCase`
    *   `Optional<Profile> execute(String id)`
*   **Outbound Port**: `LoadProfilePort`
    *   `Optional<Profile> findById(String id)`
*   **Implementation**: `GetProfileService`
    *   呼叫 LoadProfilePort.findById() -> 若不存在則拋出 `ProfileNotFoundException` (由 Controller 處理為 404)。

### 3. Update Profile
*   **Inbound Port**: `UpdateProfileUseCase`
    *   `void execute(UpdateProfileRequest request, String id)`
*   **Outbound Ports**:
    *   `LoadProfilePort`: `Optional<Profile> findById(String id)`
    *   `SaveProfilePort`: `void save(Profile profile)`
*   **Implementation**: `UpdateProfileService`
    *   呼叫 LoadProfilePort.findById(id) -> 若無則拋出例外 -> 呼叫 domain entity.update(...) -> 呼叫 SaveProfilePort.save()。

### 4. Delete Profile
*   **Inbound Port**: `DeleteProfileUseCase`
    *   `void execute(String id)`
*   **Outbound Ports**:
    *   `LoadProfilePort`: `Optional<Profile> findById(String id)`
    *   `DeleteProfilePort`: `void deleteById(String id)`
*   **Implementation**: `DeleteProfileService`
    *   呼叫 LoadProfilePort.findById(id) -> 若無則拋出例外 -> 呼叫 DeleteProfilePort.deleteById(id)。

## Web Adapter (REST API)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.web`

### Controller: `ProfileController`
實現上述四個 Use Case Ports。

*   **POST `/api/profiles`**
    *   Method: `create(@Valid @RequestBody CreateProfileRequest request, HttpServletResponse response)`
    *   DTO: `CreateProfileRequest` (`email`, `displayName`)。使用 Jakarta Validation (`@Email`, `@NotBlank`/`@Size`)。
    *   Response: 201 Created + Header `Location: /api/profiles/{id}`。
    *   Errors:
        *   Validation Fail -> 400 Bad Request (`MethodArgumentNotValidException`).
        *   Invalid Email Format (Domain Exception) -> 400 Bad Request.
        *   Duplicate Email (Domain Exception) -> 409 Conflict.

*   **GET `/api/profiles/{id}`**
    *   Method: `getById(@PathVariable("id") String id)`
    *   Response: 200 OK + JSON Body (`GetProfileResponse`: `id`, `email`, `displayName`).
    *   Errors:
        *   Not Found -> 404 Not Found (`ProfileNotFoundException`).

*   **PUT `/api/profiles/{id}`**
    *   Method: `update(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
    *   DTO: `UpdateProfileRequest` (`email`, `displayName`)。與 Create 類似，但 ID 來自路徑。
    *   Response: 200 OK。
    *   Errors:
        *   Not Found -> 404 Not Found.
        *   Validation/Format/Duplicate -> 400 / 409.

*   **DELETE `/api/profiles/{id}`**
    *   Method: `delete(@PathVariable("id") String id)`
    *   Response: 204 No Content.
    *   Errors:
        *   Not Found -> 404 Not Found.

### Error Handling: `GlobalExceptionHandler`
使用 `@RestControllerAdvice`。
*   `MethodArgumentNotValidException` -> 400 (JSON error detail).
*   `HttpMessageNotReadableException` -> 400.
*   `DuplicateEmailException` -> 409 Conflict.
*   `InvalidEmailFormatException` -> 400 Bad Request.
*   `ProfileNotFoundException` -> 404 Not Found.
*   **注意**：不攔截通用 `Exception.class`，以免覆蓋 Spring Boot 的 404/500 預設行為導致煙霧測試失敗。

## Persistence Adapter (JPA)

套件：`com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

### JPA Entity: `ProfileEntity`
實作 `Persistable<String>` 以處理 UUID 主鍵與 isNew 狀態（避免 saveAndFlush 時的衝突問題）。

```java
@Entity
@Table(name = "profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class ProfileEntity implements Persistable<String> {
    @Id
    private String id; // UUID string
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String displayName; // Nullable
    
    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    public void onLoad() {
        this.isNew = false;
    }

    // Getters, Setters, Constructor...
    @Override
    public String getId() { return id; }
    @Override
    public boolean isNew() { return isNew; }
}
```

### Repository: `ProfileRepository`
繼承 `CrudRepository<ProfileEntity, String>`。Spring Data 自動生成基本 CRUD，我們只需確保 `save` 行為正確。

### Port Implementations & Converters

*   **Converter**: `ProfileConverter` (Static methods or Component)
    *   `toDomain(ProfileEntity entity)` -> `Profile`
    *   `toEntity(Profile domain)` -> `ProfileEntity` (保留 ID 與 isNew 狀態)

*   **SavePort Impl**: `JpaSaveProfilePort`
    *   Implements `SaveProfilePort`.
    *   Uses `profileRepository.saveAndFlush(entity)`.
    *   Catches `DataIntegrityViolationException` and throws `DuplicateEmailException`.

*   **LoadPort Impl**: `JpaLoadProfilePort`
    *   Implements `LoadProfilePort`.
    *   Uses `profileRepository.findById(id)`. Returns empty Optional if not found.

*   **DeletePort Impl**: `JpaDeleteProfilePort`
    *   Implements `DeleteProfilePort`.
    *   Uses `profileRepository.deleteById(id)`.

## Bootstrap Configuration

套件：`com.github.chunpingwang.ollamaagentoutcome.bootstrap`

在 `UseCaseConfiguration.java` (`@Configuration`) 中定義所有 Application Layer Beans。

```java
@Configuration
public class UseCaseConfiguration {
    
    @Bean
    public CreateProfileService createProfileService(SaveProfilePort savePort, ProfileConverter converter) {
        return new CreateProfileService(savePort); // Converter can be injected or static util
    }
    
    @Bean
    public GetProfileService getProfileService(LoadProfilePort loadPort) {
        return new GetProfileService(loadPort);
    }
    
    @Bean
    public UpdateProfileService updateProfileService(LoadProfilePort loadPort, SaveProfilePort savePort) {
        return new UpdateProfileService(loadPort, savePort);
    }
    
    @Bean
    public DeleteProfileService deleteProfileService(LoadProfilePort loadPort, DeleteProfilePort deletePort) {
        return new DeleteProfileService(loadPort, deletePort);
    }
}
```

*注意：Ports (Interfaces) 不需要 Bean，它們由 Adapter 層實作並注入到 Service 中。Adapter 層的 Port Implementations 也需要被註冊為 Spring Beans（通常在各自的模組或 Bootstrap 中掃描，但根據規則，Bootstrap 負責組裝 Use Case，而 Adapter 的 Bean 通常由 `@Component` 自動偵測或在此處明確定義。鑑於架構分離，建議在 `adapter-persistence` 中使用 `@Repository`/`@Service` 註解讓 Component Scan 發現，或在 Bootstrap 中 `@Import`。為了簡化且符合「Bootstrap 組裝」原則，若 adapter 模組有獨立配置則更佳；但在單一專案結構下，通常依賴 Component Scan。這裡假設 `adapter.persistence` 中的實現類標記了 `@Repository` 等註解，Spring Boot 會自動載入。如果必須手動組裝，可在 Bootstrap 中 `@Bean` 這些 Ports 實現。**修正**：根據 System Prompt，Application Layer 不依賴 Spring，所以 Ports 是介面。Adapter 層實作這些介面。Bootstrap 需要將這些實例注入到 Application Services 中。最乾淨的方式是在 `bootstrap.UseCaseConfiguration` 中直接 `@Bean` 建立所有需要的 Port 實現類別和 Service 類別，或者依賴 Component Scan。考慮到 ArchUnit 限制與模組隔離，通常在 `adapter-persistence` 模組中，Port 實現類加上 `@Repository` (JPA) 或 `@Service`，並在 `bootstrap.ApplicationContextTest` 或主應用啟動時掃描該套件。但 System Prompt 強調 "Use Case Configuration" 負責組裝。因此，我們將在 `UseCaseConfiguration` 中明確定義 **Ports 的 Bean**（指向 Adapter 層的實現）以及 **Services 的 Bean**。*

*更精確的策略*：
1. `adapter-persistence` 中的 `JpaSaveProfilePort` 等類別標記為 `@Component` 或 `@Repository`。
2. `application` 中的 `CreateProfileService` 等類別標記為 `@Service`? **不行**，System Prompt 說 "不要在建構子注入... application layer classes should not have Spring annotations like @Service". 
3. **正確做法**：Application Layer 的 Service 是純 Java 類別。**Bootstrap** (`UseCaseConfiguration`) 必須使用 `@Bean` 方法來實例化這些 Service，並從 Spring Context 中獲取 Port 的實作（因為 Ports 是由 Adapters 提供的）。或者，Adapter 層不註冊為 Bean，而是由 Bootstrap 手動 new？這違反了 DI 原則。
4. **最佳實踐調整**：讓 `adapter.persistence` 中的 Port 實現類加上 `@Repository`/`@Service` (Spring Boot Component Scan 會處理)。在 `bootstrap.UseCaseConfiguration` 中，透過建構子參數注入這些 Port Beans，然後 return 新的 Application Service 實體。這樣既保持了 Application 層的無狀態/無註解特性，又利用了 Spring 的 DI。

## Testing Strategy

### Unit Tests (Domain & Application)
*   **Location**: `domain/src/test/java`, `application/src/test/java`.
*   **Tools**: JUnit Jupiter, AssertJ, Mockito.
*   **Focus**:
    *   Domain: Value Object validation rules (Email format, DisplayName trim).
    *   Application: Use Case logic flow. Mock Outbound Ports to verify correct calls and exception handling. No Spring context.

### Integration Tests (Web Adapter)
*   **Location**: `adapter-web/src/test/java`.
*   **Tools**: `@WebMvcTest`, `MockMvc`, `@MockitoBean` for Services.
*   **Focus**: HTTP request/response mapping, status codes, JSON serialization/deserialization, Validation errors.

### Persistence Tests
*   **Location**: `adapter-persistence/src/test/java`.
*   **Tools**: `@DataJpaTest`, Testcontainers (`PostgreSQLContainer`), `@ServiceConnection`.
*   **Focus**: Entity mappings, Repository queries, Unique constraint enforcement, Port implementations' conversion logic. Uses `PersistenceTestApplication` provided by skeleton.

### BDD / E2E Tests (Cucumber)
*   **Location**: `bootstrap/src/test/resources/features/001-personal-profile-crud.feature` & `bdd/ProfileSteps.java`.
*   **Tools**: Cucumber 8, JUnit Platform Engine, `TestRestTemplate` (via Bootstrap Context with Testcontainers).
*   **Feature File Name**: `001-personal-profile-crud.feature`
*   **Language**: `zh-TW`
*   **Scenario Mapping**:
    *   Create Profile Success -> POST `/api/profiles`
    *   Duplicate Email Fail -> POST `/api/profiles`
    *   Invalid Email Format Fail -> POST `/api/profiles`
    *   Get Profile Success -> GET `/api/profiles/{id}`
    *   Get Profile Not Found -> GET `/api/profiles/non-existent-id`
    *   Update Profile Success -> PUT `/api/profiles/{id}` + Verify via GET
    *   Update Profile Not Found -> PUT `/api/profiles/missing-id`
    *   Delete Profile Success -> DELETE `/api/profiles/{id}` + Verify via GET (404)
    *   Delete Profile Not Found -> DELETE `/api/profiles/non-existent-delete-id`

## Class List

| Module | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for UUID ID. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object with format validation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object handling null/blank names. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity representing a user profile. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception for duplicate email constraint violation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailFormatException.java` | Exception for invalid email syntax. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception when profile is not found in domain context. |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Interface for creating profiles. |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Interface for retrieving profiles by ID. |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Interface for updating profiles. |
| Application Port In | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Interface for deleting profiles. |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Interface for saving profiles. |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Interface for loading profiles. |
| Application Port Out | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Interface for deleting profiles. |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implements CreateProfileUseCase logic. |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implements GetProfileUseCase logic. |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implements UpdateProfileUseCase logic. |
| Application Use Case | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implements DeleteProfileUseCase logic. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | DTO for creating profile input. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | DTO for updating profile input. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GetProfileResponse.java` | DTO for returning profile data. |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapped to 'profiles' table. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository interface. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Implements LoadProfilePort using JPA. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Implements DeleteProfilePort using JPA. |
| Persistence Adapter | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Converts between Domain Profile and JPA Entity. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Main application entry point. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configures Application Services and Ports as Beans. |
| BDD Steps | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber step definitions for E2E tests. |
