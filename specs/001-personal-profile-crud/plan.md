# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了「個人資料管理」功能的技術設計，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於基礎設施，並支援 TDD/BDD 開發流程。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

- **ProfileId** (`record`)
    - 內容：`UUID value`
    - 不變條件：值不得為 null。
    - 用途：作為 Domain Entity 的唯一識別碼，由應用層或網關生成後傳入，避免 JPA 自動生成的衝突。

- **Email** (`record`)
    - 內容：`String address`
    - 不變條件：格式需符合 RFC 5322 基本規範（非空、包含 @）。
    - 用途：封裝電子郵件驗證邏輯，確保領域內使用的 Email 皆合法。

- **DisplayName** (`record`)
    - 內容：`String name`
    - 不變條件：允許 null 或空白字串（視為無暱稱）。若輸入僅含空白字符，內部可統一設為 null 或保留原樣，視業務需求而定；此處暫採保留原樣但 UI 層處理顯示。
    - 用途：用戶的顯示名稱。

- **Bio** (`record`)
    - 內容：`String content`
    - 不變條件：長度限制（例如 500 字元），防止過長數據存入。
    - 用途：個人簡介。

- **Profile** (Entity)
    - 屬性：
        - `id: ProfileId`
        - `email: Email`
        - `displayName: DisplayName`
        - `bio: Bio`
    - 行為：
        - `update(displayName, bio)`：更新暱稱與簡介。注意：根據 REST PUT 語義，通常全量更新，但 email 作為唯一識別聯絡方式，在「修改」情境下是否允許變更需釐清。根據 Spec US-3 與驗收場景，PUT 請求包含 email 欄位，但若該 email 已存在於其他記錄則應失敗。為簡化領域邏輯，我們假設 PUT 操作主要用於更新 displayName 和 bio，若 email 發生變化且與其他記錄衝突，由 Port 或 Application Layer 攔截。*修正策略*：鑑於 Spec 中 PUT 包含 email，且驗證條件提到「嘗試更新不存在的...404」以及「成功更新...再次查詢變更為新值」，但未明確說明 PUT 時能否改 email。然而，RESTful 慣例中 PUT /{id} 通常意味著替換資源。若允許改 email，必須檢查 uniqueness。為符合 SOLID 單一職責，Domain Entity 僅負責狀態一致性（如 bio 長度），Unique Constraint 的檢查放在 Persistence Port 層處理較佳，因為 Domain 不應依賴資料庫約束。因此，`Profile` 實體本身不包含 `updateEmail` 方法，而是通過 `update(displayName, bio)` 進行部分更新，或者如果業務要求 PUT 可改 email，則在 UseCase 層呼叫特定的 port 方法或在 Entity 提供全量 setter 但由外部保證唯一性。
        - *決定*：為了保持 Domain 純淨且不耦合 DB 約束，`Profile` 提供 `changeDisplayName` 和 `changeBio` 方法。對於 PUT 請求中的 Email 變更，由於 Spec US-3 場景顯示 PUT body 包含 email，我們將在 Application Layer (Use Case) 處理邏輯：先查舊資料，比較 email 是否改變，若改變且重複則拋出例外；若不改變或唯一，則更新其他欄位。或者更簡單地，Entity 提供一個 `reconfigure(email, displayName, bio)` 方法，但這樣會讓 Domain 知道 Email 的唯一性需求嗎？不，Domain 只關心值對象的有效性。唯一性檢查應在 Outbound Port (`SaveProfilePort`) 執行，捕捉 DataIntegrityViolationException 並轉為 Domain Exception。
    - 不變條件：建立時所有必填欄位（email）必須有效。

### Domain Exceptions

- **InvalidEmailException**：當 Email Value Object 驗證失敗時拋出。
- **DuplicateEmailException**：當嘗試儲存具有已存在電子郵件的 Profile 時拋出（由 Persistence Adapter 轉換而來）。
- **ProfileNotFoundException**：當查詢指定 ID 的 Profile 不存在時拋出。
- **BioTooLongException**：當 Bio 長度超過限制時拋出。

## 2. Use Cases & Ports

### Inbound Ports (Interfaces)

1.  **`CreateProfileUseCase`**
    -   `void execute(CreateProfileCommand command);`
    -   參數 Command 包含：`email`, `displayName`, `bio`。

