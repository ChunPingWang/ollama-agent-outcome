# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Ports and Adapters），確保領域邏輯獨立於框架細節。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
- **ProfileId** (`record`)
    - 內容：`UUID id`
    - 不變條件：不可為 null。
- **Email** (`record`)
    - 內容：`String value`
    - 不變條件：符合 RFC 5322 基本格式（使用正則表達式驗證）、全域唯一性由 Repository Port 保證、非空白。
- **DisplayName** (`record`)
    - 內容：`String value`
    - 不變條件：允許為 null 或空字串（表示未設定）。若僅含空白字符視為空。
- **Profile** (`class`, immutable state via methods, or mutable for JPA mapping in adapter? -> Domain Entity is usually domain-centric. For simplicity with JPA, we keep it as a standard class but enforce immutability of ID creation logic if needed, though here ID is assigned by DB/UUID generator strategy. Let's use `Persistable<UUID>` interface on the entity to handle isNew status correctly per system prompt rules.)
    - 屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName`
    - 行為：
        - `create(email, displayName)`: Factory method or static factory to create new instance.
        - `update(displayName, email)`: Updates fields after validation. Note: Email uniqueness is checked at persistence layer/port level, but domain might validate format again. The spec says "Update", implying full replacement of provided fields.

### Domain Exceptions
- **DuplicateEmailException**: 當嘗試建立或更新資料時，電子郵件已存在於系統中。
- **InvalidEmailFormatException**: 當輸入的電子郵件格式不符合規範時。
- **ProfileNotFoundException**: 當查詢或操作不存在的個人資料識別碼時（通常由 Application Layer 拋出，但也可定義為通用 Domain Exception）。*修正：根據 SOLID，Domain Exception 應反映領域概念。找不到資源通常是應用層或基礎設施層的狀態，但在 CRUD 操作中，"不存在" 常被視為一種業務規則違反（如刪除不存在的東西）。為了簡化，我們將 `ProfileNotFoundException` 放在 Domain 作為通用例外，或在 Application 層處理。鑑於 Spec 要求明確的狀態碼，建議在 Application Port 實作中捕捉 JPA 異常並轉換為此 Domain Exception，或直接由 UseCase 檢查後拋出。這裡將 `ProfileNotFoundException` 定義在 Domain 以保持一致性。*

## 2. Use Cases & Ports

### Inbound Ports (Interfaces)
1. **CreateProfileUseCase**
   - `void execute(CreateProfileRequest request)`
     - 參數：包含 email, displayName 的 DTO/Value Object。
     - 行為：驗證輸入 -> 呼叫 SavePort -> 無回傳值（成功即代表建立完成）。
2. **GetProfileUseCase**
   - `Optional<Profile> execute(UUID profileId)`
     - 參數：目標 ID。
     - 行為：呼叫 LoadPort -> 若存在則返回 Optional，否則返回空 Optional。
3. **UpdateProfileUseCase**
   - `void execute(UpdateProfileRequest request)`
     - 參數：id, newDisplayName, newEmail。
     - 行為：先 Load -> 若不存在拋出 ProfileNotFoundException -> 更新實體狀態 -> 呼叫 SavePort。
4. **DeleteProfileUseCase**
   - `void execute(UUID profileId)`
     - 參數：目標 ID。
     - 行為：先 Load -> 若不存在拋出 ProfileNotFoundException -> 呼叫 DeletePort。

### Outbound Ports (Interfaces in Application Layer)
1. **SaveProfilePort**
   - `void save(Profile profile)`
2. **LoadProfilePort**
   - `Optional<Profile> findById(UUID id)`
3. **DeleteProfilePort**
   - `void deleteById(UUID id)`

### Use Case Implementations
- `CreateProfileService`
- `GetProfileService`
- `UpdateProfileService`
- `DeleteProfileService`

## 3. Web Adapter

### Controller
- **ProfileController**: `@RestController`, path `/api/profiles`
    - `POST /`: `create(CreateProfileDto dto)` -> 201 Created + Location header.
    - `GET /{id}`: `get(@PathVariable("id") UUID id)` -> 200 OK with Response DTO, or 404.
    - `PUT /{id}`: `update(@PathVariable("id") UUID id, UpdateProfileDto dto)` -> 200 OK, or 404/409/400.
    - `DELETE /{id}`: `delete(@PathVariable("id") UUID id)` -> 204 No Content, or 404.

### Request/Response DTOs (Jakarta Validation)
- **CreateProfileRequestDto**:
    - `email`: String, @NotBlank, @Email.
    - `displayName`: String, nullable.
- **UpdateProfileRequestDto**:
    - `email`: String, @NotBlank, @Email.
    - `displayName`: String, nullable.
- **ProfileResponseDto**:
    - `id`: String (UUID string representation).
    - `email`: String.
    - `displayName`: String (nullable).

### Error Handling (`@RestControllerAdvice`)
- `MethodArgumentNotValidException` -> 400 Bad Request.
- `HttpMessageNotReadableException` -> 400 Bad Request.
- `DuplicateEmailException` -> 409 Conflict.
- `ProfileNotFoundException` -> 404 Not Found.
- *注意*：不要攔截通用 Exception，以免覆蓋 Spring Boot 的 404/500 預設行為。

## 4. Persistence Adapter

### JPA Entity
- **ProfileEntity** (`jakarta.persistence.Entity`, implements `Persistable<UUID>`)
    - Fields: `id` (UUID, PK), `email` (String, Unique, Not Null), `display_name` (String, Nullable).
    - Logic for `isNew()`: Use a transient boolean flag set to true on creation and updated via lifecycle callbacks or constructor logic in the converter. Specifically, when converting from Domain Profile (which might not know if it's new) to Entity, we need to handle this. Since Domain doesn't track persistence state, the Converter will create a fresh Entity with `isNew = true`. The Repository save method needs to distinguish insert vs update? No, `saveAndFlush` handles both based on ID presence and isNew status. If ID is present but isNew is false, it updates. Wait, standard JPA `save` checks if ID exists in DB. To be safe per system prompt: "Domain若自行產生識別碼...讓Entity實作Persistable". Here UUID is likely generated by client or server before saving? Spec says POST creates. Usually UUID is generated by app/domain layer before calling port. So ID is always present. We must rely on `isNew` flag.
    - Implementation detail: When creating a new profile, the Application Layer passes a Profile object with an assigned UUID. The Adapter converts this to Entity. How does Entity know it's new? It can't infer from data alone unless we use a specific strategy. System Prompt suggests: "轉換器建立新 Entity 時保持 isNew = true". This implies the Converter has context of whether it's a creation or update operation, OR the Port implementation sets it. Better approach: The `SaveProfilePort` implementation receives the entity. If it's a brand new domain object (created via factory), set `entity.setNew(true)`. But Domain objects are immutable usually. Let's make `Profile` have a method `markAsSaved()`? No.
    - Refined Strategy for Persistence:
        1. Domain `Profile` contains `id`, `email`, `displayName`.
        2. In `CreateProfileService`, after validation, we call `savePort.save(profile)`.
        3. In `JpaSaveProfilePort`: Convert `profile` -> `ProfileEntity`. Since this is a create flow, explicitly set `entity.setIsNew(true)` before saving. For Update flows (`UpdateProfileService`), load existing entity, merge changes, and save. The loaded entity will have `isNew=false`.
        4. Wait, if I just pass the same interface to Save, how does the port know? The port signature is `void save(Profile profile)`. It doesn't carry metadata about "is this from Create or Update?".
        5. Alternative: Use two ports? Or check existence in DB first inside the port? Checking existence defeats performance.
        6. Best Practice with Persistable: When converting *from* Domain *to* Entity during creation, force `isNew=true`. During update, we modify an existing managed entity (loaded by ID), so its state is already known. So, only the Converter used in Create needs to ensure `isNew=true`. But they share one converter class usually.
        7. Solution: Add a method to Port? No, keep ports simple. Let's look at System Prompt again: "轉換器建立新 Entity 時保持 isNew = true". This implies the conversion logic itself might need to be aware, OR we use different converters/ports for Create vs Update? That violates DRY.
        8. Practical Fix: In `CreateProfileService`, before calling `savePort.save(profile)`, we can wrap it or handle it. Actually, simplest way compliant with prompt: The `SaveProfilePort` implementation checks if the entity exists in DB using `findById`. If not found, treat as insert (`setIsNew(true)`). If found, treat as update. This adds overhead but ensures correctness without complex flags. However, Spec says "Unique constraint... catch DataIntegrityViolationException".
        9. Let's stick to the prompt's hint: "Domain若自行產生識別碼...讓Entity實作Persistable...轉換器建立新Entity時保持isNew=true". I will implement a specific conversion strategy where the Application Layer explicitly marks the intent, OR simply rely on the fact that for CREATE, we pass a fresh object. For UPDATE, we load an existing object and modify it *in place* (if mutable) or create a new one from loaded data. Since Domain is immutable record-like, we must reload -> transform -> save.
        10. Refined Plan: 
            - `UpdateProfileService`: Loads Profile via LoadPort. Creates a NEW Profile instance with updated fields. Calls SavePort.
            - Now both Create and Update call `SavePort.save(newOrExistingProfile)`. How does Port distinguish? It MUST check existence in DB first if ID is present but isNew status is ambiguous. 
            - To optimize: We can add a marker interface or just accept the extra query for updates. Given this is a simple CRUD, checking existence before saving is acceptable for consistency.
            - Actually, simpler: In `Create` flow, use a dedicated `JpaCreateProfilePort`? No, keep ports unified per spec structure usually. Let's assume the standard pattern: The Repository handles persistence state. If we use `saveAndFlush`, JPA determines insert/update based on whether the primary key exists in the database IF `Persistable.isNew()` returns true/false correctly.
            - I will implement `JpaSaveProfilePort` such that it checks `repository.existsById(id)`. If false, set entity to new and persist. If true, merge. This guarantees correct behavior regardless of how the domain object was created.

### Spring Data Repository
- **ProfileRepository**: Extends `CrudRepository<ProfileEntity, UUID>`
    - Custom method (optional): `boolean existsByEmail(String email)` for validation optimization (though unique constraint catches duplicates). Using `existsByEmail` allows early rejection with 409 instead of waiting for flush exception.

### Port Implementations
- **JpaLoadProfilePort**: Uses `ProfileRepository.findById()`. Converts Entity -> Domain Profile.
- **JpaSaveProfilePort**: 
    - Checks if profile ID already exists in DB (`repository.existsById`).
    - If not exists: Convert Domain -> Entity, set `isNew=true`, call `saveAndFlush`.
    - If exists: Load existing Entity, update fields from Domain, call `saveAndFlush`.
    - Catch `DataIntegrityViolationException` -> throw `DuplicateEmailException`.
- **JpaDeleteProfilePort**: Calls `repository.deleteById(id)`.

## 5. Bootstrap

- **UseCaseConfiguration** (`@Configuration`)
    - Defines beans for all Use Case implementations (`CreateProfileService`, etc.).
    - Injects outbound ports into use cases via constructor arguments.
    - *Note*: Do NOT define port beans here; they are auto-configured or defined in their respective adapter modules' configurations? System prompt says "bootstrap...組裝use case bean". Usually, Ports interfaces are in Application module, Implementations in Persistence module. Spring Boot component scanning should pick them up IF annotated with `@Component`/`@Service`. However, strict Hexagonal often avoids annotations on adapters and wires manually. The prompt says: "application 定義 outbound port 介面，adapter 實作；以建構子注入". And "bootstrap 的 @SpringBootApplication ... UseCaseConfiguration ... 只修改 UseCaseConfiguration". It implies we might need to import the configuration classes of adapters OR rely on standard boot scanning if adapters have `@Repository`/`@Service`. 
    - Given "adapter-persistence" has JPA tests, it likely uses `@DataJpaTest` which scans repositories. For full app context, `@SpringBootApplication` scans base package. If I put `@Repository` on `ProfileRepository` and `@Service` on Port Impl, they will be found. BUT, system prompt warns about BeanDefinitionOverrideException if not careful. 
    - Safest approach per prompt: Define explicit `@Bean` methods for **Use Cases** only in `UseCaseConfiguration`. Let Spring auto-wire the Ports (assuming they are beans). To ensure Ports are beans without `@Service`, we can define them as `@Bean`s in a separate config or just use `@Repository`/`@Service` annotations on the adapter implementations since they are part of the main application scan path (`com.github.chunpingwang.ollamaagentoutcome`). The prompt says "禁止 Spring annotation... domain", but doesn't forbid them in Adapters. In fact, Adapters *are* infrastructure code often annotated.
    - Correction: Prompt says "application 定義 outbound port 介面，adapter 實作". It does NOT explicitly ban `@Component` on adapters. However, to be strictly decoupled, some prefer XML-style Java Config. But with Boot 4, scanning is standard. I will add `@Repository` to JPA Repo and `@Service` to Port Implementations so they are picked up by component scanning, then wire UseCases manually in `UseCaseConfiguration` to control their lifecycle/scoping if needed, or let Autowiring handle it? No, `UseCaseConfiguration` must define them. So: 
        1. Adapter classes have `@Repository` / `@Service`.
        2. `UseCaseConfiguration` has `@Bean CreateProfileService(...)` etc., taking Ports as args. This overrides any auto-created bean if name conflicts, but here names differ (Port vs Service).

## 6. Testing Strategy

- **Domain Tests** (`domain/src/test/java/...`)
    - `EmailTest`: Validate format regex, null handling.
    - `DisplayNameTest`: Null/blank handling.
    - `ProfileFactoryTest`: Verify creation logic.
- **Application Tests** (`application/src/test/java/...`)
    - `CreateProfileServiceTest`: Mock `SaveProfilePort`. Test success (201 equivalent) and duplicate email exception throwing.
    - `GetProfileServiceTest`: Mock `LoadProfilePort`. Test present/absent cases.
    - `UpdateProfileServiceTest`: Mock Load/Save. Test update existing, fail on missing ID.
    - `DeleteProfileServiceTest`: Mock Load/Delete. Test delete existing, fail on missing ID.
- **Web Adapter Tests** (`adapter-web/src/test/java/...`)
    - `ProfileControllerIT` or `ProfileControllerTest` using `@WebMvcTest`.
    - Mock the Use Cases directly? No, `@WebMvcTest` loads MVC layer. We need to mock the dependencies of Controller. The Controller depends on Inbound Ports (UseCases). So we use `@MockBean CreateProfileUseCase`, etc.
    - Verify HTTP status codes: 201, 409, 400, 200, 404, 204.
    - Verify JSON structure in responses.
- **Persistence Adapter Tests** (`adapter-persistence/src/test/java/...`)
    - `JpaProfileRepositoryTest`: Using `@DataJpaTest` + Testcontainers.
        - Test unique constraint violation -> `DuplicateKeyException` / `DataIntegrityViolationException`.
        - Test basic CRUD operations with JPA lifecycle.
    - `JpaLoadProfilePortTest`: Integration test for conversion and loading.
    - `JpaSaveProfilePortTest`: Integration test for save logic including isNew handling.
- **BDD/Cucumber Tests** (`bootstrap/src/test/resources/features/`)
    - File: `personal-profile-crud.feature`
    - Steps defined in `bdd.PersonalProfileSteps`.
    - Uses `TestRestTemplate` to hit real endpoints against a Testcontainer PostgreSQL instance.

## 7. 類別清單

| 模組 | 完整路徑 (相對專案根目錄) | 職責描述 |
| :--- | :--- | :--- |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object，封裝電子郵件格式驗證與儲存。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object，封裝顯示名稱（允許空值）。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object，UUID 識別碼包裝。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Domain Entity，包含核心屬性與業務行為。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception，表示電子郵件重複。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailFormatException.java` | Domain Exception，表示郵件格式無效。 |
| domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Domain Exception，表示找不到指定 ID 的個人資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port 介面：建立個人資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port 介面：查詢個人資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port 介面：更新個人資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port 介面：刪除個人資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port 介面：儲存資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port 介面：載入資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port 介面：刪除資料。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case 實作，協調建立流程與驗證。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case 實作，負責查詢邏輯。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case 實作，負責更新流程（先查後改）。 |
| application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case 實作，負責刪除流程（先查後刪）。 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller，處理 HTTP 請求並映射至 Use Cases。 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDto.java` | DTO，接收建立請求的 JSON 資料。 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDto.java` | DTO，接收更新請求的 JSON 資料。 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponseDto.java` | DTO，輸出個人資料回應格式。 |
| adapter-web | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/WebExceptionHandler.java` | 全域異常處理器，將 Domain Exception 轉為對應 HTTP 狀態碼。 |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java` | JPA Entity，對應資料庫表格，實作 Persistable。 |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java` | Spring Data Repository，提供基礎 CRUD 與自訂查詢。 |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Port 實作，負責從 DB 載入並轉換為 Domain Object。 |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port 實作，處理新增/更新邏輯及唯一性檢查。 |
| adapter-persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Port 實作，執行物理刪除。 |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/Application.java` | Spring Boot 啟動入口。 |
| bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Configuration，組裝 Use Case Bean 及其依賴的 Ports。 |
