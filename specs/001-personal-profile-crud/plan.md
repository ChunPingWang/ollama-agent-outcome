# 技術設計：個人資料管理 (Spec 001)

## 1. 領域模型 (Domain Model)

本層級包含純 Java 類別，不依賴任何框架。

### 實體與值物件

*   **`ProfileId`** (`record`)
    *   類型：`UUID`
    *   職責：值物件，確保識別碼的不可變性與類型安全。
*   **`Profile`** (`record`)
    *   欄位：
        *   `id`: `ProfileId`
        *   `name`: `String` (姓名)
        *   `email`: `String` (電子郵件)
        *   `note`: `String` (備註，可為 null 或空字串)
    *   不變條件 (Invariants)：
        *   `name` 不得為 null 或空白。
        *   `email` 不得為 null 或空白，且需符合基本電子郵件格式（正規表達式驗證）。
    *   行為：
        *   `static Profile create(ProfileId id, String name, String email, String note)`：工廠方法，執行基本驗證。
        *   `Profile update(String name, String email, String note)`：回傳新的 `Profile` 實例，執行更新驗證。

### 領域例外 (Domain Exceptions)

*   **`ProfileNotFoundException`**
    *   繼承自 `RuntimeException`。
    *   用途：當查詢或更新/刪除操作找不到指定 ID 的個人資料時拋出。
*   **`DuplicateEmailException`**
    *   繼承自 `RuntimeException`。
    *   用途：當建立或更新資料時，發現電子郵件已存在於系統中時拋出。
*   **`InvalidEmailException`**
    *   繼承自 `IllegalArgumentException`。
    *   用途：當電子郵件格式不符合規範時拋出。
*   **`InvalidNameException`**
    *   繼承自 `IllegalArgumentException`。
    *   用途：當姓名為空或無效時拋出。

## 2. Use Cases 與 Ports

本層級定義業務邏輯流程，僅依賴 `domain`。

### Inbound Ports (Use Cases)

1.  **`CreateProfileUseCase`**
    *   `void execute(CreateProfileRequest request)`
    *   參數 `CreateProfileRequest`：包含 `name`, `email`, `note`。
    *   回傳：無（識別碼由 Domain 產生或透過 Port 回傳，此處設計為 Domain 產生 ID 後由 Port 儲存，或使用 UUID 生成器）。*修正：為符合 Hexagonal 常見模式，Use Case 可回傳 `ProfileId` 或 void。此處設計為回傳 `ProfileId` 以便 Controller 取得 Location。*
    *   簽名：`ProfileId execute(CreateProfileRequest request)`

2.  **`GetProfileUseCase`**
    *   `Profile execute(ProfileId id)`
    *   若不存在，由 Port 層或 Use Case 層拋出 `ProfileNotFoundException`。

3.  **`UpdateProfileUseCase`**
    *   `Profile execute(ProfileId id, UpdateProfileRequest request)`
    *   若不存在，拋出 `ProfileNotFoundException`。
    *   若 Email 重複，拋出 `DuplicateEmailException`。

4.  **`DeleteProfileUseCase`**
    *   `void execute(ProfileId id)`
    *   若不存在，拋出 `ProfileNotFoundException`。

### Outbound Ports (Repository Interfaces)

1.  **`SaveProfilePort`**
    *   `Profile save(Profile profile)`
    *   負責檢查唯一性約束（Email）。若重複則拋出 `DuplicateEmailException`。

2.  **`LoadProfilePort`**
    *   `Optional<Profile> findById(ProfileId id)`

