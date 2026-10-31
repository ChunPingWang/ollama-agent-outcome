# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain)

### 值物件 (Value Objects)
- **`ProfileId`** (`record`)
  - 內容：`String id` (UUID 字串)。
  - 不變條件：不可為 null 或空白。
- **`Email`** (`record`)
  - 內容：`String value`。
  - 不變條件：符合 RFC 5322 標準格式（由 Domain 層驗證或依賴 Jakarta Validation 結果，此處定義為有效格式）。
- **`Name`** (`record`)
  - 內容：`String value`。
  - 不變條件：不可為 null 或空白，長度限制可視需求（此處暫無長度限制，僅非空）。
- **`Bio`** (`record`)
  - 內容：`String value`。
  - 不變條件：可為 null，若為 null 則視為空字串 `""`。

### 實體 (Entity)
- **`Profile`** (`record`)
  - 內容：`ProfileId id`, `Email email`, `Name name`, `Bio bio`。
  - 不變條件：
    - `email` 在系統中必須唯一（由 Repository 層保證）。
    - `name` 不可為空。
  - 行為：
    - `update(Name newName, Bio newBio)`: 更新姓名與簡介，回傳新的 Profile 實例（不可變設計）。

### 領域例外 (Domain Exceptions)
- **`ProfileNotFoundException`**：當查詢或更新/刪除的 ID 不存在時拋出。
- **`DuplicateEmailException`**：當建立或更新時，電子郵件已存在於系統中時拋出。
- **`InvalidEmailException`**：當電子郵件格式無效時拋出（通常在輸入驗證階段由 Web Adapter 攔截，但若 Domain 層有嚴格驗證規則亦可在此定義，此處主要依賴 Web 層驗證，Domain 層主要處理業務邏輯例外）。
- **`InvalidNameException`**：當姓名為空時拋出。

## 2. Use Cases 與 Ports

### Inbound Ports (Use Cases)
1. **`CreateProfileUseCase`**
   - `void create(CreateProfileRequest request)`
2. **`GetProfileUseCase`**
   - `ProfileResponse get(String profileId)`
3. **`UpdateProfileUseCase`**
   - `ProfileResponse update(String profileId, UpdateProfileRequest request)`
4. **`DeleteProfileUseCase`**
   - `void delete(String profileId)`

### Outbound Ports
1. **`SaveProfilePort`**
   - `void save(Profile profile)`
2. **`LoadProfilePort`**
   - `Optional<Profile> findById(String profileId)`
3. **`DeleteProfilePort`**
   - `void deleteById(String profileId)`

### Use Case Implementations
- `CreateProfileService`：實作 `CreateProfileUseCase`。
- `GetProfileService`：實作 `GetProfileUseCase`。
- `UpdateProfileService`：實作 `UpdateProfileUseCase`。
- `DeleteProfileService`：實作 `DeleteProfileUseCase`。

## 3. Web Adapter

### Controller
- **`ProfileController`** (`@RestController`)
  - `POST /profiles`：呼叫 `CreateProfileUseCase`。
  - `GET /profiles/{id}`：呼叫 `GetProfileUseCase`。
  - `PUT /profiles/{id}`：呼叫 `UpdateProfileUseCase`。
  - `DELETE /profiles/{id}`：呼叫 `DeleteProfileUseCase`。

### DTOs
- **`CreateProfileRequest`**
  - `String email` (驗證：@Email, @NotBlank)
  - `String name` (驗證：@NotBlank)
  - `String bio` (可選)
- **`UpdateProfileRequest`**
  - `String email` (驗證：@Email, @NotBlank)
  - `String name` (驗證：@NotBlank)
  - `String bio` (可選)
- **`ProfileResponse`**
  - `String id`
  - `String email`
  - `String name`
  - `String bio`

### 錯誤處理 (`@RestControllerAdvice`)
- `ProfileNotFoundException` → 404 Not Found
- `DuplicateEmailException` → 409 Conflict
- `MethodArgumentNotValidException` → 400 Bad Request
- 其他未預期例外 → 500 Internal Server Error (僅限明確捕捉的例外，避免覆蓋 404)

