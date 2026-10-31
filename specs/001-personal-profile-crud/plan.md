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
        - `update(displayName, bio)`：更新暱稱與簡介。注意：根據 REST PUT 語義，通常全量更新，但 email 作為唯一識別聯絡方式，在「修改」情境下是否允許變更需釐清。根據 Spec US-3 與驗收場景，PUT 請求包含 email 欄位，但若該 email 已存在於其他記錄則應失敗。為簡化領域邏輯，我們假設 PUT 操作主要用於更新 displayName 和 bio，若 email 發生變化且與其他記錄衝突，由 Port 或 Application Layer 攔截。*修正策略*：鑑於 Spec 中 PUT 包含 email，且驗證條件提到「嘗試更新不存在的...404」以及「成功更新...再次查詢變更為新值」，但未明確說明 PUT 時能否改 email。然而，RESTful 慣例中 PUT /{id} 通常意味著替換資源。若允許改 email，必須檢查 uniqueness。為符合 SOLID 單一職責，Domain Entity 僅負責狀態一致性（如 bio 長度），Unique Constraint 的檢查放在 Persistence Port 層處理較佳，因為 Domain 不應依賴資料庫約束。因此，`Profile` 實體本身不包含 `updateEmail` 方法，而是提供 `updateDisplayNameAndBio`。若應用層收到 PUT 要求改變 email，它會先呼叫 Outbound Port 檢查唯一性，再決定是否調用 Domain 方法或拋出例外。**但在本設計中，為了保持簡單並符合 Spec US-3 的 JSON payload**，我們將 `Profile` 視為不可變或部分可變。考慮到 Spec 未明確禁止修改 Email，但強調了 Email 的唯一性，最安全的做法是：**PUT 操作主要用於更新 displayName 和 bio**。如果用戶想改 email，通常需要重新註冊或透過專門的 API。*然而*，查看驗收場景 "成功更新個人資料"，JSON 中包含 `"email": "old-email@test.com"`，這暗示 email 可能只是用來確認身份或不變動。若我們嚴格遵循 REST，PUT 應包含所有欄位。
    - **決策**：為了避免複雜的領域邏輯與資料庫約束耦合，我們定義 `Profile` 為具有 `id`, `email`, `displayName`, `bio` 的狀態對象。Domain Service 或 Use Case 負責協調。由於 Spec 沒有明確說明「修改 Email」的流程，且通常修改 Email 涉及驗證流程，這裡假設 **PUT /{id} 僅允許更新 `displayName` 和 `bio`**，或者如果傳入的 email 與現有不同，則視為錯誤（400 Bad Request）或忽略（只更新其他字段）。*再審視 Spec*：Spec 說 "全量更新指定 ID 的資料"。這意味著 JSON 中的 email 應該被尊重嗎？如果系統設計嚴謹，改變 email 應該是一個獨立的操作或有特殊處理。鑑於這是 CRUD 基礎功能，我們將採用較保守的策略：**PUT 操作接受 displayName 和 bio 的更新。如果請求中的 email 與當前記錄不同，應用層將拒絕該請求並返回 400 Bad Request（因為 email 是唯一標識，不應在 PUT 中隨意變更而不經過驗證流程）**。這樣符合單一職責，也避免了領域模型過於龐大。
    - *修正*：為了更貼近一般 REST API 的設計，許多系統允許 PUT 時包含所有欄位，若 email 變動則檢查唯一性。但考慮到本專案是 Spec-Driven Development，且未提及 Email 變更流程，我們將在 Application Layer 實作一個簡單的檢查：如果 PUT 請求中的 email 不等於資料庫中現有的 email，則拋出 `EmailChangeNotAllowedException` (Domain Exception) 或直接由 Port 層處理 uniqueness。為簡化，**我們假設 PUT /{id} 僅用於更新非識別欄位 (displayName, bio)**。若前端傳入不同的 email，後端應忽略或報錯。這裡選擇 **報錯 400 Bad Request**，提示 "Email cannot be changed via this endpoint"。

### Domain Exceptions

- `ProfileNotFoundException`：當查詢或刪除找不到對應 ID 時拋出。
- `DuplicateEmailException`：當儲存資料時發現電子郵件已存在時拋出。
- `InvalidEmailFormatException`：當 Email Value Object 建立失敗時拋出（也可在 Web Adapter DTO 驗證階段攔截，但 Domain 層保留此例外以確保領域完整性）。
- `BioTooLongException`：簡介超過長度限制。

