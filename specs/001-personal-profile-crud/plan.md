# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本文件定義了 Spec 001 的技術實現細節，遵循六角形架構（Ports and Adapters），確保領域邏輯獨立於基礎設施。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities

- **ProfileId** (`record`)
    - 內容：`UUID value`
    - 不變條件：值不可為 null。
    - 用途：作為 Domain Entity 的唯一識別符，並由 Application Layer 產生或傳遞。

- **Email** (`record`)
    - 內容：`String address`
    - 不變條件：
        - 不能為 null 或空白。
        - 必須符合標準 Email 格式（使用正則表達式驗證）。
        - 長度限制在 254 字以內。
    - 用途：封裝電子郵件的有效性檢查，確保全域唯一性由外部約束保證，但格式正確性是領域責任。

- **DisplayName** (`record`)
    - 內容：`String name`
    - 不變條件：
        - 允許為 null 或空字串。
        - 若包含內容，去除前後空白後不得全為空白字符。
        - 最大長度 100 字元。
    - 用途：表示使用者的顯示名稱。

- **Bio** (`record`)
    - 內容：`String text`
    - 不變條件：
        - 允許為 null。
        - 若存在，最大長度 500 字元。
    - 用途：使用者簡介。

- **Profile** (Entity)
    - 欄位：
        - `id: ProfileId`
        - `email: Email`
        - `displayName: DisplayName`
        - `bio: Bio`
    - 行為方法：
        - `update(email, displayName, bio)`：執行全量更新邏輯，驗證新值的有效性並更新內部狀態。
    - 不變條件：建立時所有必填欄位必須有效；更新時同樣需滿足有效性。

### Domain Exceptions

- **InvalidEmailException**：當 Email Value Object 格式無效時拋出。
- **InvalidDisplayNameException**：當 Display Name 違反不變條件（如過長、僅含空白）時拋出。
- **DuplicateEmailException**：由 Persistence Layer 捕捉資料庫唯一性衝突後轉換而來，用於告知 Application/Adapter 層此電子郵件已存在。
- **ProfileNotFoundException**：當查詢或刪除操作找不到對應 ID 的資料時拋出。

## 2. Use Cases & Ports

### Inbound Ports (Interfaces)

1. **CreateProfileUseCase**
   ```java
   void execute(CreateProfileRequest request);
   // CreateProfileRequest: record { Email email; DisplayName displayName; Optional<Bio> bio; }
   ```

2. **GetProfileUseCase**
   ```java
   ProfileResult execute(ProfileId id);
   // ProfileResult: record { ProfileId id; Email email; DisplayName displayName; Bio bio; }
   ```

3. **UpdateProfileUseCase**
   ```java
   void execute(UpdateProfileRequest request);
   // UpdateProfileRequest: record { ProfileId id; Email email; DisplayName displayName; Bio bio; }
   ```

4. **DeleteProfileUseCase**
   ```java
   void execute(ProfileId id);
   ```

### Outbound Ports (Interfaces)

1. **SaveProfilePort**
   ```java
   Profile save(Profile profile);
   ```

2. **LoadProfilePort**
   ```java
   Optional<Profile> findById(ProfileId id);
   ```

3. **DeleteProfilePort**
   ```java
   void deleteById(ProfileId id);
   ```

### Use Case Implementations (Services)

- `CreateProfileService`：實作 `CreateProfileUseCase`。驗證輸入，建立 Domain Entity，呼叫 `SaveProfilePort.save()`。若拋出 `DuplicateEmailException` 則向上傳遞。
- `GetProfileService`：實作 `GetProfileUseCase`。呼叫 `LoadProfilePort.findById()`。若為空則拋出 `ProfileNotFoundException`。將 Domain Entity 轉換為 Result DTO 回傳。
- `UpdateProfileService`：實作 `UpdateProfileUseCase`。先呼叫 `LoadProfilePort.findById()` 取得現有實體，若不存在拋出 `ProfileNotFoundException`。呼叫實體的 `update()` 方法更新狀態。最後呼叫 `SaveProfilePort.save()` 持久化。注意處理唯一性衝突異常。
- `DeleteProfileService`：實作 `DeleteProfileUseCase`。先檢查是否存在（可選，或直接刪除並捕捉例外），建議先查詢確認存在以符合業務邏輯嚴謹性，或依賴 Repository 刪除後的行為。根據 Spec，刪除不存在的應返回 404，因此需先 `findById`，若無則拋出 `ProfileNotFoundException`，再調用 `deleteById`。