3.  **`DeleteProfilePort`**
    *   `void deleteById(ProfileId id)`
    *   若資料不存在，可選擇拋出例外或回傳 false。為簡化 Use Case 邏輯，建議若不存在則拋出 `ProfileNotFoundException` 或在 Use Case 層先檢查。*決策：Port 層只負責刪除，若找不到則拋出 `ProfileNotFoundException` 以保持一致性，或 Use Case 先呼叫 `findById` 檢查。為減少 DB 呼叫，`DeleteProfilePort` 若採用 JPA `deleteById`，找不到時通常不拋出例外（除非設為 `DELETE` 模式）。此處設計：`DeleteProfilePort` 回傳 `boolean` 或 Use Case 先 Load。為符合 SOLID 單一職責，Use Case 應負責流程控制。*
    *   *修正策略*：`DeleteProfilePort` 僅負責刪除。Use Case `DeleteProfileService` 先呼叫 `LoadProfilePort.findById`，若無則拋出 `ProfileNotFoundException`，再呼叫 `DeleteProfilePort.deleteById`。

### Use Case Implementations (Services)

*   `CreateProfileService`：實作 `CreateProfileUseCase`。
*   `GetProfileService`：實作 `GetProfileUseCase`。
*   `UpdateProfileService`：實作 `UpdateProfileUseCase`。
*   `DeleteProfileService`：實作 `DeleteProfileUseCase`。

## 3. Web Adapter

本層級處理 HTTP 請求/回應轉換，依賴 `application`。

### Controller

*   **`ProfileController`** (`@RestController`)
    *   `@RequestMapping("/profiles")`

### Endpoints

1.  **POST `/profiles`**
    *   Method: `createProfile(@Valid @RequestBody CreateProfileRequest request)`
    *   回傳：`ResponseEntity.created(location).body(responseDto)`
    *   DTO: `CreateProfileRequest` (name, email, note), `ProfileResponse` (id, name, email, note)

2.  **GET `/profiles/{id}`**
    *   Method: `getProfile(@PathVariable("id") UUID id)`
    *   回傳：`ResponseEntity.ok(profileResponse)`

3.  **PUT `/profiles/{id}`**
    *   Method: `updateProfile(@PathVariable("id") UUID id, @Valid @RequestBody UpdateProfileRequest request)`
    *   回傳：`ResponseEntity.ok(profileResponse)`

4.  **DELETE `/profiles/{id}`**
    *   Method: `deleteProfile(@PathVariable("id") UUID id)`
    *   回傳：`ResponseEntity.noContent().build()`

### DTOs

*   `CreateProfileRequest`：`record`，欄位 `name`, `email`, `note`。使用 Jakarta Validation (`@NotBlank`, `@Email`)。
*   `UpdateProfileRequest`：`record`，欄位 `name`, `email`, `note`。使用 Jakarta Validation。
*   `ProfileResponse`：`record`，欄位 `id`, `name`, `email`, `note`。

### 錯誤處理

*   **`ProfileControllerAdvice`** (`@RestControllerAdvice`)
    *   `@ExceptionHandler(ProfileNotFoundException.class)` → 404 Not Found
    *   `@ExceptionHandler(DuplicateEmailException.class)` → 409 Conflict
    *   `@ExceptionHandler(MethodArgumentNotValidException.class)` → 400 Bad Request
    *   `@ExceptionHandler(InvalidEmailException.class, InvalidNameException.class)` → 400 Bad Request
    *   *注意*：不攔截 `Exception.class`，以免覆蓋 Spring 的 404/500 預設行為。

## 4. Persistence Adapter

本層級處理資料庫存取，依賴 `application` 的 Outbound Ports 與 `domain`。

### JPA Entity

