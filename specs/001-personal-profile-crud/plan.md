# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計六角形架構的實現細節。所有程式碼將嚴格遵循 Hexagonal Architecture，依賴方向由外向內（Adapter -> Application -> Domain），並使用 JDK 17+ 特性。

## 領域模型 (Domain Model)

### Value Objects & Entities

由於系統要求「硬刪除」且識別碼由外部指定或 UUID 生成，我們採用 **UUID** 作為主鍵。為符合 JPA 最佳實踐與 Spec 中的 ID 格式，我們將識別碼封裝在 Value Object 中，但為了簡化持久層映射，Entity 直接使用 `String` 或 `UUID` 類型儲存。根據 Spec 示例 ID (`550e8400...`)，這裡使用 `java.util.UUID` 作為內部邏輯型別，但在 DTO 和 API 層以 String 傳輸。

*   **ProfileId**: `record`
    *   內容：`UUID value`
    *   不變條件：不可為 null。
*   **Email**: `record`
    *   內容：`String address`
    *   不變條件：非空白、符合 RFC 5322 基本格式（透過正則表達式驗證）、全域唯一（此約束由 Port/Repository 層保證）。
*   **DisplayName**: `record`
    *   內容：`String name`
    *   不變條件：允許 null 或空字串；若僅含空白字符視為 null 處理。長度限制建議設為 100 (可調整)。
