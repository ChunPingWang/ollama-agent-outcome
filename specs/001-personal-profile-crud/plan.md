# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain)

### 實體與值物件

*   **`ProfileId`** (Value Object, `record`)
    *   內容：`UUID id`
    *   不變條件：不可為 null。
    *   用途：作為 Domain Entity 與 JPA Entity 的識別碼橋接。

*   **`Email`** (Value Object, `record`)
    *   內容：`String value`
    *   不變條件：符合標準電子郵件格式（由 Domain 層或 Adapter 層驗證，此處定義為有效狀態）。
    *   用途：確保電子郵件的唯一性與格式正確性。

*   **`Profile`** (Entity, `record` 或不可變類別 + Builder/Factory)
    *   內容：
        *   `id: ProfileId`
        *   `name: String` (非空、長度限制)
        *   `email: Email`
    *   不變條件：
        *   `name` 不可為空白。
        *   `email` 必須唯一（由 Repository 層或 Domain Service 確保，視實作而定，此處建議由 Repository 層處理唯一性約束，Domain 層僅負責業務邏輯）。
    *   行為：
        *   `update(name: String, email: Email): Profile`：建立新的 Profile 實例（Immutable 設計），或更新內部狀態（Mutable 設計）。考慮到 JPA 的受管實體特性，Domain 層建議使用 Immutable record，由 Adapter 層處理轉換與更新邏輯，或使用 Mutable 類別但保持業務規則封裝。
        *   *決策*：為簡化 JPA 整合與更新邏輯，Domain Entity 採用 **Mutable** 類別，但保持業務規則封裝。

*   **`Profile`** (Entity, `class`)
    *   欄位：`id: ProfileId`, `name: String`, `email: Email`
    *   方法：
        *   `static Profile create(id: ProfileId, name: String, email: Email): Profile`
        *   `void updateName(String newName)`
        *   `void updateEmail(Email newEmail)`
        *   私有建構子，透過 Factory 方法建立。

### Domain Exceptions

*   **`ProfileNotFoundException`**：當查詢或更新不存在的 Profile 時拋出。
*   **`DuplicateEmailException`**：當建立或更新時，電子郵件已存在時拋出。
*   **`InvalidEmailException`**：當電子郵件格式無效時拋出（可放在 Domain 或 Adapter，此處放在 Domain 以符合 SOLID，但驗證邏輯可由 Adapter 的 DTO 層先攔截）。
*   **`InvalidNameException`**：當姓名無效時拋出。

## 2. Use Cases 與 Ports

### Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileCommand command)`
    *   對應場景：US-1

2.  **`GetProfileUseCase`**
    *   `Profile execute(GetProfileCommand command)`
    *   對應場景：US-2

3.  **`UpdateProfileUseCase`**
    *   `void execute(UpdateProfileCommand command)`
    *   對應場景：US-3

4.  **`DeleteProfileUseCase`**
    *   `void execute(DeleteProfileCommand command)`
    *   對應場景：US-4

### Outbound Ports (Repository)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
    *   負責將 Domain Entity 持久化。

2.  **`FindProfileByIdPort`**
    *   `Optional<Profile> findById(ProfileId id)`
    *   負責根據 ID 查詢 Profile。

3.  **`FindProfileByEmailPort`** (可選，或合併至 Save 時的檢查)
    *   `Optional<Profile> findByEmail(Email email)`
    *   用於檢查電子郵件唯一性。

### Use Case Implementations

*   `CreateProfileService`：實現 `CreateProfileUseCase`。
*   `GetProfileService`：實現 `GetProfileUseCase`。
*   `UpdateProfileService`：實現 `UpdateProfileUseCase`。
*   `DeleteProfileService`：實現 `DeleteProfileUseCase`。

## 3. Web Adapter

### Controller

*   **`ProfileController`** (`@RestController`)
    *   `POST /profiles`：呼叫 `CreateProfileUseCase`。
    *   `GET /profiles/{id}`：呼叫 `GetProfileUseCase`。
    *   `PUT /profiles/{id}`：呼叫 `UpdateProfileUseCase`。
    *   `DELETE /profiles/{id}`：呼叫 `DeleteProfileUseCase`。

### DTOs

*   **`CreateProfileRequest`**
    *   `String name`
    *   `String email`
    *   驗證：`@NotBlank`, `@Email`

*   **`UpdateProfileRequest`**
    *   `String name`
    *   `String email`
    *   驗證：`@NotBlank`, `@Email`

*   **`ProfileResponse`**
    *   `String id` (UUID string)
    *   `String name`
    *   `String email`

### Error Handling

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   `ProfileNotFoundException` → 404 Not Found
    *   `DuplicateEmailException` → 409 Conflict
    *   `InvalidEmailException` / `InvalidNameException` → 400 Bad Request
    *   `MethodArgumentNotValidException` → 400 Bad Request

## 4. Persistence Adapter

### JPA Entity

*   **`ProfileEntity`** (`@Entity`)
    *   欄位：
        *   `@Id UUID id`
        *   `@Column(unique = true) String email`
        *   `String name`
    *   實作 `Persistable<UUID>` 以處理 ID 生成（若 Domain 自行生成 ID）。
    *   注意：若 Domain 使用 `ProfileId` (UUID)，則 Entity 的 `id` 直接對應。

### Repository

