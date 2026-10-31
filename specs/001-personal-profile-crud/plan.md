# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了 Spec 001 的技術實現細節，遵循六角形架構（Hexagonal Architecture），確保領域邏輯獨立於基礎設施，並透過明確的 Port 進行通訊。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

所有實體均位於 `com.github.chunpingwang.ollamaagentoutcome.domain.profile` 套件下。

#### `ProfileId` (Value Object / Record)
- **描述**：代表個人資料的唯一識別碼。使用 UUID 格式。
- **屬性**：`String value`
- **不變條件**：值不得為 null 或空白字串；必須符合 UUID 格式（可選驗證，建議在建立時由 Domain Service 或 Factory 確保）。

#### `Email` (Value Object / Record)
- **描述**：使用者的電子郵件地址。
- **屬性**：`String address`
- **不變條件**：
    - 不得為 null 或空白。
    - 長度限制：1 ~ 254 個字符。
    - 格式需符合 RFC 5322 基本規範（至少包含 `@` 與域名部分）。
- **行為**：提供靜態工廠方法 `of(String raw)` 進行驗證，失敗拋出 `InvalidEmailException`。

#### `DisplayName` (Value Object / Record)
- **描述**：使用者公開顯示的名稱。
- **屬性**：`String name`
- **不變條件**：
    - 不得為 null。
    - 允許空白字元，但總長度不得超過 50 個字符。
    - 若輸入僅含空白字元，視為無效（可選策略：在此版本中允許純空白，但若 spec 要求嚴謹可改為 trim 後檢查非空）。根據 Spec "允許空白"，我們只嚴格限制長度上限。
- **行為**：提供靜態工廠方法 `of(String raw)` 進行驗證，失敗拋出 `InvalidDisplayNameException`。

#### `Bio` (Value Object / Record)
- **描述**：使用者的簡介。
- **屬性**：`String content`
- **不變條件**：
    - 可為 null（表示未填寫）。
    - 若不為 null，長度不得超過 500 個字符。
- **行為**：建構子或 setter 負責截斷或拒絕過長內容？建議在 Domain Service 層面處理轉換，或在 VO 建構時拋出例外以確保一致性。此處定義為不可變，若需更新則建立新實例。

#### `Profile` (Entity / Class)
- **描述**：核心領域實體，聚合上述 Value Objects。
- **狀態**：
    - `id: ProfileId`
    - `email: Email`
    - `displayName: DisplayName`
    - `bio: Bio` (nullable)
    - `createdAt: Instant` (系統時間，建立時設定)
    - `updatedAt: Instant` (最後修改時間)
- **業務規則與行為**：
    - `update(DisplayName newDisplayName, Bio newBio)`：僅允許更新顯示名稱與簡介。**電子郵件一旦建立不可更改**（根據常見實務與 Spec "Update full profile" 雖有 email 欄位，但通常 ID 對應的記錄其主鍵識別如 Email 不應隨意變更，除非是重新註冊流程。然而 Spec 場景中 PUT 包含了 email 字段。為了符合 RESTful 全量更新的語義且避免複雜性，我們假設本系統的「更新」是指替換該 ID 下的所有公開資料，包括 Email？或者更嚴謹地說，Email 作為唯一憑證在 CRUD 中通常不允許透過 Update API 改變，否則會破壞索引或關聯。鑑於 Spec 未明確說明 Email 是否可變，但一般設計原則建議 Email 為 Immutable Key 或在特定權限下才可改。考慮到 Spec 場景 "成功更新個人資料" 中傳入了新的 email，我們將實作為：**允許更新 Email**。若新 Email 已存在，則由 Domain Service 層面拒絕並拋出例外）。
    - *修正*：仔細閱讀 Spec US-3 "修改我的顯示名稱或簡介"，並未提到可以修改 Email。但在 Gherkin 場景中 PUT body 確實包含 `email`。這可能是測試數據的慣例。為了安全起見，Domain Entity 提供 `updateAll(...)` 方法接受所有欄位，但業務規則驗證（如重複 Email）放在 Use Case/Service 層。
    - `delete()`：標記刪除狀態？Spec 要求硬刪除 (Hard Delete)。因此此方法可能僅用於觸發領域事件或直接由 Repository 物理刪除。在 Hexagonal 架構中，Entity 本身不應知道如何被持久化刪除，而是由 Application Service 呼叫 Port 來執行刪除動作。這裡我們保留實體完整性，刪除操作由外部驅動。

