# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計細節，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於基礎設施。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

由於識別碼由系統生成且需保持唯一性，我們採用 **Application-Generated ID** 模式，讓 Domain 物件自行持有 UUID，並透過 JPA 的 `Persistable` 介面處理持久化狀態。

*   **`ProfileId`** (`record`)
    *   類型：UUID
    *   職責：封裝個人資料的唯一標識符。
    *   不變條件：不可為 null。

*   **`Email`** (`record`)
    *   類型：String
    *   職責：封裝電子郵件地址。
    *   不變條件：符合 RFC 5322 基本格式（非空、包含 @）。在 Domain 層進行初步驗證，Web 層也應有 DTO 驗證作為第一道防線。

*   **`Name`** (`record`)
    *   類型：String
    *   職責：封裝用戶姓名。
    *   不變條件：非空，長度限制（例如 1-100 字元）。

*   **`Profile`** (`class`, immutable fields via constructor, mutable setters for updates)
    *   屬性：
        *   `id: ProfileId` (系統生成或應用層分配)
        *   `name: Name`
        *   `email: Email`
    *   業務規則：
        *   建立時必須提供有效的 name 和 email。
        *   更新時，email 不得與現有其他 profile 衝突（此邏輯由 Port/Repository 層處理，Domain 僅負責自身狀態一致性）。
    *   注意：為了支持 JPA 的 `findById` + update pattern，實體類需保留 setter 或使用 builder，但領域行為應盡量保持不可變性概念。在此設計中，我們將使用標準 Java class 以方便 JPA 映射與更新操作，但在 Domain Service 中確保業務規則。

### Domain Exceptions

*   **`DuplicateEmailException`** (`extends RuntimeException`)
    *   觸發時機：嘗試保存具有重複電子郵件的個人資料。
    *   用途：告知 Adapter/Web 層返回 HTTP 409 Conflict。

*   **`InvalidEmailFormatException`** (`extends IllegalArgumentException`)
    *   觸發時機：輸入的電子郵件格式無效。
    *   用途：告知 Web 層返回 HTTP 400 Bad Request。（註：通常 DTO 驗證會攔截大部分，但若 Domain 有更嚴格的規則可在此拋出）

*   **`ProfileNotFoundException`** (`extends RuntimeException`)
    *   觸發時機：查詢或刪除不存在的 ID。
    *   用途：告知 Web 層返回 HTTP 404 Not Found。

## 2. Use Cases & Ports (Application Layer)

遵循命名規範：Inbound Port 以 `UseCase` 結尾，Outbound Port 以 `Port` 結尾，實作以 `Service` 結尾。

### Inbound Ports (Interfaces)

1.  **`CreateProfileUseCase`**
    ```java
    void execute(CreateProfileCommand command);
    // Command: name, email
    ```
2.  **`LoadProfileUseCase`**
    ```java
    ProfileResult execute(String id);
    // Result: id, name, email
    ```
3.  **`UpdateProfileUseCase`**
    ```java
    void execute(UpdateProfileCommand command);
    // Command: id, name, email
    ```
4.  **`DeleteProfileUseCase`**
    ```java
    void execute(String id);
    ```

### Outbound Ports (Interfaces)

1.  **`SaveProfilePort`**
    ```java
    void save(Profile profile);
    ```
2.  **`FindProfileByIdPort`**
    ```java
    Optional<Profile> findById(String id);
    ```

### Use Case Implementations (`usecase` package)

*   `CreateProfileService`: 接收 DTO/Command -> 驗證 Domain Rules -> 呼叫 `SaveProfilePort.save()`。若發生 `DuplicateEmailException`，則向上拋出或轉換為 Application Exception（此處直接讓 Domain Exception 穿透至 Adapter 處理較簡潔）。
*   `LoadProfileService`: 呼叫 `FindProfileByIdPort.findById()`。若不存在，拋出 `ProfileNotFoundException`。
*   `UpdateProfileService`: 先呼叫 `FindProfileByIdPort.findById()` 獲取現有實體。若無，拋出 `ProfileNotFoundException`。更新欄位後，呼叫 `SaveProfilePort.save()`。需檢查新 Email 是否與其他 ID 衝突（這通常需要 Repository 提供額外查詢方法，或在 Port 層處理唯一性約束異常並重拋）。
*   `DeleteProfileService`: 先呼叫 `FindProfileByIdPort.findById()`。若無，拋出 `ProfileNotFoundException`。然後需要一個刪除操作。由於 Spring Data JPA 的 `deleteById` 是標準操作，我們可以新增一個 **`DeleteProfilePort`** 或者在 `SaveProfilePort` 中不適用。為了單一職責，建議新增：
    *   **`DeleteProfilePort`**: `void delete(String id);`

