# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，不依賴任何框架。

### 1.1 Value Objects

*   **`ProfileId`** (`record`)
    *   內容：`UUID id`
    *   職責：封裝個人資料的唯一識別碼。
    *   不變條件：不可為 `null`。

*   **`Email`** (`record`)
    *   內容：`String value`
    *   職責：封裝電子郵件地址，確保格式正確性。
    *   不變條件：
        *   不可為 `null` 或空白。
        *   必須符合標準電子郵件格式（使用正則表達式驗證）。
        *   長度限制（例如 254 字元）。

### 1.2 Entity

*   **`Profile`** (`record`)
    *   內容：`ProfileId id`, `String name`, `Email email`
    *   職責：代表一個完整的個人資料聚合根。
    *   不變條件：
        *   `id` 不可為 `null`（建立時由系統生成）。
        *   `name` 不可為 `null` 或空白。
        *   `email` 不可為 `null`。
    *   行為：
        *   `updateName(String newName)`: 更新姓名。
        *   `updateEmail(Email newEmail)`: 更新電子郵件。

### 1.3 Domain Exceptions

*   **`ProfileNotFoundException`**
    *   情境：查詢或更新/刪除不存在的個人資料。
    *   訊息：包含缺失的 ID。

*   **`DuplicateEmailException`**
    *   情境：建立或更新時，電子郵件已存在於系統中。
    *   訊息：包含重複的電子郵件地址。

*   **`InvalidEmailException`**
    *   情境：電子郵件格式不符合規範。
    *   訊息：包含無效的電子郵件字串。

*   **`InvalidNameException`**
    *   情境：姓名為空或無效。
    *   訊息：說明姓名無效。

## 2. Use Cases 與 Ports (Application Layer)

本層級定義業務邏輯流程，僅依賴 `domain`。

### 2.1 Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   方法：`ProfileId create(CreateProfileRequest request)`
    *   參數：`CreateProfileRequest` (record: `String name`, `String email`)
    *   流程：
        1. 驗證輸入（可在此或 Domain 層驗證，建議在 Domain 的 `Email` VO 中驗證格式，業務規則如唯一性由 Port 處理）。
        2. 呼叫 `SaveProfilePort.save()`。
        3. 回傳生成的 ID。

2.  **`GetProfileUseCase`**
    *   方法：`Profile getProfile(ProfileId id)`
    *   流程：
        1. 呼叫 `LoadProfilePort.load(id)`。
        2. 若找不到，拋出 `ProfileNotFoundException`。
        3. 回傳 `Profile`。

3.  **`UpdateProfileUseCase`**
    *   方法：`void updateProfile(UpdateProfileRequest request)`
    *   參數：`UpdateProfileRequest` (record: `ProfileId id`, `String name`, `String email`)
    *   流程：
        1. 呼叫 `LoadProfilePort.load(id)` 取得現有資料。
        2. 若找不到，拋出 `ProfileNotFoundException`。
        3. 呼叫 `SaveProfilePort.save()` 儲存更新後的資料（Port 層需處理唯一性檢查）。

4.  **`DeleteProfileUseCase`**
    *   方法：`void deleteProfile(ProfileId id)`
    *   流程：
        1. 呼叫 `LoadProfilePort.load(id)` 確認存在。
        2. 若找不到，拋出 `ProfileNotFoundException`。
        3. 呼叫 `DeleteProfilePort.delete(id)`。

### 2.2 Outbound Ports

1.  **`SaveProfilePort`**
    *   方法：`Profile save(Profile profile)`
    *   職責：儲存或更新個人資料。需處理唯一性約束檢查（若重複則拋出 `DuplicateEmailException`）。

2.  **`LoadProfilePort`**
    *   方法：`Optional<Profile> findById(ProfileId id)`
    *   職責：根據 ID 查詢個人資料。

3.  **`DeleteProfilePort`**
    *   方法：`void deleteById(ProfileId id)`
    *   職責：根據 ID 刪除個人資料。

### 2.3 Use Case Implementations

