# 技術設計：個人資料管理 (Personal Profile CRUD)

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，無外部依賴。

### Entity / Value Object

- **`ProfileId`** (`record`)
  - 型別：`record ProfileId(String value)`
  - 職責：封裝識別碼字串，確保型別安全。
  - 不變條件：`value` 不得為 null 或空白。

- **`Email`** (`record`)
  - 型別：`record Email(String value)`
  - 職責：封裝電子郵件地址，確保型別安全。
  - 不變條件：`value` 不得為 null 或空白，且需符合基本電子郵件格式（正規表達式驗證）。

- **`Profile`** (`record`)
  - 型別：`record Profile(ProfileId id, Email email, String name)`
  - 職責：代表個人資料的不可變狀態。
  - 不變條件：
    - `id` 不得為 null。
    - `email` 不得為 null。
    - `name` 不得為 null。
  - 行為：
    - `updateName(String newName)`: 回傳新的 `Profile` 實例，`name` 更新為 `newName`，其他欄位不變。若 `newName` 為 null 或空白則拋出例外。

### Domain Exceptions

- **`ProfileNotFoundException`**
  - 繼承自 `RuntimeException`。
  - 用途：當查詢或更新/刪除不存在的識別碼時拋出。

- **`DuplicateEmailException`**
  - 繼承自 `RuntimeException`。
  - 用途：當建立或更新資料時，電子郵件已存在於系統中時拋出。

- **`InvalidEmailException`**
  - 繼承自 `RuntimeException`。
  - 用途：當電子郵件格式無效時拋出（通常在 Value Object 建構子中檢查）。

## 2. Use Cases 與 Ports

本層級定義業務邏輯流程，僅依賴 `domain`。

### Inbound Ports (Use Cases)

1. **`CreateProfileUseCase`**
   - 方法：`ProfileId createProfile(String name, String email)`
   - 描述：接收姓名與電子郵件字串，驗證格式，檢查重複，建立 Profile 並回傳 ID。

2. **`LoadProfileUseCase`**
   - 方法：`Profile loadProfile(ProfileId id)`
   - 描述：根據 ID 載入 Profile，若不存在則拋出 `ProfileNotFoundException`。

3. **`UpdateProfileUseCase`**
   - 方法：`Profile updateProfile(ProfileId id, String newName)`
   - 描述：根據 ID 載入 Profile，驗證新姓名，更新後儲存並回傳更新後的 Profile。若不存在則拋出 `ProfileNotFoundException`。

4. **`DeleteProfileUseCase`**
   - 方法：`void deleteProfile(ProfileId id)`
   - 描述：根據 ID 刪除 Profile。若不存在則拋出 `ProfileNotFoundException`。

### Outbound Ports (Repository Interfaces)

1. **`SaveProfilePort`**
   - 方法：`ProfileId save(String name, String email)`
   - 描述：儲存新的個人資料。若電子郵件重複，應拋出 `DuplicateEmailException`。

2. **`LoadProfilePort`**
   - 方法：`Optional<Profile> findById(ProfileId id)`
   - 描述：根據 ID 查詢個人資料。

3. **`DeleteProfilePort`**
   - 方法：`void deleteById(ProfileId id)`
   - 描述：根據 ID 刪除個人資料。

### Use Case Implementations

- **`CreateProfileService`**：實作 `CreateProfileUseCase`。
- **`LoadProfileService`**：實作 `LoadProfileUseCase`。
- **`UpdateProfileService`**：實作 `UpdateProfileUseCase`。
- **`DeleteProfileService`**：實作 `DeleteProfileUseCase`。

## 3. Web Adapter

本層級處理 HTTP 請求與回應，依賴 `application`。

### Controller

- **`ProfileController`** (`@RestController`)
  - 路徑：`/api/profiles`

### Endpoints

1. **POST `/api/profiles`**
   - 方法：`createProfile(@Valid @RequestBody CreateProfileRequest request)`
   - 回傳：`201 Created`，Header `Location` 指向新資源。
   - 參數：`CreateProfileRequest` (包含 `name`, `email`)。

2. **GET `/api/profiles/{id}`**
   - 方法：`getProfile(@PathVariable("id") String id)`
   - 回傳：`200 OK`，`ProfileResponse`。
   - 參數：`id` (UUID 字串)。

3. **PUT `/api/profiles/{id}`**
   - 方法：`updateProfile(@PathVariable("id") String id, @Valid @RequestBody UpdateProfileRequest request)`
   - 回傳：`200 OK`，`ProfileResponse`。
   - 參數：`id` (UUID 字串)，`UpdateProfileRequest` (包含 `name`)。

4. **DELETE `/api/profiles/{id}`**
   - 方法：`deleteProfile(@PathVariable("id") String id)`
   - 回傳：`204 No Content`。
   - 參數：`id` (UUID 字串)。

### DTOs

- **`CreateProfileRequest`**
  - 欄位：`String name` (NotBlank), `String email` (Email, NotBlank)。
- **`UpdateProfileRequest`**
  - 欄位：`String name` (NotBlank)。
- **`ProfileResponse`**
  - 欄位：`String id`, `String email`, `String name`。

### Error Handling

- **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
  - `ProfileNotFoundException` → `404 Not Found`。
  - `DuplicateEmailException` → `409 Conflict`。
  - `InvalidEmailException` → `400 Bad Request`。
  - `MethodArgumentNotValidException` → `400 Bad Request`。
  - `HttpMessageNotReadableException` → `400 Bad Request`。

## 4. Persistence Adapter

本層級處理資料庫存取，依賴 `application` 的 Ports 與 `domain`。

### JPA Entity

