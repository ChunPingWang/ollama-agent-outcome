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
- **Profile** (`class`, immutable state via methods, or mutable for JPA mapping in adapter? -> Domain Entity is usually mutable in DDD aggregates but we keep it simple here as a POJO with validation logic)
    - *修正*：根據 System Prompt "Domain 物件以 record 或不可變類別為主"，但 JPA Entity 需要 setter 或建構子。為了簡化並遵循提示中的 "JPA Entity... 轉換器建立新 Entity 時保持 isNew = true"，我們讓 `Profile` 成為一個簡單的領域實體（有狀態），但在 Application/Adapter 層處理持久化細節。或者更純粹地，將 Profile 定義為包含 Value Objects 的聚合根。
    - 屬性：`id: ProfileId`, `email: Email`, `displayName: DisplayName` (nullable)。
    - 業務規則：
        - 建立時必須提供有效的 Email 和 Display Name。
        - 更新時可修改 Display Name；Email 通常不建議在個人檔案中隨意更改以避免衝突，但 Spec 顯示 PUT 請求包含 email，故允許更新，但需檢查唯一性。

### Domain Exceptions
- **DuplicateEmailException**: 當嘗試儲存具有已存在電子郵件的個人資料時拋出。
- **ProfileNotFoundException**: 當查詢或操作不存在的 ID 時拋出。
- **InvalidEmailException**: 當提供的電子郵件格式無效時拋出。

## 2. Use Cases & Ports

### Inbound Ports (`application.port.in`)
1. **CreateProfileUseCase**
   - `void execute(CreateProfileRequest request)`
     - 參數：包含 email, displayName。
     - 行為：驗證輸入 -> 呼叫 SavePort -> 轉換結果。
2. **GetProfileUseCase**
   - `Optional<Profile> execute(ProfileId id)`
     - 行為：呼叫 LoadPort。
3. **UpdateProfileUseCase**
   - `void execute(UpdateProfileRequest request)`
     - 參數：id, email, displayName。
     - 行為：先 Load (若不存在拋出 NotFoundException) -> 驗證新資料 -> 呼叫 SavePort。
4. **DeleteProfileUseCase**
   - `void execute(ProfileId id)`
     - 行為：先 Load (若不存在拋出 NotFoundException) -> 呼叫 DeletePort。

### Outbound Ports (`application.port.out`)
1. **SaveProfilePort**
   - `Profile save(Profile profile)`
2. **LoadProfilePort**
   - `Optional<Profile> findById(ProfileId id)`
3. **DeleteProfilePort**
   - `void deleteById(ProfileId id)`

## 3. Web Adapter (`adapter.web`)

- **Controller**: `ProfileController`
    - `POST /api/profiles`: 對應 `CreateProfileUseCase`。回傳 201 + Location header。
    - `GET /api/profiles/{id}`: 對應 `GetProfileUseCase`。回傳 200 或 404。
    - `PUT /api/profiles/{id}`: 對應 `UpdateProfileUseCase`。回傳 200 或 404/409。
    - `DELETE /api/profiles/{id}`: 對應 `DeleteProfileUseCase`。回傳 204 或 404。
- **DTOs**:
    - `CreateProfileRequest`: `email`, `displayName`. (Jakarta Validation: @Email, not blank).
    - `UpdateProfileRequest`: `email`, `displayName`. (Same validation as Create).
    - `ProfileResponse`: `id`, `email`, `displayName`.
- **Error Handling**: `GlobalExceptionHandler` (@RestControllerAdvice)
    - `InvalidInputException` (from DTO validation or Domain InvalidEmail): -> 400 Bad Request.
    - `DuplicateEmailException`: -> 409 Conflict.
    - `ProfileNotFoundException`: -> 404 Not Found.

## 4. Persistence Adapter (`adapter.persistence`)

