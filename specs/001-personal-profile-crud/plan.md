# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本模組採用值物件 (Value Object) 與實體 (Entity) 分離的設計，確保領域邏輯的純淨性與不可變性。

### 1.1 值物件 (Value Objects)

*   **`ProfileId`** (`record`)
    *   內容：`UUID id`
    *   職責：封裝個人資料的唯一識別碼。
    *   不變條件：不可為 `null`。

*   **`Email`** (`record`)
    *   內容：`String value`
    *   職責：封裝電子郵件地址。
    *   不變條件：
        *   不可為 `null` 或空白。
        *   必須符合標準電子郵件格式（由 Domain 層驗證，或依賴 Jakarta Validation 在輸入層過濾後再建立，此處建議在 Domain 建立時進行嚴格格式檢查以確保領域完整性，或僅作為容器由 Validator 處理。根據 Spec，錯誤訊息需由 Domain 或 Application 層產生，故 Domain 應包含驗證邏輯或拋出例外）。
        *   *決策*：為符合 Spec 中「回應主體應包含錯誤訊息說明電子郵件格式不正確」，我們將在 `Email` 的建構子或靜態工廠方法中驗證格式，若無效則拋出 `InvalidEmailException`。

*   **`Name`** (`record`)
    *   內容：`String value`
    *   職責：封裝使用者姓名。
    *   不變條件：
        *   不可為 `null` 或空白。
        *   若無效，建構子拋出 `InvalidNameException`。

*   **`Age`** (`record`)
    *   內容：`int value`
    *   職責：封裝使用者年齡。
    *   不變條件：
        *   必須為正整數 (`> 0`)。
        *   若無效，建構子拋出 `InvalidAgeException`。

### 1.2 實體 (Entity)

*   **`Profile`** (`record` 或 `class`)
    *   由於需要支援更新操作且需與 JPA Entity 對應，Domain Entity 建議使用不可變的 `record` 來表示領域狀態，但在 Application 層進行狀態轉換。或者，為了簡化領域邏輯，使用 `class` 並提供 `update` 方法返回新實例或修改自身（依團隊慣例，此處採用不可變設計模式，即 `withUpdated(...)` 返回新 `Profile`）。
    *   欄位：
        *   `ProfileId id`
        *   `Email email`
        *   `Name name`
        *   `Age age`
    *   不變條件：
        *   所有欄位不可為 `null`。
        *   `email` 在系統中必須唯一（此約束由 Repository 層或 Domain Service 層確保，通常由 Repository 的 `findByEmail` 配合唯一索引處理，或 Domain Service 在 Save 前檢查）。
    *   行為：
        *   `update(Name newName, Age newAge)`: 返回新的 `Profile` 實例，反映更新後的狀態。

### 1.3 領域例外 (Domain Exceptions)

*   **`ProfileNotFoundException`**：當查詢或更新/刪除不存在的 Profile 時拋出。
*   **`DuplicateEmailException`**：當建立或更新時，電子郵件已存在時拋出。
*   **`InvalidEmailException`**：當電子郵件格式無效時拋出。
*   **`InvalidNameException`**：當姓名為空或無效時拋出。
*   **`InvalidAgeException`**：當年齡非正整數時拋出。

## 2. Use Cases 與 Ports

### 2.1 Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   方法：`ProfileId create(CreateProfileRequest request)`
    *   職責：驗證輸入資料，檢查電子郵件唯一性，建立新的 Profile 並儲存。

2.  **`GetProfileUseCase`**
    *   方法：`Profile getProfile(ProfileId id)`
    *   職責：根據 ID 查詢 Profile，若不存在則拋出 `ProfileNotFoundException`。

3.  **`UpdateProfileUseCase`**
    *   方法：`Profile updateProfile(ProfileId id, UpdateProfileRequest request)`
    *   職責：驗證輸入資料，檢查 Profile 是否存在，檢查電子郵件唯一性（若 email 有變更），更新 Profile 並儲存。

4.  **`DeleteProfileUseCase`**
    *   方法：`void deleteProfile(ProfileId id)`
    *   職責：檢查 Profile 是否存在，若存在則刪除，若不存在則拋出 `ProfileNotFoundException`。