2.  **`GetProfileUseCase`**
    -   `ProfileDto execute(ProfileId id);`
    -   回傳 DTO 或 Entity（視測試便利性而定，通常 Application Layer 回傳 DTO 以解耦 Domain 與 Web）。此處定義回傳 `ProfileResponse` (DTO)。

3.  **`UpdateProfileUseCase`**
    -   `void execute(UpdateProfileCommand command);`
    -   參數 Command 包含：`id`, `email`, `displayName`, `bio`。
    -   *注意*：雖然 PUT 語義是全量更新，但為了處理 Email 唯一性衝突，Use Case 需先讀取現有資料進行比較，或直接呼叫 Port 並讓其處理衝突。為符合「查詢後更新」的常見模式且避免併發問題簡單化，這裡採用：Load -> Validate Uniqueness if changed -> Save。

4.  **`DeleteProfileUseCase`**
    -   `void execute(ProfileId id);`

### Outbound Ports (Interfaces)

1.  **`SaveProfilePort`**
    -   `void save(Profile profile);`
    -   負責將 Profile 持久化。若發生重複電子郵件，應拋出 `DuplicateEmailException` 或由 Adapter 捕捉 DB 異常後轉發。根據 System Prompt，建議在 Port 實作中捕捉 `DataIntegrityViolationException` 並轉換。

2.  **`FindProfileByIdPort`**
    -   `Optional<Profile> findById(ProfileId id);`

3.  **`ExistsByEmailPort`** (可選，或合併於 Find/Save)
    -   為簡化架構，我們可以不單獨定義此 Port，而是在 `UpdateProfileUseCase` 中透過 `FindProfileByIdPort` 獲取舊記錄，並在 `SaveProfilePort` 層面檢查唯一性約束。如果 `SaveProfilePort` 無法直接檢查其他 ID 的唯一性（因為它只接收 Entity），則需要在 Use Case 層做邏輯判斷：
        - *優化策略*：讓 `SaveProfilePort` 僅負責儲存。在 `CreateProfileUseCase` 和 `UpdateProfileUseCase` 中，如果需要檢查 Email 唯一性，可以引入一個 `CheckEmailUniquenessPort` 或者更簡單地，利用 JPA Repository 的 `existsByEmail` 方法作為另一個 Outbound Port。
        - *決定*：為了保持 Port 介面小而專一，新增 **`ValidateEmailUniquePort`**。
            -   `boolean isUnique(String email, ProfileId excludeId);`
            -   這使得 Domain/Application 不需要知道資料庫細節，只需詢問「這個 Email 是否可用」。

### Use Case Implementations (Services)

-   `CreateProfileService`：實現 `CreateProfileUseCase`。驗證輸入 -> 建立 Domain Entity -> 呼叫 `ValidateEmailUniquePort.isUnique()` -> 呼叫 `SaveProfilePort.save()`。
-   `GetProfileService`：實現 `GetProfileUseCase`。呼叫 `FindProfileByIdPort.findById()` -> 若無則拋出 `ProfileNotFoundException` -> 轉換為 DTO 回傳。
-   `UpdateProfileService`：實現 `UpdateProfileUseCase`。呼叫 `FindProfileByIdPort.findById()` -> 若無則拋出 `ProfileNotFoundException` -> 比較新舊 Email，若不同則呼叫 `ValidateEmailUniquePort.isUnique(email, id)` -> 更新 Entity 狀態 -> 呼叫 `SaveProfilePort.save()`。
-   `DeleteProfileService`：實現 `DeleteProfileUseCase`。呼叫 `FindProfileByIdPort.findById()` -> 若無則拋出 `ProfileNotFoundException` -> 呼叫 `SaveProfilePort.delete(id)` (需新增此方法或擴充 SavePort)。
    - *修正*：通常 Delete 和 Save 是分開的 Port。新增 **`RemoveProfilePort`**：`void remove(ProfileId id);`。

## 3. Web Adapter

### Controller