## 4. Persistence Adapter

### JPA Entity
- **`ProfileEntity`**
  - `@Id String id` (UUID)
  - `@Column(unique = true) String email`
  - `String name`
  - `String bio`
  - 實作 `Persistable<String>` 以支援自訂 ID 生成與 isNew 狀態追蹤。

### Repository
- **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, String>`)

### Port Implementations
- **`JpaSaveProfilePort`**：實作 `SaveProfilePort`。使用 `saveAndFlush` 以確保唯一性約束在測試中立即觸發。捕捉 `DataIntegrityViolationException` 並轉換為 `DuplicateEmailException`。
- **`JpaLoadProfilePort`**：實作 `LoadProfilePort`。使用 `findById` 回傳 `Optional`。
- **`JpaDeleteProfilePort`**：實作 `DeleteProfilePort`。使用 `deleteById`。

### Converter
- **`ProfileConverter`**：負責 `Profile` (Domain) 與 `ProfileEntity` (JPA) 之間的轉換。

## 5. Bootstrap

- **`UseCaseConfiguration`** (`@Configuration`)
  - `@Bean` 定義所有 `*UseCase` 介面與 `*Service` 實作的綁定。
  - `@Bean` 定義所有 `*Port` 介面與 `*Port` 實作的綁定。
  - 確保 `ProfileRepository` 等 Spring Data 介面可被注入。

## 6. 測試策略

### Unit Tests (JUnit + Mockito + AssertJ)
- **Domain**:
  - `ProfileTest`：測試值物件的不變條件與 `update` 行為。
- **Application**:
  - `CreateProfileServiceTest`：測試建立邏輯，模擬 `SaveProfilePort`。
  - `GetProfileServiceTest`：測試查詢邏輯，模擬 `LoadProfilePort`。
  - `UpdateProfileServiceTest`：測試更新邏輯，模擬 `LoadProfilePort` 與 `SaveProfilePort`。
  - `DeleteProfileServiceTest`：測試刪除邏輯，模擬 `LoadProfilePort` 與 `DeleteProfilePort`。
- **Web Adapter**:
  - `ProfileControllerTest` (`@WebMvcTest`)：測試 HTTP 請求/回應，模擬 Use Case。
- **Persistence Adapter**:
  - `ProfileEntityTest`：測試 JPA Entity 映射。
  - `JpaSaveProfilePortTest` (`@DataJpaTest` + Testcontainers)：測試儲存邏輯與唯一性約束。
  - `JpaLoadProfilePortTest` (`@DataJpaTest` + Testcontainers)：測試查詢邏輯。
  - `JpaDeleteProfilePortTest` (`@DataJpaTest` + Testcontainers)：測試刪除邏輯。
  - `ProfileConverterTest`：純 Java 測試，測試轉換邏輯。

### Integration Tests (Cucumber BDD)
- **Feature File**: `bootstrap/src/test/resources/features/personal-profile-crud.feature`
- **Step Definitions**: `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 使用 `TestRestTemplate` 呼叫實際 API，並透過 `Testcontainers` 提供 PostgreSQL。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料識別碼值物件 |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件值物件 |
| `Name` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | 姓名值物件 |
| `Bio` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Bio.java` | 簡介值物件 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料實體 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到個人資料例外 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 電子郵件重複例外 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立個人資料 Port 介面 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料 Port 介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料 Port 介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料 Port 介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料 Port 介面 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 載入個人資料 Port 介面 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | 刪除個人資料 Port 介面 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 建立個人資料 Use Case 實作 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | 查詢個人資料 Use Case 實作 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 更新個人資料 Use Case 實作 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 刪除個人資料 Use Case 實作 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST API 控制器 |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | 建立請求 DTO |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | 更新請求 DTO |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | 回應 DTO |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 全域錯誤處理 |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 載入 Port 實作 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 刪除 Port 實作 |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Domain 與 JPA 轉換器 |
| `UseCaseConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Use Case Bean 組態 |
| `ProfileSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber 步驟定義 |
| `personal-profile-crud.feature` | `bootstrap/src/test/resources/features/personal-profile-crud.feature` | Cucumber Feature 檔 |