## 3. Web Adapter (adapter-web)

### Controller: `ProfileController`

*   `POST /profiles` -> `createProfile(@Valid @RequestBody CreateProfileRequest request)`
    *   呼叫 `CreateProfileUseCase.execute()`
    *   成功：返回 201 Created，Header Location 指向 `/profiles/{id}`。回應體包含新生成的 Profile DTO。
    *   失敗：400 (Invalid Input), 409 (Duplicate Email).
*   `GET /profiles/{id}` -> `getProfile(@PathVariable("id") String id)`
    *   呼叫 `LoadProfileUseCase.execute(id)`
    *   成功：返回 200 OK，回應體為 Profile DTO。
    *   失敗：404 Not Found.
*   `PUT /profiles/{id}` -> `updateProfile(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
    *   呼叫 `UpdateProfileUseCase.execute(new Command(id, ...))`
    *   成功：返回 200 OK。
    *   失敗：400 (Invalid Input), 404 Not Found, 409 Duplicate Email.
*   `DELETE /profiles/{id}` -> `deleteProfile(@PathVariable("id") String id)`
    *   呼叫 `DeleteProfileUseCase.execute(id)`
    *   成功：返回 204 No Content。
    *   失敗：404 Not Found.

### Request/Response DTOs (`web.dto`)

*   **`CreateProfileRequest`**: `name`, `email`. Jakarta Validation: `@NotBlank`, `@Email`.
*   **`UpdateProfileRequest`**: `name`, `email`. Jakarta Validation: `@NotBlank`, `@Email`.
*   **`ProfileResponse`**: `id`, `name`, `email`.

### Exception Handler: `GlobalExceptionHandler`

*   `handle(ProfileNotFoundException)`: 404 Not Found.
*   `handle(DuplicateEmailException)`: 409 Conflict.
*   `handle(MethodArgumentNotValidException)`: 400 Bad Request.
*   `handle(HttpMessageNotReadableException)`: 400 Bad Request.

## 4. Persistence Adapter (adapter-persistence)

### JPA Entity: `ProfileEntity`

*   實作 `Persistable<UUID>` 以支持 Application-Generated ID。
*   Fields: `uuid` (@Id), `name`, `email`.
*   `isNew()`: returns true if uuid is null or a special flag, but since we generate UUID in App layer before saving, we can set a transient field or check if the entity was just created. *Correction*: Since Domain generates UUID, the Port will create an Entity with that UUID. We need to distinguish between "new" and "existing". A common pattern is using a separate boolean field `persisted` which defaults to false, updated by lifecycle callbacks (`@PostLoad`, `@PreUpdate`). Or simply rely on the fact that if it comes from Create UseCase, it's new. However, for Update, we load existing. So `saveAndFlush` works fine if we handle the unique constraint correctly. The prompt suggests implementing `Persistable` with `isNew = true` initially. Let's stick to the prompt's advice: `@Transient private boolean isNew = true;` + setters via constructors/converters.

### Repository: `JpaProfileRepository` extends `CrudRepository<ProfileEntity, UUID>`

*   Standard methods provided by Spring Data JPA.
*   No custom queries needed unless specific validation requires checking email existence before save (which might cause race conditions). Better to let DB unique constraint throw exception and catch it in Port.

### Outbound Ports Implementation

1.  **`JpaSaveProfilePort`** implements `SaveProfilePort`
    *   Converts `Domain Profile` -> `ProfileEntity`.
    *   Calls `repository.saveAndFlush(entity)`.
    *   Catches `DataIntegrityViolationException` (Unique Constraint Violation) and re-throws as `DuplicateEmailException`.
2.  **`JpaFindProfileByIdPort`** implements `FindProfileByIdPort`
    *   Calls `repository.findById(uuid)`.
    *   Converts `Optional<Entity>` -> `Optional<Profile>`. Returns empty if not found.
3.  **`JpaDeleteProfilePort`** implements `DeleteProfilePort`
    *   Checks existence first? Or just delete? Spec says "If not exists, return 404". So we must check existence.
    *   Call `findById`. If absent, throw `ProfileNotFoundException`. Else call `deleteById(id)` or `delete(entity)`. Flush to ensure immediate effect for tests/transactions.

## 5. Bootstrap (`bootstrap`)

*   **`UseCaseConfiguration`**: `@Configuration` class that defines beans for:
    *   `CreateProfileService`
    *   `LoadProfileService`
    *   `UpdateProfileService`
    *   `DeleteProfileService`
    *   These services are wired with the Outbound Ports which are also defined here or auto-detected if in component scan path (but explicit bean definition is safer per rules). Actually, since Adapters are separate modules and might be scanned separately, it's better to define the Port implementations as Beans in their respective adapter configurations OR have a central configuration. Given the structure, usually Adapter configs handle JPA beans. Let's assume standard Spring Boot scanning finds them IF they are annotated. BUT rule says: "use case bean only in UseCaseConfiguration... do not create other @Configuration". This implies we should manually wire everything in `UseCaseConfiguration` using `@Bean` methods, importing the necessary classes from adapters? No, cross-module dependency issues.
    *   *Correction*: The prompt states: "adapter-persistence ... implements application outbound port". And "bootstrap ... @SpringBootApplication ... UseCaseConfiguration". Usually, in Hexagonal Arch with Spring, the Infrastructure/Adapter layer provides the implementation of ports via `@Component` or specific config, and Application layer just depends on interfaces. However, strict separation suggests that `UseCaseConfiguration` should inject these beans. If `adapter-persistence` is a separate module, its beans need to be visible. Standard practice: `adapter-persistence` has a `PersistenceAutoConfiguration` which registers the Ports as Beans. But the rule forbids new Configs except `UseCaseConfiguration`.
    *   *Resolution*: We will rely on Component Scanning for the Adapters if possible, OR define all Port Implementations as `@Bean`s inside `UseCaseConfiguration` by referencing their fully qualified names (assuming classpath access). Since `application` cannot depend on `adapter`, it can't reference them directly in code unless they are passed in. Wait, `UseCaseConfiguration` IS in `bootstrap`, which CAN depend on everything. So we can write:
        ```java
        @Bean
        SaveProfilePort saveProfilePort(JpaProfileRepository repo) { return new JpaSaveProfilePort(repo); }
        ```
        This requires `JpaProfileRepository` to be available. It will be auto-configured by Spring Boot Data JPA starter if present in classpath.

## 6. Testing Strategy

*   **Domain Tests** (`domain/src/test/java/...`):
    *   Test Value Object immutability and validation rules (e.g., invalid email format throws exception).
    *   Pure Java, no Spring.
*   **Application Tests** (`application/src/test/java/...`):
    *   Use Mockito to mock Outbound Ports.
    *   Verify that `CreateProfileService` calls `save()` only with valid data.
    *   Verify exceptions are thrown correctly for business rule violations.
*   **Web Adapter Tests** (`adapter-web/src/test/java/...`):
    *   `@WebMvcTest(ProfileController.class)` + `@MockitoBean` for all Use Cases.
    *   Mock the Use Case interfaces directly.
    *   Assert HTTP status codes (201, 400, 409, 404, 200, 204) and Response Body structure.
*   **Persistence Adapter Tests** (`adapter-persistence/src/test/java/...`):
    *   `@DataJpaTest` + Testcontainers PostgreSQL.
    *   Test `JpaSaveProfilePort`: Save new entity, verify ID generation/preservation, test duplicate email constraint handling (catching DB exception -> Domain Exception).
    *   Test `JpaFindProfileByIdPort`: Find existing, return empty for non-existing.
    *   Test `JpaDeleteProfilePort`: Delete existing, throw if not exists.
*   **BDD / Integration Tests** (`bootstrap/src/test/resources/features/` & `bdd/` package):
    *   Feature File: `personal-profile-crud.feature`.
    *   Steps use `TestRestTemplate` to hit actual endpoints.
    *   Verify end-to-end flow including database persistence via Testcontainers.

## 7. Class List

| Module | Path | Responsibility |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile UUID |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object with Email validation |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | Value Object for User Name |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity holding state and basic business logic |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Exception for duplicate email constraint violation |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailFormatException.java` | Exception for invalid email format in domain layer |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Exception when profile is not found by ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface: Create new profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/LoadProfileUseCase.java` | Inbound Port Interface: Load profile by ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface: Update existing profile |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface: Delete profile by ID |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface: Save a Profile entity |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port Interface: Find Profile by UUID string |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port Interface: Delete Profile by UUID string |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileService.java` | Implementation of LoadProfileUseCase |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller handling HTTP requests/responses |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for creating profile input validation |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for updating profile input validation |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | DTO for returning profile data to client |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Handles domain and validation exceptions, maps to HTTP status codes |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity mapping Profile to database table |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaProfileRepository.java` | Spring Data Repository interface for ProfileEntity |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA Repository |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java` | Implements FindProfileByIdPort using JPA Repository |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Implements DeleteProfilePort using JPA Repository |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Main application entry point and component scan base |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration class wiring Use Cases with their Port Implementations as Beans |