## 2. Use Cases & Ports

### Inbound Ports (Interfaces in `application.port.in`)

1.  **CreateProfileUseCase**
    -   `void execute(CreateProfileCommand command)`
        -   Command: `email`, `displayName`, `bio`.

2.  **GetProfileUseCase**
    -   `Optional<Profile> execute(GetProfileQuery query)`
        -   Query: `profileId`.

3.  **UpdateProfileUseCase**
    -   `void execute(UpdateProfileCommand command)`
        -   Command: `profileId`, `displayName`, `bio`. (註：不包含 email)

4.  **DeleteProfileUseCase**
    -   `void execute(DeleteProfileCommand command)`
        -   Command: `profileId`.

### Outbound Ports (Interfaces in `application.port.out`)

1.  **SaveProfilePort**
    -   `Profile save(Profile profile)`
    -   *注意*：需處理唯一性約束衝突並轉換為 Domain Exception。

2.  **LoadProfilePort**
    -   `Optional<Profile> findById(ProfileId id)`

3.  **ExistsByEmailPort** (可選，或合併在 SaveProfilePort 內部檢查)
    -   為了符合 SOLID 介面分離原則，若 Save 邏輯複雜，可單獨定義。但在此簡單場景下，我們讓 `SaveProfilePort` 負責持久化，並在發生 DataIntegrityViolationException 時由 Port 實作層捕捉並拋出 `DuplicateEmailException`。因此不需要額外的 Exists Port。

### Use Case Implementations (in `application.usecase`)

-   `CreateProfileService`：實現 `CreateProfileUseCase`。驗證輸入（呼叫 Value Object 建構子），建立 Profile Entity，呼叫 `saveProfilePort.save()`。
-   `GetProfileService`：實現 `GetProfileUseCase`。呼叫 `loadProfilePort.findById()`，回傳 Optional。
-   `UpdateProfileService`：實現 `UpdateProfileUseCase`。先透過 `loadProfilePort` 載入現有 Profile，確認存在後，調用 Profile 的更新方法，再呼叫 `saveProfilePort.save()`。
-   `DeleteProfileService`：實現 `DeleteProfileUseCase`。先確認存在 (`findById`)，再刪除（或直接呼叫 save/delete port）。由於 JPA 的 delete 通常依賴 ID，我們可以有一個 `deleteById` 在 Port 中，或者 load 後 mark for deletion。為簡潔，Outbound Port 可定義 `void delete(ProfileId id)`。

## 3. Web Adapter

### Controller

-   **ProfileController** (`@RestController`, path: `/api/profiles`)
    -   `POST /{id}` -> No, POST is collection. `POST /` (mapped to `/api/profiles`).
        -   Method: `create(@Valid @RequestBody CreateProfileRequest request)`
        -   Returns: `ResponseEntity<Void>` with Location header and status 201.
    -   `GET /{id}`
        -   Method: `get(@PathVariable("id") UUID id)`
        -   Returns: `ResponseEntity<GetProfileResponse>`. Status 200 or 404.
    -   `PUT /{id}`
        -   Method: `update(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)`
        -   Returns: `ResponseEntity<Void>` with status 200.
    -   `DELETE /{id}`
        -   Method: `delete(@PathVariable("id") UUID id)`
        -   Returns: `ResponseEntity<Void>` with status 204 or 404.

### DTOs

-   **CreateProfileRequest**: `email` (@Email), `displayName` (optional/nullable), `bio` (optional).
-   **UpdateProfileRequest**: `displayName`, `bio`. (No email field allowed in PUT payload for this design decision to simplify uniqueness logic, or if included, must match existing). *Refinement*: To strictly follow "Full Update", we might include email. But given the complexity of changing unique keys in REST without side effects, and typical CRUD specs, I will stick to updating displayName/bio only via PUT. If the spec implies email can be changed, it would require a more complex flow. Let's assume standard behavior: PUT updates non-key fields.
-   **GetProfileResponse**: `id` (UUID string), `email`, `displayName`, `bio`.

### Error Handling (`@RestControllerAdvice`)

-   `MethodArgumentNotValidException` -> 400 Bad Request.
-   `HttpMessageNotReadableException` -> 400 Bad Request.
-   `DuplicateEmailException` (Domain) -> 409 Conflict.
-   `ProfileNotFoundException` (Domain) -> 404 Not Found.
-   Other Exceptions -> 500 Internal Server Error (handled by default Spring handler, but explicit advice is good practice; however, per rules, do NOT catch generic Exception).