### Domain Exceptions
位於 `com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception`：
- `ProfileNotFoundException`：查詢時找不到資料。
- `DuplicateEmailException`：建立或更新時，電子郵件已被佔用。
- `InvalidEmailException`：Email 格式錯誤。
- `InvalidDisplayNameException`：Display Name 長度或內容無效。
- `InvalidBioException`：Bio 過長。

## 2. Use Cases & Ports

所有 Use Case 介面與實作位於 `application` 模組。

### Inbound Ports (`port.in`)

1. **`CreateProfileUseCase`**
   - `void execute(CreateProfileCommand command)`
   - Command DTO: `record CreateProfileCommand(String email, String displayName, String bio)`

2. **`GetProfileUseCase`**
   - `ProfileDto execute(GetProfileQuery query)` (或使用 Record 回傳)
   - Query DTO: `record GetProfileQuery(ProfileId id)`
   - Return: `record ProfileResponse(String id, String email, String displayName, String bio)`

3. **`UpdateProfileUseCase`**
   - `void execute(UpdateProfileCommand command)`
   - Command DTO: `record UpdateProfileCommand(ProfileId id, String email, String displayName, String bio)`

4. **`DeleteProfileUseCase`**
   - `void execute(DeleteProfileCommand command)`
   - Command DTO: `record DeleteProfileCommand(ProfileId id)`

### Outbound Ports (`port.out`)

為了符合 SOLID 的介面分離原則，我們將 Port 細分為讀取與寫入操作，儘管對於此小型系統合併也是可行的，但遵循規則建議分開或依行為分類。考慮到 CRUD 緊密相關，且 Spec 未要求複雜查詢，我們可以定義一個綜合性的 Port 或分別定義。為簡化並保持清晰：

1. **`LoadProfilePort`**
   - `Optional<Profile> findById(ProfileId id);`

2. **`SaveProfilePort`**
   - `Profile save(Profile profile);` (用於 Create 和 Update)

3. **`RemoveProfilePort`**
   - `void deleteById(ProfileId id);`

*(註：若希望更精簡，可將 Load/Save/Delete 合併為 `ProfileRepositoryPort`，但根據 "介面分離" 原則，分開較佳。然而，Spring Data JPA 通常提供單一 Repository。在 Adapter 層實作時，可以包裝 Spring Data Repository。在此設計中，我們定義三個小介面，由 Persistence Adapter 統一實現。)*

## 3. Web Adapter