-   **`ProfileController`** (`@RestController`)
    -   Base Path: `/api/profiles`
    -   Dependencies: Injected Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`, etc.) via Constructor.

### Endpoints & DTOs

1.  **POST /api/profiles**
    -   Request Body: `CreateProfileRequestDTO` (`email`, `displayName`, `bio`)
    -   Response: 201 Created + Location Header.
    -   Error Mapping:
        -   Validation Fail (Jakarta): 400 Bad Request.
        -   Domain Exception (InvalidEmail, BioTooLong): 400 Bad Request.
        -   Duplicate Email: 409 Conflict.

2.  **GET /api/profiles/{id}**
    -   Path Variable: `String id` -> Converted to UUID/ProfileId in Service or Controller? Best practice: Convert in Controller or a Converter. Let's do it in Controller for clarity.
    -   Response: 200 OK with `ProfileResponseDTO`.
    -   Error Mapping:
        -   Profile Not Found: 404 Not Found.

3.  **PUT /api/profiles/{id}**
    -   Path Variable: `String id`.
    -   Request Body: `UpdateProfileRequestDTO` (`email`, `displayName`, `bio`). Note: Spec shows PUT body contains email. Even though ID is in URL, the payload might contain the "current" email for validation purposes or just redundant data. We will map all fields.
    -   Response: 200 OK.
    -   Error Mapping:
        -   Profile Not Found: 404 Not Found.
        -   Validation Fail: 400 Bad Request.
        -   Duplicate Email (if changed): 409 Conflict.

4.  **DELETE /api/profiles/{id}**
    -   Path Variable: `String id`.
    -   Response: 204 No Content.
    -   Error Mapping:
        -   Profile Not Found: 404 Not Found.

### Exception Handler

-   **`GlobalExceptionHandler`** (`@RestControllerAdvice`)
    -   Handle `InvalidEmailException`, `BioTooLongException` -> 400.
    -   Handle `DuplicateEmailException` -> 409.
    -   Handle `ProfileNotFoundException` -> 404.
    -   Handle `MethodArgumentNotValidException` -> 400.
    -   *Crucial*: Do NOT catch generic `Exception.class` to avoid masking Spring's own 404/500 errors for unmapped paths or internal server errors that aren't our domain exceptions.

## 4. Persistence Adapter

### JPA Entity

-   **`ProfileEntity`**
    -   Implements `Persistable<UUID>`? Spec mentions UUID generation strategy issues. Since we use Domain-generated IDs (or App-layer generated), we should handle the "new vs existing" state manually if using `save()`.
    -   Fields: `id` (@Id, @Column(unique=true)), `email` (@Column(unique=true)), `displayName`, `bio`.
    -   State Management: To support `save()` correctly without `@GeneratedValue` conflicts when ID is provided by app/domain:
        -   Add `@Transient private boolean isNew = true;`
        -   Implement `isNew()` returning this field.
        -   Use `@PostLoad` and `@PostPersist` to set `isNew = false`.
        -   This ensures `repository.save(entity)` works as an insert when `isNew` is true, and update otherwise. However, since we are doing explicit Find then Update in some cases, or Create directly, this pattern helps. For Delete, we just need the ID.

### Repository

-   **`ProfileJpaRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    -   Methods: `Optional<ProfileEntity> findById(UUID id)`, `boolean existsByEmail(String email)`, `void deleteById(UUID id)`.

### Port Implementation & Converter

-   **`JpaSaveProfilePort`**: Implements `SaveProfilePort`. Converts Domain `Profile` -> JPA `ProfileEntity`. Calls `repo.saveAndFlush()`. Catches `DataIntegrityViolationException` (specifically unique constraint on email) and throws `DuplicateEmailException`.
-   **`JpaFindProfileByIdPort`**: Implements `FindProfileByIdPort`. Calls `repo.findById()`. Converts Entity back to Domain `Profile`. Returns empty Optional if not found.
-   **`JpaValidateEmailUniquePort`**: Implements `ValidateEmailUniquePort`. Uses a custom query method in Repo like `existsByEmailAndIdNot(email, excludeId)`.
-   **`JpaRemoveProfilePort`**: Implements `RemoveProfilePort`. Calls `repo.deleteById(id)`. Checks existence first? Or let it throw? Spec says 404 for deleting non-existent. So we should check existence or catch specific exception. Better to use `findById` then `delete` to ensure atomicity and correct error code, or rely on the Use Case having already verified existence via `Get/Update` flow? No, Delete is independent. The Use Case will call FindFirst, if absent throw Exception. So Port just needs to delete.