## 4. Persistence Adapter

### JPA Entity

-   **ProfileEntity** (`@Entity`)
    -   Fields: `id` (String/UUID, Primary Key), `email` (Unique Constraint), `display_name`, `bio`.
    -   Implements `Persistable<String>`? No, since ID is generated externally or passed in, we use `@Id String id`. To handle "new vs existing" for save(), if we pass the UUID from Domain, it's not auto-generated. So `save()` works fine without `Persistable` logic if we always provide an ID. However, Spec US-1 says "Create", implying new record. If we generate UUID in Application Layer before calling Port, then simple `save(entity)` works. We don't need `Persistable` unless using DB sequence/auto-increment. Let's stick to UUID provided by App Layer.
    -   *Correction*: The prompt mentions a trap about `Persistable` and `isNew`. This applies when IDs are domain-generated but JPA tries to treat them as transient or managed incorrectly. Since we will likely generate UUID in the Controller or Use Case, the entity has a non-null ID. Standard Spring Data JPA `save()` handles this correctly (insert if no matching PK exists). We just need to ensure unique constraint violation throws correct exception.

### Repository

-   **ProfileJpaRepository** (`extends JpaRepository<ProfileEntity, String>`)
    -   Method: `Optional<ProfileEntity> findById(String id)`.
    -   Constraint: Unique index on `email`.

### Port Implementation

-   **JpaSaveProfilePort** (`implements SaveProfilePort`)
    -   Injects `ProfileJpaRepository`.
    -   Converts Domain `Profile` -> `ProfileEntity`.
    -   Calls `repository.saveAndFlush(entity)`.
    -   Catches `DataIntegrityViolationException`, checks for email uniqueness error code/message, re-throws `DuplicateEmailException`.
-   **JpaLoadProfilePort** (`implements LoadProfilePort`)
    -   Injects `ProfileJpaRepository`.
    -   Calls `findById(id)`.
    -   Converts `ProfileEntity` -> Domain `Profile`. Returns `Optional.empty()` if not found.
-   **JpaDeleteProfilePort** (`implements DeleteProfilePort`? Or add delete method to existing port?)
    -   Let's create a separate interface or extend an existing one? Better to have specific ports per SOLID. But usually, CRUD repos are combined. However, in Hexagonal, we define interfaces by use case need. So `DeleteProfilePort` with `void deleteById(ProfileId id)` is cleanest.
    -   Wait, the prompt says "outbound port 介面一律以 Port 結尾". And "同一模組內不得有兩個同名型別".
    -   I will define:
        -   `SaveProfilePort`: `save(Profile profile)`
        -   `LoadProfilePort`: `Optional<Profile> findById(ProfileId id)`
        -   `DeleteProfilePort`: `void deleteById(ProfileId id)`

### Converter/Assembler

-   Logic can be inline in Ports or separated into `ProfileConverter`. For simplicity and testability, inline conversion methods within the Port implementation classes are acceptable for this scale, or small static helper class `ProfileMapper`. Given the strict package structure, putting mapper logic inside the Adapter module is fine. We'll put it in `adapter.persistence` as `ProfileEntityToDomainConverter` etc., or just private methods in the Port implementations. Private methods are better for encapsulation if not reused elsewhere. Let's use private methods in the Port Implementations to keep dependencies low.

## 5. Bootstrap

-   **UseCaseConfiguration** (`@Configuration`)
    -   Defines beans for all Use Case Services (e.g., `createProfileService`, `getProfileService`).
    -   Injects necessary ports (which are also defined as beans here).
    -   Defines beans for Port Implementations (JPA versions), injecting Repositories.
    -   *Note*: Do NOT add `@Component` on services. Only define them via `@Bean` in this configuration class.

## 6. Testing Strategy

-   **Unit Tests (JUnit + Mockito)**:
    -   `CreateProfileServiceTest`: Verify command validation, port calls, exception mapping.
    -   `GetProfileServiceTest`: Verify loading and optional handling.
    -   `UpdateProfileServiceTest`: Verify existence check, update call, save call.
    -   `DeleteProfileServiceTest`: Verify existence check before delete.
    -   Domain Object Tests: Value object immutability and invariant checks.