## 3. Web Adapter (REST API)

### Controller: `ProfileController`
位於套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.web`

- **POST /api/profiles**
    - 映射：`@PostMapping("/profiles")`
    - 參數：`CreateProfileRequestDto dto` (Jakarta Validation)
    - 回應：201 Created + Location Header
    - 錯誤對應：
        - Jakarta Validation Fail -> 400 Bad Request (`MethodArgumentNotValidException`)
        - Domain Invalid Input -> 400 Bad Request (`InvalidEmailException`, etc.)
        - Duplicate Email -> 409 Conflict (`DuplicateEmailException`)

- **GET /api/profiles/{id}**
    - 映射：`@GetMapping("/profiles/{id}")`
    - 參數：`@PathVariable("id") UUID id`
    - 回應：200 OK + Profile Response DTO
    - 錯誤對應：
        - Not Found -> 404 Not Found (`ProfileNotFoundException`)

- **PUT /api/profiles/{id}**
    - 映射：`@PutMapping("/profiles/{id}")`
    - 參數：`@PathVariable("id") UUID id`, `UpdateProfileRequestDto dto`
    - 回應：200 OK
    - 錯誤對應：
        - Not Found -> 404 Not Found (`ProfileNotFoundException`)
        - Duplicate Email -> 409 Conflict (`DuplicateEmailException`)
        - Invalid Input -> 400 Bad Request

- **DELETE /api/profiles/{id}**
    - 映射：`@DeleteMapping("/profiles/{id}")`
    - 參數：`@PathVariable("id") UUID id`
    - 回應：204 No Content
    - 錯誤對應：
        - Not Found -> 404 Not Found (`ProfileNotFoundException`)

### DTOs (Data Transfer Objects)

- `CreateProfileRequestDto`：包含 `email`, `displayName`, `bio`。使用 Jakarta Validation 註解（如 `@Email`, `@NotBlank`/`@Size`）。
- `UpdateProfileRequestDto`：同 Create，但結構相同或可共用基類。
- `ProfileResponseDto`：包含 `id`, `email`, `displayName`, `bio`。用於 GET 回應。

### Error Handling: `GlobalExceptionHandler`
位於套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.web`

- 處理 `MethodArgumentNotValidException` -> 400 + JSON error detail.
- 處理 `HttpMessageNotReadableException` -> 400.
- 處理 `InvalidEmailException`, `InvalidDisplayNameException` -> 400.
- 處理 `DuplicateEmailException` -> 409.
- 處理 `ProfileNotFoundException` -> 404.
- **注意**：不攔截通用 `Exception`，以免覆蓋 Spring Boot 的 404 NoHandlerFound 或其他框架錯誤。

## 4. Persistence Adapter

### JPA Entity: `ProfileEntity`
位於套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

- 欄位：
    - `id`: UUID, Primary Key (非自增，由應用層決定)
    - `email`: String, Unique Constraint, Not Null
    - `display_name`: String, Nullable
    - `bio`: String, Nullable
- 實作 `Persistable<UUID>`：
    - `isNew()`: 回傳一個瞬態標誌 `boolean isNew`。在 `@PostLoad` 和 `@PostPersist` 中設為 false。建立新實體時預設 true。這解決了手動指定 ID 時的 save/update 判斷問題。
- 無 `@Version`。

### Repository Interface: `ProfileRepository`
位於套件 `com.github.chunpingwang.ollamaagentoutcome.adapter.persistence`