位於 `adapter-web` 模組，套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.web.profile`。

### Controller: `ProfileController`
- `@RestController`
- `@RequestMapping("/api/profiles")`

#### Endpoints:
1. **POST `/api/profiles`**
   - Method: `createProfile(@Valid @RequestBody CreateProfileRequest request)`
   - Response: `201 Created`, Header `Location: /api/profiles/{id}`
   - DTO Input: `CreateProfileRequest(String email, String displayName, String bio)`
   
2. **GET `/api/profiles/{id}`**
   - Method: `getProfile(@PathVariable("id") String id)`
   - Response: `200 OK`, Body `GetProfileResponse`
   - DTO Output: `GetProfileResponse(String id, String email, String displayName, String bio)`

3. **PUT `/api/profiles/{id}`**
   - Method: `updateProfile(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
   - Response: `200 OK`, Body `UpdateProfileResponse` (或空 body? Spec 說 200 OK，通常回傳更新後的狀態較好，但 Gherkin 只檢查狀態碼和資源內容。為簡化，可回傳 200 與新資料，或僅 200。這裡選擇回傳更新後的 ProfileDto)
   - DTO Input/Output: 同 Create 結構的變體，或共用 Record。

4. **DELETE `/api/profiles/{id}`**
   - Method: `deleteProfile(@PathVariable("id") String id)`
   - Response: `204 No Content`

### Error Handling: `GlobalExceptionHandler`
位於 `adapter.web.exception`：
- `@RestControllerAdvice`
- 處理 `MethodArgumentNotValidException` → `400 Bad Request`
- 處理 `InvalidEmailException`, `InvalidDisplayNameException`, etc. → `400 Bad Request`
- 處理 `DuplicateEmailException` → `409 Conflict` (Spec 未明確指定重複 Email 的狀態碼，但 RESTful 慣例為 409；若嚴格遵循 Spec "錯誤訊息應指出..."，400 也可接受，但 409 更精確。考慮到 Spec 場景中沒有直接測試重複 Email 的建立（只有格式錯誤），我們暫定使用 409 以符合標準，或在 Domain Exception 映射時統一視為業務邏輯錯誤 400？鑑於 Spec 僅提到 "錯誤訊息"，為了安全通過測試，若 Cucumber 只檢查狀態碼，需確認。通常 Duplicate Key 是 409。若測試預期 400，則調整。此處設計採用 **409** 對於唯一性衝突，**400** 對於驗證失敗。)
    - *修正*：查看 Spec 場景 "建立資料時電子郵件格式錯誤" -> 400。沒有場景測試重複 Email。因此我們可以靈活選擇。建議：格式/長度問題 -> 400；業務規則違反（如重複）-> 409。
- 處理 `ProfileNotFoundException` → `404 Not Found`

## 4. Persistence Adapter

位於 `adapter-persistence` 模組，套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile`。

### JPA Entity: `ProfileEntity`
- `@Entity`
- `@Table(name = "profiles", uniqueConstraints = { @UniqueConstraint(columnNames = {"email"}) })`
- Fields:
    - `id`: `String` (對應 ProfileId.value)
    - `email`: `String`
    - `display_name`: `String`
    - `bio`: `String`
    - `created_at`: `Instant`
    - `updated_at`: `Instant`
- Converter: 使用 `AttributeConverter<ProfileId, String>` 等將 Value Objects 轉換為 DB 欄位類型。或者在 Port 實作層進行 Domain <-> Entity 的映射。為了保持 Entity 純淨且易於維護，建议在 Port 實現類中手動映射或使用 MapStruct（若允許）。根據 System Prompt "不使用 Lombok" 和標準 Java，我們將在 Port 實作中使用顯式映射方法。

### Repository Interface: `JpaProfileRepository`
- extends `CrudRepository<ProfileEntity, String>`
- `Optional<ProfileEntity> findByEmail(String email);` (用於檢查重複 Email)

### Port Implementations (`port.out`)
1. **`JpaLoadProfilePort`** implements `LoadProfilePort`
   - 依賴 `JpaProfileRepository`
   - `findById`: 查詢 Entity，若存在則轉換為 Domain `Profile`，否則回傳 `Optional.empty()`。

2. **`JpaSaveProfilePort`** implements `SaveProfilePort`
   - 依賴 `JpaProfileRepository`
   - `save`: 
     - 判斷是 Create 還是 Update？Domain `Profile` 可包含狀態或透過 ID 是否存在來判斷。
     - 更優雅的方式：Use Case 決定行為。但 Save Port 通常負責持久化。
     - 策略：在 `save` 之前，由 Use Case 確保資料正確性。Port 僅負責將 Domain Object 轉為 Entity 並呼叫 `repository.saveAndFlush(entity)`。
     - *注意*：Spec 要求硬刪除。Update 操作在 DB 層面通常是 UPDATE SQL。
   
3. **`JpaRemoveProfilePort`** implements `RemoveProfilePort`
   - 依賴 `JpaProfileRepository`
   - `deleteById`: 先檢查是否存在（可選，或由 Repository 拋出異常），然後執行物理刪除。為了符合 "404 if not found" 的應用層邏輯，建議在此 Port 中先 `findById`，若無則拋出 `EntityNotFoundException` (Spring Data) 或在 Application 層處理。最佳實踐是在 Application Service 層查詢後再調用 Delete，或者讓 Delete 方法回傳 boolean/void 並在失敗時拋出特定例外。這裡選擇：`deleteById` 直接呼叫 repo.delete(id)，若不存在會拋出 `EmptyResultDataAccessException`，需捕獲並轉換為自訂例外或讓上層處理。

### Converter / Mapper
- 位於同一套件下，作為靜態工具類或獨立類別 `ProfileMapper`，負責 `Domain.Profile <-> Persistence.ProfileEntity` 之間的雙向轉換。

## 5. Bootstrap

位於 `bootstrap` 模組，套件 `com.github.chunpingwang.ollamaagentoutcome.bootstrap.config.profile`。

### Configuration: `ProfileUseCaseConfiguration`
- `@Configuration`
- `@EnableTransactionManagement` (通常由 Spring Boot Autoconfig 處理，但明確聲明無害)
- Beans:
    - `createProfileUseCase()`: 實例化 `CreateProfileService`, 注入 `LoadProfilePort`(用於檢查重複), `SaveProfilePort`.
    - `getProfileUseCase()`: 實例化 `GetProfileService`, 注入 `LoadProfilePort`.
    - `updateProfileUseCase()`: 實例化 `UpdateProfileService`, 注入 `LoadProfilePort`, `SaveProfilePort`.
    - `deleteProfileUseCase()`: 實例化 `DeleteProfileService`, 注入 `RemoveProfilePort`.
    
*(註：為了避免循環依賴和過度設計，對於 Create/Update 中的 Email 唯一性檢查，可以在 Use Case Service 中先呼叫 LoadPort 查詢該 Email 是否屬於其他 ID，若是則拋出 DuplicateEmailException。)*

## 6. 測試策略

### Unit Tests (Domain & Application)
- **Location**: `domain/src/test/java/...` and `application/src/test/java/...`
- **Tools**: JUnit Jupiter, AssertJ, Mockito.
- **Focus**:
    - Domain VO validation rules (e.g., invalid email format).
    - Use Case business logic (e.g., preventing duplicate emails during update if the new email belongs to another user).
    - Mocking Outbound Ports (`@MockBean` or constructor injection with mocks in test setup).

### Integration Tests (Persistence Adapter)
- **Location**: `adapter-persistence/src/test/java/...`
- **Tools**: `@DataJpaTest`, Testcontainers PostgreSQL, `@AutoConfigureTestDatabase(replace = NONE)`.
- **Focus**:
    - Verify Entity mappings.
    - Verify Repository queries (findByEmail).
    - Verify Port implementations handle DB constraints correctly (UniqueConstraint violation -> Exception).

### Web Controller Tests
- **Location**: `adapter-web/src/test/java/...`
- **Tools**: `@WebMvcTest(ProfileController.class)`, `@MockitoBean` for Use Cases.
- **Focus**:
    - HTTP Status codes mapping.
    - Request/Response DTO serialization/deserialization.
    - Validation error responses (400).
    - Not Found handling (404).

### BDD / Cucumber Tests
- **Location**: `bootstrap/src/test/resources/features/profile.feature` and `bdd/ProfileSteps.java`
- **Tools**: Cucumber 8, Spring Boot Test Context, `TestRestTemplate`.
- **Feature File Name**: `profile-crud.feature`
- **Language**: zh-TW (`# language: zh-TW`)
- **Scenarios**: Map directly to Spec scenarios.
    - Scenario: "成功建立新的個人資料"
    - Scenario: "建立資料時電子郵件格式錯誤"
    - Scenario: "建立資料時顯示名稱過長"
    - Scenario: "查詢不存在的個人資料"
    - Scenario: "成功更新個人資料"
    - Scenario: "嘗試刪除不存在的資料"
    - Scenario: "成功刪除個人資料"

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/ProfileId.java` | Value Object: Profile ID UUID wrapper |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/Email.java` | Value Object: Email validation and storage |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/DisplayName.java` | Value Object: Display Name length/content check |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/Bio.java` | Value Object: Bio content limit check |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/Profile.java` | Entity: Core business logic for Profile state |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/exception/DuplicateEmailException.java` | Domain Exception: Email already exists |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/exception/InvalidEmailException.java` | Domain Exception: Invalid email format |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/profile/exception/InvalidDisplayNameException.java` | Domain Exception: Invalid display name |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface: Create Profile |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface: Get Profile |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface: Update Profile |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface: Delete Profile |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Impl: Orchestrates creation, checks duplicates |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Impl: Loads and returns profile data |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Impl: Validates updates, saves changes |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Impl: Verifies existence, triggers deletion |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface: Read operations |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface: Write (Create/Update) operations |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java` | Outbound Port Interface: Delete operations |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/profile/ProfileController.java` | REST Controller: Handles HTTP requests/responses |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | DTO: Input for Create endpoint |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/GetProfileResponse.java` | DTO: Output for Get endpoint |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | DTO: Input for Update endpoint |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/exception/GlobalExceptionHandler.java` | Error Handler: Maps exceptions to HTTP status codes |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/profile/ProfileEntity.java` | JPA Entity: Database table mapping |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/JpaProfileRepository.java` | Spring Data Repository: DB access interface |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Port Impl: Loads Profile from DB |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Port Impl: Saves Profile to DB |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaRemoveProfilePort.java` | Port Impl: Removes Profile from DB |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/profile/ProfileUseCaseConfiguration.java` | Config: Wires Use Cases and Ports as Beans |