*   **`ProfileEntity`** (`@Entity`)
    *   欄位：
        *   `id`: `UUID` (`@Id`)
        *   `name`: `String` (`@Column(nullable = false)`)
        *   `email`: `String` (`@Column(nullable = false, unique = true)`)
        *   `note`: `String` (`@Column`)
    *   實作 `Persistable<UUID>` 以支援 Domain 產生的 ID。
        *   `isNew()`: 回傳 `true` 若 ID 為 null 或新建立（此處 Domain 產生 UUID，故 Entity 建構時 ID 非 null，但需區分新舊。*修正*：若 Domain 產生 UUID，則 `isNew` 邏輯需調整。通常 JPA 對於已有 ID 的實體視為新實體若 DB 中不存在。為簡化，可讓 `ProfileEntity` 不實作 `Persistable`，而是由 `SaveProfilePort` 實作中處理 `saveAndFlush` 並捕捉 `DataIntegrityViolationException`。若使用 `@GeneratedValue` 則與 Domain 產生 ID 衝突。此處採用 **Domain 產生 UUID** 策略，Entity 不實作 `Persistable`，但在 `SaveProfilePort` 中，若 DB 中已存在相同 Email，則拋出例外。*注意*：JPA `save` 對於已有 ID 的實體，若 DB 中不存在，會執行 INSERT；若存在，會執行 UPDATE。為確保唯一性檢查在 INSERT 階段觸發，需依賴 DB 的 Unique Constraint 並捕捉 `DataIntegrityViolationException`。

### Repository

*   **`ProfileRepository`** (`extends JpaRepository<ProfileEntity, UUID>`)
    *   `Optional<ProfileEntity> findByEmail(String email)` (用於唯一性檢查，若 JPA 不提供自動檢查)
    *   *注意*：Spring Data JPA 的 `save` 在唯一約束違反時會拋出 `DataIntegrityViolationException`。

### Port Implementations