- **`ProfileEntity`**
  - 實作 `Persistable<String>`。
  - 欄位：
    - `@Id String id` (UUID)。
    - `@Column(unique = true) String email`。
    - `String name`。
    - `@Transient boolean isNew = true`。
  - 行為：
    - `isNew()`: 回傳 `isNew` 欄位。
    - `@PostLoad`, `@PostPersist`: 將 `isNew` 設為 `false`。
    - 建構子：接受 `Profile` 並轉換為 Entity，保持 `isNew = true`。
    - 靜態方法 `fromDomain(Profile)`: 建立 Entity。
    - 實例方法 `toDomain()`: 轉換回 Domain `Profile`。

### Repository

- **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, String>`)
  - 方法：`Optional<ProfileEntity> findByEmail(String email)` (用於檢查重複)。

### Port Implementations

- **`JpaSaveProfilePort`**
  - 實作 `SaveProfilePort`。
  - 邏輯：
    1. 檢查 `email` 是否已存在 (`findByEmail`)，若存在拋出 `DuplicateEmailException`。
    2. 建立 `ProfileEntity`。
    3. 呼叫 `repository.saveAndFlush(entity)`。
    4. 回傳 `entity.getId()`。
    5. 捕捉 `DataIntegrityViolationException` 並轉為 `DuplicateEmailException` (以防萬一)。

- **`JpaLoadProfilePort`**
  - 實作 `LoadProfilePort`。
  - 邏輯：
    1. 呼叫 `repository.findById(id)`。
    2. 若存在，轉換為 `Profile` 並回傳 `Optional.of(...)`。
    3. 否則回傳 `Optional.empty()`。

- **`JpaDeleteProfilePort`**
  - 實作 `DeleteProfilePort`。
  - 邏輯：
    1. 呼叫 `repository.deleteById(id)`。
    2. 若 ID 不存在，JPA 可能不會拋出例外，需先檢查或捕捉例外。建議先 `findById` 檢查，若不存在拋出 `ProfileNotFoundException`，再執行 `deleteById`。

### Converter

- 轉換邏輯內建於 `ProfileEntity` 的靜態方法與實例方法中，或可提取為 `ProfileConverter` 類別（若邏輯複雜）。此處建議直接放在 Entity 或一個靜態工具類別 `ProfileEntityConverter` 中。為保持清晰，將轉換邏輯放在 `ProfileEntity` 的靜態方法中。

## 5. Bootstrap

本層級組裝 Bean。

- **`UseCaseConfiguration`** (`@Configuration`)
  - `@Bean` `CreateProfileService` (注入 `SaveProfilePort`)。
  - `@Bean` `LoadProfileService` (注入 `LoadProfilePort`)。
  - `@Bean` `UpdateProfileService` (注入 `LoadProfilePort`, `SaveProfilePort`)。
  - `@Bean` `DeleteProfileService` (注入 `LoadProfilePort`, `DeleteProfilePort`)。
  - `@Bean` `JpaSaveProfilePort` (注入 `ProfileRepository`)。
  - `@Bean` `JpaLoadProfilePort` (注入 `ProfileRepository`)。
  - `@Bean` `JpaDeleteProfilePort` (注入 `ProfileRepository`)。

- **`WebConfiguration`** (可選，若需自訂 JSON 序列化)
  - 此專案使用標準 Jackson，無需額外組裝。

## 6. 測試策略

### Unit Tests (JUnit + AssertJ + Mockito)

- **`domain`**:
  - `ProfileIdTest`: 驗證建構與不變條件。
  - `EmailTest`: 驗證格式檢查。
  - `ProfileTest`: 驗證 `updateName` 行為與不變條件。
- **`application`**:
  - `CreateProfileServiceTest`: 測試成功建立、重複電子郵件例外、無效電子郵件例外。
  - `LoadProfileServiceTest`: 測試存在與不存在的情況。
  - `UpdateProfileServiceTest`: 測試成功更新、不存在例外、無效姓名例外。
  - `DeleteProfileServiceTest`: 測試成功刪除、不存在例外。
- **`adapter-web`**:
  - `ProfileControllerTest` (`@WebMvcTest`): 測試所有 CRUD 端點的 HTTP 狀態碼、請求/回應格式、錯誤處理。
- **`adapter-persistence`**:
  - `JpaSaveProfilePortTest` (`@DataJpaTest` + Testcontainers): 測試儲存、重複電子郵件檢查、轉換邏輯。
  - `JpaLoadProfilePortTest` (`@DataJpaTest` + Testcontainers): 測試查詢。
  - `JpaDeleteProfilePortTest` (`@DataJpaTest` + Testcontainers): 測試刪除。

### Integration Tests (Cucumber)

- **Feature File**: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
- **Step Definitions**: `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 使用 `TestRestTemplate` 呼叫實際 API，驗證端到端行為。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 識別碼 Value Object |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件 Value Object |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料 Entity |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到資料例外 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 重複電子郵件例外 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 無效電子郵件例外 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立資料 Use Case 介面 |
| `LoadProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/LoadProfileUseCase.java` | 查詢資料 Use Case 介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新資料 Use Case 介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除資料 Use Case 介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存資料 Port 介面 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 載入資料 Port 介面 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | 刪除資料 Port 介面 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 建立資料 Use Case 實作 |
| `LoadProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/LoadProfileService.java` | 查詢資料 Use Case 實作 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 更新資料 Use Case 實作 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 刪除資料 Use Case 實作 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST API Controller |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 錯誤處理 |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | 建立請求 DTO |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | 更新請求 DTO |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | 回應 DTO |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 載入 Port 實作 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 刪除 Port 實作 |
| `UseCaseConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Use Case Bean 組裝 |
| `ProfileSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber 步驟定義 |
| `personal-profile-crud.feature` | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature 檔 |