*   `CreateProfileService`
*   `GetProfileService`
*   `UpdateProfileService`
*   `DeleteProfileService`

## 3. Web Adapter (adapter-web)

本層級處理 HTTP 請求與回應，依賴 `application`。

### 3.1 Controller

*   **`ProfileController`** (`@RestController`)
    *   路徑：`/api/profiles`

    | 方法 | 路徑 | 動作 | 狀態碼 |
    | :--- | :--- | :--- | :--- |
    | `POST` | `/api/profiles` | 建立 | 201 Created (含 Location header) |
    | `GET` | `/api/profiles/{id}` | 查詢 | 200 OK |
    | `PUT` | `/api/profiles/{id}` | 更新 | 200 OK |
    | `DELETE` | `/api/profiles/{id}` | 刪除 | 204 No Content |

### 3.2 DTOs

*   **`CreateProfileRequest`** (`record`)
    *   欄位：`@NotBlank String name`, `@Email String email`
*   **`UpdateProfileRequest`** (`record`)
    *   欄位：`@NotBlank String name`, `@Email String email`
*   **`ProfileResponse`** (`record`)
    *   欄位：`String id`, `String name`, `String email`

### 3.3 Error Handling

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   `ProfileNotFoundException` → 404 Not Found
    *   `DuplicateEmailException` → 409 Conflict
    *   `InvalidEmailException` / `InvalidNameException` → 400 Bad Request
    *   `MethodArgumentNotValidException` → 400 Bad Request (Jakarta Validation 失敗)
    *   `HttpMessageNotReadableException` → 400 Bad Request

## 4. Persistence Adapter (adapter-persistence)

本層級處理資料庫操作，依賴 `application` (Ports) 與 `domain`。

### 4.1 JPA Entity

*   **`ProfileEntity`** (`@Entity`)
    *   實作 `Persistable<UUID>` 以處理 ID 生成邏輯（避免 `@GeneratedValue` 與 Domain 生成 ID 的衝突）。
    *   欄位：
        *   `@Id UUID id`
        *   `@Column(unique = true, nullable = false) String email`
        *   `@Column(nullable = false) String name`
        *   `@Transient boolean isNew = true`
    *   方法：
        *   `isNew()`: 回傳 `isNew` 狀態。
        *   `@PostLoad`, `@PostPersist`: 管理 `isNew` 狀態。

### 4.2 Repository

*   **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   方法：`Optional<ProfileEntity> findByEmail(String email)` (用於唯一性檢查)

### 4.3 Port Implementations

*   **`JpaSaveProfilePort`** (`implements SaveProfilePort`)
    *   依賴：`ProfileRepository`
    *   邏輯：
        1. 檢查 `email` 是否已存在（使用 `findByEmail`）。若存在且非同一筆 ID，拋出 `DuplicateEmailException`。
        2. 將 Domain `Profile` 轉換為 `ProfileEntity`。
        3. 呼叫 `repository.save()`。
        4. 將結果轉換回 Domain `Profile` 並回傳。

*   **`JpaLoadProfilePort`** (`implements LoadProfilePort`)
    *   依賴：`ProfileRepository`
    *   邏輯：
        1. 呼叫 `repository.findById()`。
        2. 若存在，轉換為 Domain `Profile` 並回傳 `Optional`。
        3. 若不存在，回傳 `Optional.empty()`。

*   **`JpaDeleteProfilePort`** (`implements DeleteProfilePort`)
    *   依賴：`ProfileRepository`
    *   邏輯：
        1. 呼叫 `repository.deleteById()`。

### 4.4 Converter

*   **`ProfileConverter`** (Stateless Utility Class)
    *   方法：
        *   `toEntity(Profile profile)`: 轉換 Domain 到 Entity。
        *   `toDomain(ProfileEntity entity)`: 轉換 Entity 到 Domain。

## 5. Bootstrap

*   **`ProfileApplication`** (`@SpringBootApplication`)
    *   主程式入口。