*   **`JpaSaveProfilePort`** (`implements SaveProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `save(Profile profile)`：
        1.  轉換 `Profile` 至 `ProfileEntity`。
        2.  呼叫 `repository.saveAndFlush(entity)`。
        3.  若拋出 `DataIntegrityViolationException`，檢查是否為 Email 重複，若是則拋出 `DuplicateEmailException`，否則重新拋出。
        4.  轉換回 `Profile` 並回傳。
*   **`JpaLoadProfilePort`** (`implements LoadProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `findById(ProfileId id)`：呼叫 `repository.findById(id.id())`，轉換為 `Profile`。
*   **`JpaDeleteProfilePort`** (`implements DeleteProfilePort`)
    *   依賴 `ProfileRepository`。
    *   `deleteById(ProfileId id)`：呼叫 `repository.deleteById(id.id())`。

### Converter

*   **`ProfileConverter`** (靜態工具類別或 Component)
    *   `toEntity(Profile profile)`
    *   `toDomain(ProfileEntity entity)`

## 5. Bootstrap

本層級組裝 Bean。

*   **`UseCaseConfiguration`** (`@Configuration`)
    *   `@Bean` `CreateProfileService`
    *   `@Bean` `GetProfileService`
    *   `@Bean` `UpdateProfileService`
    *   `@Bean` `DeleteProfileService`
    *   *注意*：Port 實作與 Repository 由 Spring Data JPA 自動掃描或 `adapter-persistence` 模組的 `@EnableJpaRepositories` 處理。`UseCaseConfiguration` 僅負責 Use Case 的組裝。

*   **`WebMvcConfiguration`** (可選，若需自訂 JSON 序列化)
    *   通常不需要，除非有特殊的序列化需求。

## 6. 測試策略

### Unit Tests (JUnit + Mockito + AssertJ)

*   **Domain**
    *   `ProfileTest`：測試 `create` 與 `update` 的驗證邏輯（無效 Email、空姓名等）。
*   **Application**
    *   `CreateProfileServiceTest`：測試成功建立、Email 重複（模擬 Port 拋出例外）、無效輸入。
    *   `GetProfileServiceTest`：測試找到與找不到。
    *   `UpdateProfileServiceTest`：測試成功更新、找不到、Email 重複。
    *   `DeleteProfileServiceTest`：測試成功刪除、找不到。

### Integration Tests (Spring Boot Test + Testcontainers)

*   **Persistence Adapter**
    *   `JpaSaveProfilePortTest` (`@DataJpaTest`)：測試 `save` 與 `saveAndFlush` 在唯一約束違反時的行為，以及正常儲存。
    *   `JpaLoadProfilePortTest` (`@DataJpaTest`)：測試 `findById`。
    *   `JpaDeleteProfilePortTest` (`@DataJpaTest`)：測試 `deleteById`。
*   **Web Adapter**
    *   `ProfileControllerTest` (`@WebMvcTest`)：測試各端點的 HTTP 狀態碼、回應內容、驗證錯誤。使用 `@MockitoBean` 模擬 Use Cases。

### BDD Tests (Cucumber)

*   **Feature File**
    *   `bootstrap/src/test/resources/features/profile-crud.feature`
    *   語言：`# language: zh-TW`
    *   場景：涵蓋 Spec 中的所有場景（成功建立、格式錯誤、重複 Email、查詢、更新、刪除、404 等）。
*   **Step Definitions**
    *   `ProfileStepDefinitions` (`@SpringBootTest` + `@AutoConfigureTestRestTemplate`)
    *   使用 `TestRestTemplate` 發送 HTTP 請求並驗證回應。
    *   使用 `@MockitoBean` 或實際資料庫（若為整合測試）。*決策*：BDD 測試應為整合測試，使用真實資料庫（Testcontainers）。

## 7. 類別清單

| 類別名稱 | 完整路徑 (相對於專案根目錄) | 職責 |
| :--- | :--- | :--- |
| `ProfileId` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | 值物件，個人資料識別碼 |
| `Profile` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | 實體，個人資料資料模型 |
| `ProfileNotFoundException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | 領域例外，資料不存在 |
| `DuplicateEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | 領域例外，電子郵件重複 |
| `InvalidEmailException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java` | 領域例外，無效電子郵件格式 |
| `InvalidNameException` | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidNameException.java` | 領域例外，無效姓名 |
| `CreateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port，建立個人資料介面 |
| `GetProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port，查詢個人資料介面 |
| `UpdateProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port，更新個人資料介面 |
| `DeleteProfileUseCase` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port，刪除個人資料介面 |
| `SaveProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port，儲存個人資料介面 |
| `LoadProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port，載入個人資料介面 |
| `DeleteProfilePort` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port，刪除個人資料介面 |
| `CreateProfileRequest` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/CreateProfileRequest.java` | Use Case 輸入 DTO |
| `UpdateProfileRequest` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/UpdateProfileRequest.java` | Use Case 輸入 DTO |
| `CreateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case 實作，建立個人資料 |
| `GetProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case 實作，查詢個人資料 |
| `UpdateProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case 實作，更新個人資料 |
| `DeleteProfileService` | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case 實作，刪除個人資料 |
| `ProfileController` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller，處理 HTTP 請求 |
| `ProfileControllerAdvice` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java` | 全域異常處理器 |
| `CreateProfileRequestDto` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequestDto.java` | Web 層輸入 DTO |
| `UpdateProfileRequestDto` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequestDto.java` | Web 層輸入 DTO |
| `ProfileResponseDto` | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponseDto.java` | Web 層輸出 DTO |
| `ProfileEntity` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity |
| `ProfileRepository` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository |
| `JpaSaveProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port 實作，儲存個人資料 |
| `JpaLoadProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Port 實作，載入個人資料 |
| `JpaDeleteProfilePort` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Port 實作，刪除個人資料 |
| `ProfileConverter` | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java` | Domain 與 Entity 轉換器 |
| `UseCaseConfiguration` | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | 組裝 Use Case Beans |
| `ProfileStepDefinitions` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileStepDefinitions.java` | Cucumber 步驟定義 |
| `ProfileCrudTest` | `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileCrudTest.java` | Cucumber Runner |
| `profile-crud.feature` | `bootstrap/src/test/resources/features/profile-crud.feature` | BDD Feature 檔 |