- **Entity**: `ProfileJpaEntity`
    - 實作 `Persistable<UUID>` 以處理 ID 生成邏輯（若使用 DB auto-gen）或手動 UUID。Spec 中測試場景使用了固定的 UUID，暗示客戶端可能傳遞 ID 或由系統產生。通常 REST API 建立時不返回客戶端提供的 ID，而是由伺服器產生。但為了簡化且符合 "Domain Id" 概念，我們假設 ProfileId 是領域產生的 UUID。
    - *修正*：System Prompt 提到 "Domain 若自行產生識別碼... JPA Entity 不可同時使用 @GeneratedValue"。我們將讓 `Profile` 在建立時產生 UUID，並透過轉換器傳入。因此 Entity 不需要 `@GeneratedValue`，ID 欄位為普通欄位。
    - 欄位：`id` (String/UUID), `email`, `displayName`.
    - 唯一約束：`UNIQUE(email)`。
- **Repository**: `ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, String>, CustomProfilePort`? No, Repository is just Spring Data interface. The Port implementation wraps it.
    - Interface: `ProfileJpaRepository extends CrudRepository<ProfileJpaEntity, String>`
- **Port Implementation**: `JpaSaveProfilePort`, `JpaLoadProfilePort`, `JpaDeleteProfilePort` (or one combined class). Let's split for SOLID as per prompt suggestion ("port interfaces small and specific").
    - Actually, to reduce boilerplate, we can have one adapter class implementing multiple ports or separate classes. Given the strict naming convention (`UseCase` -> `Service`, `Port` interface), let's create specific port implementations if needed, but often a single `ProfilePersistenceAdapter` implements all outbound ports for an aggregate. However, the prompt says "outbound port interface always ends with Port". It doesn't force separate impls. I will use separate beans for clarity in configuration if they are distinct, or one bean. Let's stick to one `ProfilePersistenceAdapter` implementing `SaveProfilePort`, `LoadProfilePort`, `DeleteProfilePort` to keep it simple, OR follow the pattern of creating specific services. The prompt example shows `DeleteProfilePort`. I'll create individual port interfaces and their implementations.

## 5. Bootstrap

- **Configuration**: `UseCaseConfiguration` (@Bean definitions)
    - `CreateProfileService`
    - `GetProfileService`
    - `UpdateProfileService`
    - `DeleteProfileService`
    - JPA Ports: `JpaSaveProfilePort`, etc., wired into Services.
- **Cucumber Steps**: In `bdd` package, using `TestRestTemplate`.

## 6. Testing Strategy

- **Domain Tests** (JUnit + AssertJ): Validate Value Objects (Email format, null checks).
- **Application Tests** (JUnit + Mockito): Mock Outbound Ports. Verify Use Case logic (e.g., throws DuplicateEmailException on save failure).
- **Web Adapter Tests** (`@WebMvcTest`): Mock Inbound Ports. Verify HTTP status codes and JSON structure.
- **Persistence Tests** (`@DataJpaTest` + Testcontainers): Verify Entity mapping, Unique Constraint enforcement, and Port implementation correctness.
- **BDD/Cucumber**: End-to-end tests against a running context with H2 or PostgreSQL via Testcontainers. Feature file: `personal-profile-crud.feature`.

## 7. Class List

| Category | Path (Relative to Root) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java` | Value Object for Profile ID |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Email.java` | Value Object for Email validation |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DisplayName.java` | Value Object for Display Name |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java` | Aggregate Root / Entity |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java` | Domain Exception |
| Domain | `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java` | Domain Exception |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java` | Inbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` | Use Case Implementation |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java` | Inbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` | Use Case Implementation |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java` | Inbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` | Use Case Implementation |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java` | Inbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` | Use Case Implementation |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java` | Outbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java` | Outbound Port Interface |
| Application | `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` | Outbound Port Interface |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java` | REST Controller |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java` | Request DTO |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java` | Request DTO |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java` | Response DTO |
| Web Adapter | `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/GlobalExceptionHandler.java` | Error Handling |
| Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaEntity.java` | JPA Entity |
| Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileJpaRepository.java` | Spring Data Repository |
| Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java` | Port Implementation |
| Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java` | Port Implementation |
| Persistence | `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java` | Port Implementation |
| Bootstrap | `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/UseCaseConfiguration.java` | Bean Assembly |