### 2.2 Outbound Ports (Repository)

1.  **`SaveProfilePort`**
    *   方法：`Profile save(Profile profile)`
    *   職責：儲存或更新 Profile。

2.  **`FindProfileByIdPort`**
    *   方法：`Optional<Profile> findById(ProfileId id)`
    *   職責：根據 ID 查找 Profile。

3.  **`FindProfileByEmailPort`**
    *   方法：`Optional<Profile> findByEmail(Email email)`
    *   職責：根據 Email 查找 Profile，用於唯一性檢查。

4.  **`DeleteProfileByIdPort`**
    *   方法：`void deleteById(ProfileId id)`
    *   職責：根據 ID 刪除 Profile。

### 2.3 Use Case Implementations (Services)

*   `CreateProfileService`：實作 `CreateProfileUseCase`。
*   `GetProfileService`：實作 `GetProfileUseCase`。
*   `UpdateProfileService`：實作 `UpdateProfileUseCase`。
*   `DeleteProfileService`：實作 `DeleteProfileUseCase`。

## 3. Web Adapter

### 3.1 Controller

*   **`ProfileController`** (`@RestController`)
    *   路徑：`/profiles`
    *   依賴：`CreateProfileUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `DeleteProfileUseCase`

### 3.2 Request/Response DTOs

*   **`CreateProfileRequest`**
    *   欄位：`email` (String, @NotBlank, @Email), `name` (String, @NotBlank), `age` (Integer, @Min(1))
*   **`UpdateProfileRequest`**
    *   欄位：`email` (String, @NotBlank, @Email), `name` (String, @NotBlank), `age` (Integer, @Min(1))
*   **`ProfileResponse`**
    *   欄位：`id` (String/UUID), `email` (String), `name` (String), `age` (Integer)

### 3.3 錯誤處理 (`@RestControllerAdvice`)

*   **`ProfileExceptionHandler`**
    *   `ProfileNotFoundException` → 404 Not Found
    *   `DuplicateEmailException` → 409 Conflict
    *   `InvalidEmailException`, `InvalidNameException`, `InvalidAgeException` → 400 Bad Request
    *   `MethodArgumentNotValidException` → 400 Bad Request (Jakarta Validation 失敗)
    *   `HttpMessageNotReadableException` → 400 Bad Request

## 4. Persistence Adapter

### 4.1 JPA Entity

*   **`ProfileEntity`**
    *   實作 `Persistable<UUID>` 以處理 ID 生成邏輯（若 Domain 自行生成 UUID）。
    *   欄位：
        *   `@Id UUID id`
        *   `@Column(unique = true) String email`
        *   `String name`
        *   `Integer age`
        *   `@Transient boolean isNew = true`
        *   `@PostLoad`, `@PostPersist` 用於管理 `isNew` 狀態。
    *   注意：`email` 欄位需有唯一索引 (`@Column(unique = true)`)。

### 4.2 Spring Data Repository

*   **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   方法：`Optional<ProfileEntity> findByEmail(String email)`
    *   方法：`void deleteById(UUID id)`

### 4.3 Port Implementations

*   **`JpaSaveProfilePort`**：實作 `SaveProfilePort`。負責將 Domain `Profile` 轉換為 `ProfileEntity`，呼叫 Repository 儲存，再轉換回 Domain `Profile`。
*   **`JpaFindProfileByIdPort`**：實作 `FindProfileByIdPort`。
*   **`JpaFindProfileByEmailPort`**：實作 `FindProfileByEmailPort`。
*   **`JpaDeleteProfileByIdPort`**：實作 `DeleteProfileByIdPort`。

### 4.4 轉換器 (Converter)

*   **`ProfileEntityConverter`**：靜態工具類別或獨立類別，負責 `Profile` ↔ `ProfileEntity` 的雙向轉換。

## 5. Bootstrap

*   **`ProfileConfiguration`** (`@Configuration`)
    *   使用 `@Bean` 定義以下 Bean：
        *   `CreateProfileService`
        *   `GetProfileService`
        *   `UpdateProfileService`
        *   `DeleteProfileService`
    *   這些 Service 依賴於 Outbound Ports 的實作（由 Spring Data JPA 自動代理或手動定義 Bean，建議手動定義以明確依賴關係，或依賴 Spring Data 的自動代理）。
    *   由於 `adapter-persistence` 模組會定義 Repository 和 Port 實作，`bootstrap` 模組只需組裝 Use Case Service。

## 6. 測試策略

### 6.1 單元測試 (Unit Tests)

*   **Domain 層**：
    *   `EmailTest`, `NameTest`, `AgeTest`：測試值物件的建構與不變條件。
    *   `ProfileTest`：測試 Profile 的建立與更新行為。
*   **Application 層**：
    *   `CreateProfileServiceTest`：測試建立邏輯，包括唯一性檢查、例外拋出。使用 Mockito 模擬 `SaveProfilePort` 和 `FindProfileByEmailPort`。
    *   `GetProfileServiceTest`：測試查詢邏輯。
    *   `UpdateProfileServiceTest`：測試更新邏輯，包括存在性檢查、唯一性檢查。
    *   `DeleteProfileServiceTest`：測試刪除邏輯。

### 6.2 整合測試 (Integration Tests)

*   **Persistence 層**：
    *   `JpaProfileRepositoryTest`：使用 `@DataJpaTest` + Testcontainers PostgreSQL。測試 JPA Entity 的儲存、查詢、唯一性約束、刪除。
    *   `JpaSaveProfilePortTest`：測試 Port 實作的轉換邏輯與 Repository 呼叫。
*   **Web 層**：
    *   `ProfileControllerTest`：使用 `@WebMvcTest`。測試 Controller 的路由、DTO 驗證、例外處理。使用 `MockMvc` 模擬請求。

### 6.3 行為驅動測試 (BDD / Cucumber)

*   **Feature 檔**：`bootstrap/src/test/resources/features/profile-crud.feature`
*   **步驟定義**：`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions`
*   **測試環境**：使用 `@SpringBootTest` + `@AutoConfigureTestRestTemplate` + Testcontainers PostgreSQL。
*   **場景覆蓋**：
    *   成功建立、查詢、更新、刪除。
    *   各種錯誤情境（格式錯誤、重複、不存在）。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| **Domain** | | |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料 ID 值物件 |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件值物件 |
| `Name` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Name.java` | 姓名值物件 |
| `Age` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Age.java` | 年齡值物件 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料實體 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 找不到個人資料例外 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 電子郵件重複例外 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 無效電子郵件例外 |
| `InvalidNameException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java` | 無效姓名例外 |
| `InvalidAgeException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidAgeException.java` | 無效年齡例外 |
| **Application** | | |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立個人資料 Port 介面 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料 Port 介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料 Port 介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料 Port 介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料 Port 介面 |
| `FindProfileByIdPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | 依 ID 查詢 Port 介面 |
| `FindProfileByEmailPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByEmailPort.java` | 依 Email 查詢 Port 介面 |
| `DeleteProfileByIdPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfileByIdPort.java` | 依 ID 刪除 Port 介面 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 建立個人資料 Use Case 實作 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | 查詢個人資料 Use Case 實作 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 更新個人資料 Use Case 實作 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 刪除個人資料 Use Case 實作 |
| **Web Adapter** | | |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST API Controller |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | 建立請求 DTO |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | 更新請求 DTO |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | 回應 DTO |
| `ProfileExceptionHandler` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileExceptionHandler.java` | 全域異常處理器 |
| **Persistence Adapter** | | |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| `JpaFindProfileByIdPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java` | 依 ID 查詢 Port 實作 |
| `JpaFindProfileByEmailPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPort.java` | 依 Email 查詢 Port 實作 |
| `JpaDeleteProfileByIdPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfileByIdPort.java` | 依 ID 刪除 Port 實作 |
| `ProfileEntityConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityConverter.java` | Domain 與 Entity 轉換器 |
| **Bootstrap** | | |
| `ProfileConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ProfileConfiguration.java` | Use Case Bean 組態 |
| `ProfileStepDefinitions` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java` | Cucumber 步驟定義 |