*   **Profile** (Domain Entity): `class`
    *   屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`
    *   行為：
        *   `update(displayName, email)`: 更新顯示名稱與電子郵件。需檢查新 Email 是否與舊 Email 相同以避免不必要的唯一性衝突檢查（或在應用層處理）。
        *   `isSame(other)`: 用於比較識別碼。

### Domain Exceptions

*   **ProfileNotFoundException**: 當查詢或刪除的 ID 不存在時拋出。
*   **DuplicateEmailException**: 當嘗試建立或更新已存在的 Email 時拋出。
*   **InvalidEmailFormatException**: 當輸入的 Email 格式無效時拋出（也可在 Web Adapter DTO 驗證階段攔截，但 Domain 應保留最終防線）。
*   **EmptyDisplayNameException**: 如果業務規則要求 Display Name 不能全空白（根據 Spec "允許為空...視為空值"，此例外可能不需要，除非有非空約束。Spec 說「允許為空」，故不強制拋出，僅做 null/blank 轉換）。

## Use Cases & Ports

### Inbound Ports (Use Case Interfaces)

1.  **CreateProfileUseCase**
    *   `void execute(CreateProfileCommand command)`
2.  **GetProfileUseCase**
    *   `Optional<Profile> execute(GetProfileQuery query)`
3.  **UpdateProfileUseCase**
    *   `void execute(UpdateProfileCommand command)`
4.  **DeleteProfileUseCase**
    *   `void execute(DeleteProfileCommand command)`

### Outbound Ports (Repository Interfaces)

為了符合介面分離原則 (ISP)，我們將操作拆分為細粒度 Port：

1.  **SaveProfilePort**
    *   `Profile save(Profile profile)`
2.  **FindProfileByIdPort**
    *   `Optional<Profile> findById(UUID id)`
3.  **ExistsByEmailPort**
    *   `boolean existsByEmail(String email)` (或返回 Optional，視實現簡潔度而定，這裡用 boolean 足夠判斷衝突)
4.  **DeleteProfileByIdPort**
    *   `void deleteById(UUID id)`

### Application Services (Implementations)

*   **CreateProfileService**: 實作 `CreateProfileUseCase`。驗證 Email 格式 -> 檢查唯一性 (`existsByEmail`) -> 建立 Domain Entity -> 呼叫 `saveProfilePort.save()`。
*   **GetProfileService**: 實作 `GetProfileUseCase`。呼叫 `findProfileByIdPort.findById()`。若為空則拋出 `ProfileNotFoundException`（或在 Use Case 層處理 Optional）。
*   **UpdateProfileService**: 實作 `UpdateProfileUseCase`。先查詢現有 Profile -> 若不存在拋出例外 -> 執行 Domain 的 `update()` 方法 -> 再次檢查新 Email 是否與其他記錄衝突（排除自身）-> 儲存。
*   **DeleteProfileService**: 實作 `DeleteProfileUseCase`。先查詢確認存在 -> 呼叫 `deleteProfileByIdPort.deleteById()`。

## Web Adapter

### Controller

*   **ProfileController**: `@RestController`, path `/api/profiles`
    *   `POST /`: 對應 `CreateProfileUseCase`。回傳 201 + Location header。
    *   `GET /{id}`: 對應 `GetProfileUseCase`。回傳 200 + JSON body，或 404。
    *   `PUT /{id}`: 對應 `UpdateProfileUseCase`。回傳 200 + JSON body (更新後的資料)，或 404/409/400。
    *   `DELETE /{id}`: 對應 `DeleteProfileUseCase`。回傳 204 No Content，或 404。

### DTOs

*   **CreateProfileRequest** (`record`)
    *   `String email` (@NotBlank, @Email)
    *   `String displayName` (@Size(max=100))
*   **UpdateProfileRequest** (`record`)
    *   `String email` (@NotBlank, @Email)
    *   `String displayName` (@Size(max=100))
*   **ProfileResponse** (`record`)
    *   `String id`
    *   `String email`
    *   `String displayName` (若為 null 則回傳 null 或空字串，依 Jackson 設定)

### Error Handling

*   **GlobalExceptionHandler**: `@RestControllerAdvice`
    *   `MethodArgumentNotValidException`: 轉為 HTTP 400 Bad Request。
    *   `HttpMessageNotReadableException`: 轉為 HTTP 400 Bad Request。
    *   `ProfileNotFoundException`: 轉為 HTTP 404 Not Found。
    *   `DuplicateEmailException`: 轉為 HTTP 409 Conflict。
    *   **注意**：不要攔截 `Exception.class`，以免覆蓋 Spring Boot 預設的 404/5xx 行為。

## Persistence Adapter

### JPA Entity

*   **ProfileEntity**: `@Entity`, `@Table(uniqueConstraints = ...)`
    *   `id`: `UUID`, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`? 
        *   *修正*：Spec 中示例 ID 是固定的 UUID string。為了支援外部指定 ID（如測試中的 "update-id" 雖然是假 ID，但實務上 API 通常由 Server 生成 UUID 或由 Client 提供 UUID v4）。若允許 Client 提供 ID，則不能使用 `GenerationType.IDENTITY` 或 `AUTO` 讓 DB 生成。
        *   *決策*：鑑於 Spec 未明確說明 ID 來源，且 RESTful 慣例通常是 Server 生成 UUID。但在 Cucumber 場景中，我們需要「先建立」再「查詢」。如果 ID 由 Client 傳入，則需實現 `Persistable<UUID>` 以支持顯式 ID 插入。
        *   *簡化方案*：為符合標準 CRUD 且避免複雜的 Persistable 邏輯，除非 Spec 強制要求 Client 定義 ID，否則建議由 **Server 端生成 UUID**。這樣可以使用標準的 `@GeneratedValue(strategy = GenerationType.UUID)` (JPA 2.1+)。
        *   *然而*，Cucumber 場景 US-3, US-4 提到 "假設存在一個 ID 為..."。這暗示在測試環境中可能需要預置數據或使用固定 ID。
        *   *最終決定*：為了靈活性與測試便利性（以及符合某些微服務架構），我們將採用 **Client Provided ID** 模式（即 UUID 由呼叫方產生並放入 URL 路徑）。因此 Entity 必須實作 `Persistable<UUID>` 來處理 `isNew()` 狀態，或者簡單地在 Repository save 前檢查是否存在。考慮到 System Prompt 提示了 `Persistable` 陷阱，我們將嚴格遵循：**Entity 實作 `Persistable<UUID>`，ID 由外部指定（從 Path Variable 獲取）**。

    *   Fields:
        *   `id`: `String` (或 `UUID`) - @Id
        *   `email`: `String`, unique=true
        *   `displayName`: `String`
    *   Methods:
        *   `boolean isNew()`: returns true if id is null or new flag is set. Since we pass ID in path, it's never "new" in the sense of DB generation, but for JPA merge vs persist logic, we need to know if it exists. 
        *   *更簡單的實現*: 由於我們是 CRUD，且 ID 在 URL 中已知，我們可以依賴 `findById` 結果來決定是 Update 還是 Create？不，REST API 通常：POST /profiles (body has email/name) -> Server generates ID? Or Client provides ID?
        *   Spec US-1 POST `/api/profiles` body only has email/displayName. No ID field. This implies **Server Generates ID**.
        *   Spec US-2 GET `/api/profiles/{id}` uses a specific UUID string provided in the test setup ("假設存在..."). In BDD tests with Testcontainers, we often seed data or create via API first. If we create via API, the server returns the generated ID. Subsequent steps use that returned ID.
        *   Therefore: **ID is Generated by Server on CREATE**. On UPDATE/DELETE, the ID comes from Path Variable.
        *   So, Entity can use standard `@GeneratedValue(strategy = GenerationType.UUID)` for creation. For updates/deletes, we just load by existing ID.

### Repository

*   **ProfileRepository**: extends `JpaRepository<ProfileEntity, String>` (or UUID).
    *   `Optional<ProfileEntity> findByEmail(String email);`
    *   `boolean existsByEmail(String email);`