-   **Integration Tests**:
    -   `ProfileControllerTest` (`@WebMvcTest`): Mock controllers, verify HTTP status codes, headers, JSON structure.
    -   `JpaSaveProfilePortTest` (`@DataJpaTest` + Testcontainers): Verify DB operations, unique constraint violation handling.
    -   `JpaLoadProfilePortTest` (`@DataJpaTest` + Testcontainers): Verify load by ID.
-   **BDD/Cucumber**:
    -   Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`.
    -   Step Definitions: In `com.github.chunpingwang.ollamaagentoutcome.bdd`, using `TestRestTemplate` to hit the actual API running with Testcontainers.

## 7. Class List

| Category | Path (Relative) | Responsibility |
| :--- | :--- | :--- |
| **Domain** | `domain/.../domain/valueobject/Email.java` | Email value object with validation logic. |
| **Domain** | `domain/.../domain/valueobject/DisplayName.java` | Display name value object. |
| **Domain** | `domain/.../domain/valueobject/Bio.java` | Bio value object with length check. |
| **Domain** | `domain/.../domain/valueobject/ProfileId.java` | Profile identifier UUID wrapper. |
| **Domain** | `domain/.../domain/entity/Profile.java` | Core domain entity, holds state and basic update behavior. |
| **Domain** | `domain/.../domain/exception/ProfileNotFoundException.java` | Exception for missing profile. |
| **Domain** | `domain/.../domain/exception/DuplicateEmailException.java` | Exception for duplicate email constraint violation. |
| **Application** | `application/.../port/in/CreateProfileUseCase.java` | Inbound port interface for creating a profile. |
| **Application** | `application/.../usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase. |
| **Application** | `application/.../command/CreateProfileCommand.java` | Command object for creation request data. |
| **Application** | `application/.../port/in/GetProfileUseCase.java` | Inbound port interface for getting a profile. |
| **Application** | `application/.../query/GetProfileQuery.java` | Query object containing ID. |
| **Application** | `application/.../usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| **Application** | `application/.../port/in/UpdateProfileUseCase.java` | Inbound port interface for updating a profile. |
| **Application** | `application/.../command/UpdateProfileCommand.java` | Command object for update request data (ID, displayName, bio). |
| **Application** | `application/.../usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase. |
| **Application** | `application/.../port/in/DeleteProfileUseCase.java` | Inbound port interface for deleting a profile. |
| **Application** | `application/.../command/DeleteProfileCommand.java` | Command object containing ID to delete. |
| **Application** | `application/.../usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| **Application** | `application/.../port/out/SaveProfilePort.java` | Outbound port for saving profiles. |
| **Application** | `application/.../port/out/LoadProfilePort.java` | Outbound port for loading profiles by ID. |
| **Application** | `application/.../port/out/DeleteProfilePort.java` | Outbound port for deleting profiles by ID. |
| **Web Adapter** | `adapter-web/.../web/controller/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| **Web Adapter** | `adapter-web/.../web/dto/CreateProfileRequest.java` | DTO for POST request body. |
| **Web Adapter** | `adapter-web/.../web/dto/UpdateProfileRequest.java` | DTO for PUT request body. |
| **Web Adapter** | `adapter-web/.../web/dto/GetProfileResponse.java` | DTO for GET response body. |
| **Web Adapter** | `adapter-web/.../web/advice/GlobalExceptionHandler.java` | Handles exceptions and maps to HTTP status codes. |
| **Persistence Adapter** | `adapter-persistence/.../persistence/entity/ProfileEntity.java` | JPA Entity mapping to database table. |
| **Persistence Adapter** | `adapter-persistence/.../persistence/repository/ProfileJpaRepository.java` | Spring Data Repository interface. |
| **Persistence Adapter** | `adapter-persistence/.../persistence/port/JpaSaveProfilePort.java` | Implementation of SaveProfilePort using JPA. |
| **Persistence Adapter** | `adapter-persistence/.../persistence/port/JpaLoadProfilePort.java` | Implementation of LoadProfilePort using JPA. |
| **Persistence Adapter** | `adapter-persistence/.../persistence/port/JpaDeleteProfilePort.java` | Implementation of DeleteProfilePort using JPA. |
| **Bootstrap** | `bootstrap/.../bootstrap/config/UseCaseConfiguration.java` | Spring Configuration wiring Use Cases and Ports as Beans. |