*   **`ProfileConfiguration`** (`@Configuration`)
    *   使用 `@Bean` 建立 Application 層的 Use Case 實作類別。
    *   注入所需的 Port 實作（由 Spring Data JPA 自動建立 Proxy）。
    *   範例：
        ```java
        @Bean
        CreateProfileUseCase createProfileUseCase(SaveProfilePort saveProfilePort) {
            return new CreateProfileService(saveProfilePort);
        }
        // ... 其他 Use Cases
        ```

## 6. 測試策略

### 6.1 Unit Tests (Domain & Application)

*   **`domain` 層**：
    *   `EmailTest`: 測試格式驗證、不可變性。
    *   `ProfileTest`: 測試建立、更新、不變條件。
*   **`application` 層**：
    *   `CreateProfileServiceTest`: 使用 Mockito 模擬 `SaveProfilePort`。測試成功建立、重複電子郵件例外。
    *   `GetProfileServiceTest`: 測試存在與不存在的情況。
    *   `UpdateProfileServiceTest`: 測試成功更新、找不到資料、重複電子郵件。
    *   `DeleteProfileServiceTest`: 測試成功刪除、找不到資料。

### 6.2 Integration Tests (Persistence)

*   **`adapter-persistence` 層**：
    *   `JpaProfileRepositoryTest`: 使用 `@DataJpaTest` + Testcontainers。測試 JPA Entity 的 CRUD、唯一性約束 (`DataIntegrityViolationException`)。
    *   `JpaSaveProfilePortTest`: 使用 `@DataJpaTest` + `@Import(JpaSaveProfilePort.class)`。測試 Port 層的業務邏輯（如重複檢查）。
    *   `JpaLoadProfilePortTest`: 測試查詢邏輯。
    *   `JpaDeleteProfilePortTest`: 測試刪除邏輯。
    *   `ProfileConverterTest`: 測試 Domain 與 Entity 之間的轉換。

### 6.3 Web Tests

*   **`adapter-web` 層**：
    *   `ProfileControllerTest`: 使用 `@WebMvcTest` + `@MockitoBean` 模擬 Use Cases。測試 HTTP 狀態碼、請求/回應格式、驗證錯誤。

### 6.4 BDD Tests (Cucumber)

*   **`bootstrap` 層**：
    *   Feature 檔：`features/profile-crud.feature`
    *   步驟定義：`ProfileStepDefinitions`
    *   測試方式：使用 `TestRestTemplate` 呼叫實際 API，驗證端到端行為。
    *   環境：使用 `@SpringBootTest` + Testcontainers PostgreSQL。

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 個人資料識別碼 Value Object |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 電子郵件 Value Object，含格式驗證 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 個人資料 Entity，含業務規則 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | 找不到個人資料例外 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | 電子郵件重複例外 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | 電子郵件格式無效例外 |
| **Domain** | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidNameException.java` | 姓名無效例外 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | 建立個人資料 Inbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | 查詢個人資料 Inbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | 更新個人資料 Inbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | 刪除個人資料 Inbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | 儲存個人資料 Outbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | 載入個人資料 Outbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | 刪除個人資料 Outbound Port |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | 建立個人資料 Use Case 實作 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | 查詢個人資料 Use Case 實作 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | 更新個人資料 Use Case 實作 |
| **Application** | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | 刪除個人資料 Use Case 實作 |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST API Controller |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequest.java` | 建立請求 DTO |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequest.java` | 更新請求 DTO |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponse.java` | 回應 DTO |
| **Adapter Web** | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 全域錯誤處理 |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Domain-Entity 轉換器 |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | 儲存 Port 實作 |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | 載入 Port 實作 |
| **Adapter Persistence** | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | 刪除 Port 實作 |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ProfileApplication.java` | Spring Boot 主程式 |
| **Bootstrap** | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ProfileConfiguration.java` | Bean 組態 |
| **Bootstrap** | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java` | Cucumber 步驟定義 |
| **Bootstrap** | `bootstrap/src/test/resources/features/profile-crud.feature` | Cucumber Feature 檔 |