### Port Implementations & Converters

*   **ProfileConverter** (`@Component`): Static methods to convert between Domain `Profile` and JPA `ProfileEntity`.
*   **SaveProfilePortImpl**: Implements `SaveProfilePort`. Calls `repository.save(entity)`. Uses `saveAndFlush()` in transactional contexts if immediate constraint check is needed, but standard save is usually fine within a service method transaction. To ensure unique constraint violation throws immediately during unit/integration test transactions, `saveAndFlush` is safer.
*   **FindProfileByIdPortImpl**: Implements `FindProfileByIdPort`. Calls `repository.findById(id)`.
*   **ExistsByEmailPortImpl**: Implements `ExistsByEmailPort`. Calls `repository.existsByEmail(email)`.
*   **DeleteProfileByIdPortImpl**: Implements `DeleteProfileByIdPort**. Calls `repository.deleteById(id)`.

## Bootstrap

*   **UseCaseConfiguration**: `@Configuration` class that wires the Ports implementations to the Use Case Services.
    ```java
    @Bean
    CreateProfileService createProfileService(CreateProfilePort port) { ... } // Actually ports are injected into services
    ```
    Specifically:
    *   Define beans for all `...ServiceImpl` classes (e.g., `CreateProfileService`, `GetProfileService`).
    *   These services depend on Port interfaces. Spring will auto-wire the implementation beans defined elsewhere or we define them here? 
    *   Better: The Adapter modules provide the Bean definitions for the Port Implementations via their own `@Configuration` or Component Scanning? No, Hexagonal says Adapters are outside. Usually, the Bootstrap module scans both Application and Adapter packages, or explicitly imports configurations from adapters.
    *   Given the structure: `adapter-persistence` has JPA config. We can put a `PersistenceConfig` in adapter-persistence that exposes the Repository and Converter as Beans. Then `bootstrap.UseCaseConfiguration` injects those beans to wire the Service beans.

## 測試策略

### Unit Tests (Domain & Application)
*   **Location**: `domain/src/test/java/...` and `application/src/test/java/...`
*   **Tools**: JUnit 5, AssertJ, Mockito.
*   **Focus**:
    *   Domain Value Object immutability and validation rules.
    *   Use Case logic: Verify correct port calls, exception throwing on business rule violations (duplicate email). Mock all outbound ports.

### Integration Tests (Web Adapter)
*   **Location**: `adapter-web/src/test/java/...`
*   **Tools**: `@WebMvcTest`, `MockMvc`, `@MockitoBean`.
*   **Focus**:
    *   HTTP Request/Response mapping.
    *   Validation errors (400).
    *   Delegate calls to mocked Use Cases.
    *   Status codes (201, 200, 404, 409).

### Persistence Tests
*   **Location**: `adapter-persistence/src/test/java/...`
*   **Tools**: `@DataJpaTest`, Testcontainers (`PostgreSQLContainer`).
*   **Focus**:
    *   Entity mappings.
    *   Repository queries (findByEmail, existsByEmail).
    *   Unique constraint enforcement.
    *   Converter correctness.

### BDD / Cucumber Tests
*   **Location**: `bootstrap/src/test/resources/features/profile-crud.feature` & `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
*   **Tools**: Cucumber JVM, Spring Boot Test Context, `TestRestTemplate`.
*   **Focus**: End-to-end flow using a real database via Testcontainers.
    *   Scenario: Create -> Get -> Update -> Delete.
    *   Scenario: Duplicate Email Conflict.
    *   Scenario: Not Found cases.

## 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile UUID |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object with format validation |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object handling null/blank logic |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Aggregate Root / Entity holding state and business rules |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Exception when profile ID not found |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Exception on email conflict |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port interface for creation |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port interface for retrieval |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port interface for update |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port interface for deletion |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port for saving profiles |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port for finding by ID |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java` | Outbound Port for email uniqueness check |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | Outbound Port for deleting by ID |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller handling HTTP requests |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for POST request body |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for PUT request body |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO for response body |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity mapped to database table |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileRepository.java` | Spring Data JPA Repository interface |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverter.java` | Converts between Domain Profile and JPA Entity |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/SaveProfilePortImpl.java` | Implementation of SaveProfilePort using Repository |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/FindProfileByIdPortImpl.java` | Implementation of FindProfileByIdPort |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/ExistsByEmailPortImpl.java` | Implementation of ExistsByEmailPort |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/DeleteProfileByIdPortImpl.java` | Implementation of DeleteProfileByIdPort |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Main entry point with @SpringBootApplication |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Wires Use Case Services with Port Implementations |