- 繼承 `JpaRepository<ProfileEntity, UUID>`
- 額外方法（可選）：`Optional<ProfileEntity> findByEmail(String email)` 用於唯一性檢查（若 Port 設計需要）。但在本設計中，Port 直接操作 Domain Entity，唯一性衝突由資料庫約束觸發異常後捕捉轉換即可，故標準 CRUD 足夠。

### Outbound Ports Implementation

1. **JpaSaveProfilePort** (`implements SaveProfilePort`)
   - 依賴 `ProfileRepository`。
   - `save(Profile profile)`:
     - 將 Domain `Profile` 轉為 `ProfileEntity` (使用 Converter)。
     - 呼叫 `repository.saveAndFlush(entity)`。**必須用 flush** 以立即觸發唯一性約束檢查。
     - 捕捉 `DataIntegrityViolationException`，若原因與 email unique constraint 相關，則拋出 `DuplicateEmailException`。否則重新拋出或包裝為通用 Persistence Exception。
     - 回傳更新後的 Domain Profile (從 Entity 反向轉換或直接返回輸入的 Profile，視實現細節而定，通常 save 後實體狀態已同步)。

2. **JpaLoadProfilePort** (`implements LoadProfilePort`)
   - 依賴 `ProfileRepository`。
   - `findById(ProfileId id)`:
     - 呼叫 `repository.findById(id.value())`。
     - 若有結果，透過 Converter 轉為 Domain `Profile` 並包在 `Optional.of()` 中。
     - 無結果則回傳 `Optional.empty()`。

3. **JpaDeleteProfilePort** (`implements DeleteProfilePort`)
   - 依賴 `ProfileRepository`。
   - `deleteById(ProfileId id)`:
     - 呼叫 `repository.deleteById(id.value())`。

### Converters: `DomainToEntityConverter`, `EntityToDomainConverter`
- 負責 `Profile` <-> `ProfileEntity` 之間的欄位映射。
- 處理 Value Object 到基本型別（String）的轉換。

## 5. Bootstrap Configuration

### Bean Assembly: `UseCaseConfiguration`
位於套件 `com.github.chunpingwang.ollamaagentoutcome.bootstrap`

```java
@Configuration
public class UseCaseConfiguration {

    @Bean
    CreateProfileService createProfileService(CreateProfilePort port, ...) { // 實際依賴需根據 Port 定義調整
        return new CreateProfileService(port); 
    }
    
    // ... 其他 Use Case Services (Get, Update, Delete) 同樣在此註冊為 @Bean
    
    // Persistence Ports Implementation Beans
    @Bean
    SaveProfilePort saveProfilePort(ProfileRepository repository) {
        return new JpaSaveProfilePort(repository);
    }
    
    @Bean
    LoadProfilePort loadProfilePort(ProfileRepository repository) {
        return new JpaLoadProfilePort(repository);
    }
    
    @Bean
    DeleteProfilePort deleteProfilePort(ProfileRepository repository) {
        return new JpaDeleteProfilePort(repository);
    }
}
```
*注意：Application 層的 Service 類別本身不加 `@Component`，僅在配置類中通過 `@Bean` 創建實例，以確保依賴注入由 Spring 管理且符合架構隔離。Adapter 層（Web/Persistence）的 Bean 通常由 Component Scan 自動發現或明確定義，但本專案結構建議將 Application 服務顯式組裝。*

## 6. 測試策略

### Unit Tests (Domain & Application)
- **套件**：同模組下的 `src/test/java/...`
- **工具**：JUnit Jupiter + AssertJ + Mockito
- **重點**：
    - `EmailTest`, `DisplayNameTest`: 驗證 Value Object 的不變條件與建構邏輯。
    - `CreateProfileServiceTest`: 
        - Mock `SaveProfilePort`。
        - 測試成功建立。
        - 測試輸入無效 Email 拋出 Domain Exception。
        - 測試 Port 拋出 `DuplicateEmailException` 時正確向上傳遞。
    - `GetProfileServiceTest`:
        - Mock `LoadProfilePort`。
        - 測試存在時返回結果。
        - 測試不存在時拋出 `ProfileNotFoundException`。
    - `UpdateProfileServiceTest`:
        - Mock `LoadProfilePort` 和 `SaveProfilePort`。
        - 測試更新成功。
        - 測試目標不存在時拋出異常。
        - 測試唯一性衝突處理。
    - `DeleteProfileServiceTest`:
        - Mock `LoadProfilePort` 和 `DeleteProfilePort`。
        - 測試刪除存在資料。
        - 測試刪除不存在的資料拋出異常。

