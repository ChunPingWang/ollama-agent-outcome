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
    - **決策**：為了避免複雜的領域邏輯與資料庫約束耦合，我們定義 `Profile` 為具有 `id`, `email`, `displayName`, `bio` 的狀態對象。Domain Service 或 Use Case 負責協調。由於 Spec 沒有明確說明「修改 Email」的流程，且通常 Email 變更涉及驗證流程，本 Plan 假設 **PUT /{id} 僅允許更新 `displayName` 和 `bio`**。如果請求中的 email 與現有記錄不同，應用層將拒絕該請求（因為這可能被視為試圖竄改身份），或者更嚴格的解釋是：Spec 中的 PUT payload 包含 email 是為了完整性，但在業務邏輯上，Email 在建立後不可更改（Immutable）。*再次審視 Spec US-3*："想要更新我的個人資料（如暱稱或簡介）"。括號內的例子暗示主要更新這兩項。因此，我們將設計為：**Email 一旦建立即不可變**。若 PUT 請求中的 email 與當前不符，拋出 `InvalidUpdateException` 或在 Application Layer 檢查並拒絕。

### Domain Exceptions

- **DuplicateEmailException**: 當嘗試創建或更新資料時，電子郵件已存在於系統中。
- **ProfileNotFoundException**: 當查詢、更新或删除的 Profile ID 不存在時。
- **InvalidEmailFormatException**: 當提供的 Email 格式不合法時。
- **BioTooLongException**: 當 Bio 長度超過限制時。

## 2. Use Cases & Ports (Application Layer)

### Inbound Ports (Interfaces)

1.  **CreateProfileUseCase**
    -   `void execute(CreateProfileCommand command)`
        -   Command: `email`, `displayName`, `bio`.

2.  **GetProfileUseCase**
    -   `Optional<Profile> execute(GetProfileQuery query)`
        -   Query: `id` (UUID).

3.  **UpdateProfileUseCase**
    -   `void execute(UpdateProfileCommand command)`
        -   Command: `id`, `displayName`, `bio`. *(註：不包含 email，因假設 Email 不可變)*

4.  **DeleteProfileUseCase**
    -   `void execute(DeleteProfileCommand command)`
        -   Command: `id`.

### Outbound Ports (Interfaces)

1.  **SaveProfilePort**
    -   `Profile save(Profile profile)`
    -   *注意*：需處理唯一性約束衝突並拋出 Domain Exception。

2.  **LoadProfilePort**
    -   `Optional<Profile> findById(UUID id)`

*(註：為符合 SOLID 介面分離原則，將 Load 和 Save 分開。若性能要求高可合併，但此規模建議分離以清晰職責。)*

## 3. Web Adapter (REST API)

-   **Controller**: `ProfileRestController`
-   **Base Path**: `/api/profiles`