## 5. Bootstrap

### Configuration

-   **`UseCaseConfiguration`** (`@Configuration`)
    -   Defines Beans for all Inbound Ports implementations:
        -   `CreateProfileService` (depends on ports)
        -   `GetProfileService`
        -   `UpdateProfileService`
        -   `DeleteProfileService`
    -   *Note*: Do NOT define beans in Application module classes. Only here.

### Cucumber Steps

-   Located in `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/`
-   Class: `ProfileSteps`
-   Uses `TestRestTemplate` to interact with the running Spring Boot application context (which includes Testcontainers DB).
-   Features file: `src/test/resources/features/profile-crud.feature`.

## 6. Testing Strategy

| Layer | Framework | Key Tests | Notes |
| :--- | :--- | :--- | :--- |
| **Domain** | JUnit 5 + AssertJ | - Email validation logic<br>- Bio length limit<br>- Entity state updates | Pure Java, no Spring. Mock nothing needed if pure logic. |
| **Application** | JUnit 5 + Mockito | - Create: Success, Duplicate Email, Invalid Input<br>- Get: Found, Not Found<br>- Update: Success, Not Found, Duplicate Email change<br>- Delete: Success, Not Found | Mock Outbound Ports (`Save`, `Find`, `Validate`). Verify interactions and return values. |
| **Web Adapter** | `@WebMvcTest` | - POST /api/profiles status codes & headers<br>- GET /api/profiles/{id} response body<br>- PUT /api/profiles/{id} update verification<br>- DELETE /api/profiles/{id} 204/404<br>- Exception handling mapping | Mock Inbound Ports. Use `MockMvcTester`. Verify JSON serialization/deserialization. |
| **Persistence** | `@DataJpaTest` + Testcontainers | - Save with unique email (success)<br>- Save with duplicate email (exception thrown)<br>- Find by ID (found/not found)<br>- Delete existence check | Real PostgreSQL via Testcontainers. Import specific Port implementations to test them directly or use Repository tests. Ensure `saveAndFlush` triggers constraints. |
| **Integration/BDD** | Cucumber + Spring Boot | End-to-end scenarios from Spec US-1 to US-4 | Uses real DB, real HTTP endpoints. Validates full stack behavior. |

## 7. Class List

| Module | Path (Relative) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/ProfileId.java` | Value Object for Profile UUID. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java` | Value Object for Email validation and storage. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayName.java` | Value Object for Display Name. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Bio.java` | Value Object for Bio content with length limits. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/Profile.java` | Aggregate Root representing a User's Personal Profile. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | Exception for invalid email format. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | Exception for duplicate email constraint violation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | Exception when profile is not found by ID. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/BioTooLongException.java` | Exception when bio exceeds max length. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port interface for creating profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port interface for retrieving profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port interface for updating profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port interface for deleting profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/dto/ProfileResponseDTO.java` | DTO returned to Web layer for GET responses. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/CreateProfileCommand.java` | Command object for Create Use Case inputs. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/UpdateProfileCommand.java` | Command object for Update Use Case inputs. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port for saving profile data. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port for finding a profile by ID. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ValidateEmailUniquePort.java` | Outbound Port for checking email uniqueness. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java` | Outbound Port for removing a profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDTO.java` | DTO for POST request body. |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDTO.java` | DTO for PUT request body. |
| Adapter-Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java` | Handles domain and validation exceptions, maps to HTTP status codes. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity mapping Profile data to DB table. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java` | Spring Data Repository interface. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaFindProfileByIdPort.java` | Implements FindProfileByIdPort using JPA. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaValidateEmailUniquePort.java` | Implements ValidateEmailUniquePort using JPA. |
| Adapter-Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaRemoveProfilePort.java` | Implements RemoveProfilePort using JPA. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Spring Configuration to wire Application Use Cases and Ports. |
| Bootstrap (Test) | `bootstrap/src/test/resources/features/profile-crud.feature` | Cucumber Feature file for BDD scenarios. |
| Bootstrap (Test) | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/stepdefs/ProfileSteps.java` | Cucumber Step Definitions interacting with the API. |