### Integration Tests (Persistence Adapter)
- **套件**：`adapter-persistence/src/test/java/...`
- **工具**：`@DataJpaTest`, Testcontainers PostgreSQL, `@AutoConfigureTestDatabase(replace = NONE)`
- **重點**：
    - `JpaSaveProfilePortTest`: 
        - 使用真實資料庫容器。
        - 測試正常儲存。
        - 測試重複 Email 觸發 `DataIntegrityViolationException` 並轉換為自訂 Domain Exception。
        - 驗證 Entity 的 `Persistable` 邏輯（ID 指定時的行為）。
    - `JpaLoadProfilePortTest`: 查詢正確性與空值處理。
    - `DomainToEntityConverterTest` / `EntityToDomainConverterTest`: 確保數據映射無誤，特別是 Null 值和特殊字元。

### Web Controller Tests
- **套件**：`adapter-web/src/test/java/...`
- **工具**：`@WebMvcTest(ProfileController.class)`, `MockitoBean` for Use Cases, MockMvc
- **重點**：
    - 模擬各 HTTP Method 請求。
    - 驗證狀態碼 (201, 200, 204, 400, 404, 409)。
    - 驗證 Location Header (POST)。
    - 驗證錯誤回應 JSON 結構。
    - 驗證 Jakarta Validation 失敗情況。

### BDD Tests (Cucumber)
- **套件**：`bootstrap/src/test/java/com/github/chunpingwang.ollamaagentoutcome/bdd/`
- **Feature File**: `features/personal-profile-crud.feature`
- **Runner**: 使用骨架提供的 Cucumber Runner，繼承 Spring Context。
- **步驟定義**：
    - 使用 `TestRestTemplate` 發送真實 HTTP 請求至嵌入式測試伺服器（由 `@SpringBootTest` + Testcontainers 提供）。
    - 實現 Gherkin 場景中的假設、當、那麼步驟。
    - 確保資料庫在每個場景前被清空或重置（可透過 Truncate Tables 或在 Setup 階段建立特定資料）。

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java` | Email Value Object, 格式與不變條件驗證 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayName.java` | Display Name Value Object, 長度與空白處理 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Bio.java` | Bio Value Object, 長度限制 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileId.java` | Profile Unique Identifier Record |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/Profile.java` | Domain Entity, 包含業務邏輯與狀態更新 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java` | 重複電子郵件領域例外 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java` | 無效 Email 格式領域例外 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidDisplayNameException.java` | 無效顯示名稱領域例外 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java` | 找不到個人資料領域例外 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port: 建立資料夾請求介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port: 查詢資料夾請求介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port: 更新資料夾請求介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port: 刪除資料夾請求介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port: 儲存資料夾介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port: 載入資料夾介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port: 刪除資料夾介面 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation: 建立邏輯 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation: 查詢邏輯 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation: 更新邏輯 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation: 刪除邏輯 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDto.java` | REST Request DTO for Create |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDto.java` | REST Request DTO for Update |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponseDto.java` | REST Response DTO |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java` | REST Controller, HTTP Mapping & Validation |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/error/GlobalExceptionHandler.java` | Global Exception Handler for Web Layer |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` | JPA Entity with Persistable support |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileRepository.java` | Spring Data JPA Repository Interface |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java` | Outbound Port Implementation: Save |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java` | Outbound Port Implementation: Load |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfilePort.java` | Outbound Port Implementation: Delete |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/DomainToEntityConverter.java` | Converter Domain -> Entity |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/EntityToDomainConverter.java` | Converter Entity -> Domain |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java` | Spring Configuration for Use Case Beans & Ports |