*   **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   `Optional<ProfileEntity> findByEmail(String email)`

### Port Implementations

*   **`JpaSaveProfilePort`**：實現 `SaveProfilePort`。
    *   轉換 Domain `Profile` 到 `ProfileEntity`。
    *   呼叫 `repository.saveAndFlush(entity)`。
    *   捕捉 `DataIntegrityViolationException` 並轉換為 `DuplicateEmailException`。
*   **`JpaFindProfileByIdPort`**：實現 `FindProfileByIdPort`。
    *   呼叫 `repository.findById(id)`，轉換回 Domain `Profile`。
*   **`JpaFindProfileByEmailPort`**：實現 `FindProfileByEmailPort`。
    *   呼叫 `repository.findByEmail(email)`，轉換回 Domain `Profile`。

### Converter

*   **`ProfileConverter`**：負責 `Profile` <-> `ProfileEntity` 的轉換。

## 5. Bootstrap

*   **`UseCaseConfiguration`** (`@Configuration`)
    *   `@Bean CreateProfileUseCase createProfileUseCase(...)`
    *   `@Bean GetProfileUseCase getProfileUseCase(...)`
    *   `@Bean UpdateProfileUseCase updateProfileUseCase(...)`
    *   `@Bean DeleteProfileUseCase deleteProfileUseCase(...)`
    *   `@Bean SaveProfilePort saveProfilePort(...)`
    *   `@Bean FindProfileByIdPort findProfileByIdPort(...)`
    *   `@Bean FindProfileByEmailPort findProfileByEmailPort(...)`
    *   `@Bean ProfileConverter profileConverter(...)`

*   **`WebMvcConfiguration`** (可選，若需自訂 MVC 設定)
    *   通常不需要，Spring Boot 自動組裝。

*   **`CucumberConfiguration`** (在 `bootstrap` 測試套件中)
    *   定義 Cucumber 步驟定義類別。

## 6. 測試策略

### Unit Tests (JUnit + Mockito + AssertJ)

*   **Domain Tests**
    *   `ProfileTest`：測試 `Profile` 的業務規則（如更新姓名、電子郵件格式驗證）。
*   **Application Tests**
    *   `CreateProfileServiceTest`：測試建立邏輯，模擬 `SaveProfilePort` 和 `FindProfileByEmailPort`。
    *   `GetProfileServiceTest`：測試查詢邏輯，模擬 `FindProfileByIdPort`。
    *   `UpdateProfileServiceTest`：測試更新邏輯，模擬 `FindProfileByIdPort` 和 `SaveProfilePort`。
    *   `DeleteProfileServiceTest`：測試刪除邏輯，模擬 `FindProfileByIdPort` 和 `SaveProfilePort` (或 `delete` 方法)。

### Integration Tests (Spring Boot + Testcontainers)

*   **Adapter-Persistence Tests**
    *   `JpaSaveProfilePortTest`：測試 JPA 持久化，包括唯一性約束。
    *   `JpaFindProfileByIdPortTest`：測試查詢。
    *   `ProfileConverterTest`：測試轉換邏輯。
*   **Adapter-Web Tests**
    *   `ProfileControllerTest` (`@WebMvcTest`)：測試 REST API 端點，模擬 Use Case。
    *   `ProfileControllerIntegrationTest` (`@SpringBootTest` + `@AutoConfigureTestRestTemplate`)：端到端測試，使用 Testcontainers PostgreSQL。

### BDD Tests (Cucumber)

*   **Feature File**：`src/test/resources/features/profile-crud.feature`
    *   包含所有驗收場景。
*   **Step Definitions**：`ProfileSteps` (在 `bootstrap` 測試套件中)
    *   使用 `TestRestTemplate` 呼叫 API。
    *   使用 `@MockitoBean` 模擬 Use Case (若為單元測試風格) 或直接呼叫真實服務 (若為整合測試風格)。根據 Spec，建議使用真實服務進行 BDD 測試，以確保端到端行為正確。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 值物件：個人資料識別碼 |
| `Email` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | 值物件：電子郵件 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 實體：個人資料 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 例外：找不到個人資料 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 例外：電子郵件重複 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 例外：無效電子郵件 |
| `InvalidNameException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java` | 例外：無效姓名 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port：建立個人資料 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port：查詢個人資料 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port：更新個人資料 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port：刪除個人資料 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port：儲存個人資料 |
| `FindProfileByIdPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java` | Outbound Port：依 ID 查詢 |
| `FindProfileByEmailPort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByEmailPort.java` | Outbound Port：依 Email 查詢 |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case 實作：建立 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case 實作：查詢 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case 實作：更新 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case 實作：刪除 |
| `CreateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | DTO：建立請求 |
| `UpdateProfileRequest` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | DTO：更新請求 |
| `ProfileResponse` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | DTO：回應 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 錯誤處理 |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port 實作：儲存 |
| `JpaFindProfileByIdPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByIdPort.java` | Port 實作：依 ID 查詢 |
| `JpaFindProfileByEmailPort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaFindProfileByEmailPort.java` | Port 實作：依 Email 查詢 |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | 轉換器 |
| `UseCaseConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Bean 組態 |
| `ProfileSteps` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java` | Cucumber 步驟定義 |
| `profile-crud.feature` | `bootstrap/src/test/resources/features/profile-crud.feature` | Cucumber Feature 檔 |