| Method | Path | DTO / Input | Success Response | Error Handling |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/` | `CreateProfileRequest` (`email`, `displayName`, `bio`) | 201 Created + Location Header | 400 Bad Request (Validation/Format), 409 Conflict (Duplicate Email) |
| GET | `/{id}` | Path Variable `id` (UUID) | 200 OK + `GetProfileResponse` | 404 Not Found |
| PUT | `/{id}` | Path Variable `id`, Body `UpdateProfileRequest` (`displayName`, `bio`) | 200 OK | 400 Bad Request, 404 Not Found |
| DELETE | `/{id}` | Path Variable `id` | 204 No Content | 404 Not Found |

-   **DTOs**:
    -   `CreateProfileRequest`: record with `@Email String email`, `String displayName`, `String bio`.
    -   `GetProfileResponse`: record with `UUID id`, `String email`, `String displayName`, `String bio`.
    -   `UpdateProfileRequest`: record with `String displayName`, `String bio`. *(不包含 email)*
    -   *錯誤回應*: 統一使用 `ErrorResponse` record (`code`, `message`).

## 4. Persistence Adapter (JPA)

-   **Entity**: `ProfileEntity`
    -   Implements `Persistable<UUID>`.
    -   Fields: `uuid id`, `String email`, `String displayName`, `String bio`.
    -   Constraints: `@Column(unique = true)` on email.
    -   Lifecycle: Handle `isNew()` flag for correct insert/update behavior to avoid optimistic locking issues or incorrect updates when using generated IDs vs assigned IDs. Since we use UUID from client/domain, it's an "assigned ID" strategy. We must ensure JPA treats new entities as inserts. Using `Persistable` interface is recommended per system prompt rules if domain generates ID. However, since Spring Data JPA can handle `save()` correctly if the entity has a non-null ID and isn't managed, simply having a constructor that sets `id` works. But to be safe against `ObjectOptimisticLockingFailureException` mentioned in traps, implementing `Persistable` with `isNew` logic is safer. Actually, for Assigned IDs, standard JPA `save` usually does INSERT if not found. The trap mentions "Domain若自行產生識別碼... Entity不可同時使用 @GeneratedValue". So we do NOT use `@GeneratedValue`. We rely on the passed-in UUID.

-   **Repository**: `ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID>`
    -   Method: `Optional<ProfileEntity> findByEmail(String email)` (for duplicate check).

-   **Port Implementation**: `JpaSaveProfilePort`, `JpaLoadProfilePort`
    -   Convert between `Profile` (Domain) and `ProfileEntity` (Infrastructure).
    -   Catch `DataIntegrityViolationException` -> throw `DuplicateEmailException`.
    -   Use `saveAndFlush()` to ensure constraints are checked immediately within transactional tests.

## 5. Bootstrap Configuration

-   **Class**: `UseCaseConfiguration` (`@Configuration`)
    -   Defines beans for all Inbound Ports implementations (e.g., `CreateProfileService`).
    -   Injects Outbound Ports into Services via constructor.
    -   *Note*: Do not define Repository or Port beans here; let Spring Data scan them or define in a separate persistence config if needed, but typically Repositories are auto-detected by component scanning or defined in their own module's configuration. Since modules are separated, `adapter-persistence` should expose its repository bean, or we use `@EnableJpaRepositories`. The system prompt says bootstrap assembles use case beans. We will assume standard Spring Boot auto-config handles JPA repos, and we only wire the Application Layer services here.

## 6. Testing Strategy

### Unit Tests (JUnit + Mockito + AssertJ)
-   **Domain**: Test Value Object immutability and validation rules (e.g., invalid email format throws exception).
-   **Application**: 
    -   `CreateProfileServiceTest`: Verify it calls `SavePort`, handles duplicate exceptions correctly.
    -   `GetProfileServiceTest`: Verify it returns Optional.empty() when port returns empty.
    -   `UpdateProfileServiceTest`: Verify it loads entity, updates fields, saves back. Check 404 handling.
    -   `DeleteProfileServiceTest`: Verify load then delete flow.

### Integration Tests (Web Adapter)
-   **Class**: `ProfileRestControllerIT` (`@WebMvcTest`)
    -   Mock all Inbound Ports.
    -   Verify HTTP status codes, headers (Location), and JSON structure for success/failure cases.
    -   Verify error message content for 400/409/404.

### Persistence Tests
-   **Class**: `JpaProfileRepositoryIT` (`@DataJpaTest` + Testcontainers PostgreSQL)
    -   Test Entity mapping.
    -   Test Unique Constraint on Email (catching `DataIntegrityViolationException`).
    -   Test Save/Load round-trip with UUIDs.

### BDD / Cucumber Tests
-   **Feature File**: `src/test/resources/features/personal-profile-crud.feature`
    -   Language: `zh-TW`.
    -   Steps use `TestRestTemplate` to hit the actual running API (with embedded DB via Testcontainers).
    -   Scenarios map directly to Spec acceptance criteria.

## 7. Class List

| Category | Full Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileId.java` | Value Object for Profile ID. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Email.java` | Value Object for Email validation. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/DisplayName.java` | Value Object for Display Name. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Bio.java` | Value Object for Bio content. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Profile.java` | Aggregate Root / Entity holding profile data. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/DuplicateEmailException.java` | Exception for duplicate email errors. |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/service/ProfileNotFoundException.java` | Exception for missing profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port interface for creating profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port interface for getting a profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port interface for updating a profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port interface for deleting a profile. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port for saving profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port for loading profiles. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Implementation of CreateProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Implementation of GetProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Implementation of UpdateProfileUseCase. |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Implementation of DeleteProfileUseCase. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileRestController.java` | REST Controller handling HTTP requests. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO for creating profile input. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/GetProfileResponse.java` | DTO for getting profile output. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO for updating profile input. |
| Adapter Web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/advice/GlobalExceptionHandler.java` | Handles domain exceptions and converts to HTTP responses. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity mapping. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java` | Spring Data Repository interface. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Implements SaveProfilePort using JPA. |
| Adapter Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Implements LoadProfilePort using JPA. |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Wires Application Layer beans (Services). |
