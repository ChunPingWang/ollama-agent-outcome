# Plan 001 — 個人資料管理 (Personal Profile CRUD)

本計畫基於 Spec 001 設計，採用六角形架構（Ports and Adapters），確保領域邏輯獨立於框架細節。

## 1. 領域模型 (Domain Model)

### Value Objects & Entities
- **ProfileId** (`record`)
    - 內容：`String value` (UUID String)。
    - 不變條件：非空字串，符合 UUID 格式（由建構子或靜態工廠驗證）。
- **Email** (`record`)
    - 內容：`String address`。
    - 不變條件：非空，符合標準 Email 正規表示式。
- **Age** (`record`)
    - 內容：`int years`。
    - 不變條件：$0 \le years \le 150$。
- **Name** (`record`)
    - 內容：`String text`。
    - 不變條件：非空，長度 $1 \dots 255$。
- **Profile** (`class`, immutable state via builder or factory methods, but mutable for JPA proxy compatibility in domain context if needed, however per spec we use record-like structure with explicit update method)
    - *修正*：為了配合 JPA Entity 的代理物件需求與 Domain Service 的不可變性原則，Domain Layer 定義 `Profile` 為一個包含狀態的類別，但業務邏輯通過 Factory/Builder 建立新實例或在應用層處理更新。鑑於 Spec 要求「全量更新」且需檢查唯一性，我們採用以下設計：
    - Fields: `id (ProfileId)`, `name (Name)`, `email (Email)`, `age (Age)`。
    - Methods: `update(Name, Email, Age)` -> 回傳新的 Profile 或自我更新（視領域模型風格而定）。在此採用 **Command-based Update**：在 Application Port 接收 Command，Domain Object 提供 `validate()` 方法供 Use Case 調用，或使用 Builder 模式重建對象以確保不可變性（推薦）或受控的可变性。
    - *決定*：為簡化測試與架構清晰度，Domain `Profile` 將是一個具有私有建構子、靜態工廠方法 `create(id, name, email, age)` 和 `update(name, email, age)` 的類別。`update` 會執行領域規則驗證並返回新的 `Profile` 實例（若使用 immutable records）或修改自身（若為了 JPA 方便）。
    - *最終決定*：遵循 System Prompt "record" 建議及 SOLID，Domain Entity 應盡可能不可變。但 JPA 需要 setter/proxy。折衷方案：**JPA Entity 放在 Adapter Layer**。Domain Layer 定義純 Java 的 `Profile` record/class，不含 JPA annotation。Adapter 負責轉換。

### Domain Exceptions
- `InvalidEmailException`: 電子郵件格式無效。
- `DuplicateEmailException`: 嘗試建立或更新時，電子郵件已存在於系統中（由 Domain Service 或 Application 層結合 Repository 檢查後拋出，通常此業務規則屬於應用層或領域服務，這裡歸類為 Domain Exception 以便統一處理）。
- `ProfileNotFoundException`: 指定的 ID 不存在。
- `InvalidAgeException`: 年齡超出合理範圍。
- `InvalidNameException`: 姓名不符合規範。

## 2. Use Cases & Ports (Application Layer)

### Inbound Ports (`port.in`)
1. **CreateProfileUseCase**
   - `void execute(CreateProfileCommand command)`
     - Command: `name`, `email`, `age`.
2. **GetProfileUseCase**
   - `ProfileDto execute(String profileId)`
3. **UpdateProfileUseCase**
   - `void execute(UpdateProfileCommand command)`
     - Command: `id`, `name`, `email`, `age`.
4. **DeleteProfileUseCase**
   - `void execute(String profileId)`

### Outbound Ports (`port.out`)
1. **SaveProfilePort**
   - `void save(Profile profile)`
2. **LoadProfilePort**
   - `Optional<Profile> findById(String id)`
3. **ExistsByEmailPort** (可選，若要在 domain/application 層檢查唯一性)
   - `boolean existsByEmail(String email)`
   - *註*：為保持 Port 最小化，也可在 Save 時由 Repository 處理唯一性約束並拋出異常，再由 Application 捕捉轉發。這裡採用 **SaveProfilePort.save()** 內部處理或依賴 DB constraint + Exception translation。為了明確的業務規則驗證（如重複 Email），Application Layer 通常會先查詢是否存在，或者讓 Persistence Adapter 拋出特定異常。鑑於 Spec 要求 409 Conflict，我們定義一個 `DuplicateEmailException` 在 Domain/Adapter 間傳遞。

### Use Case Implementations (`usecase`)
- `CreateProfileService`: 實現 `CreateProfileUseCase`。驗證輸入 -> 建立 Domain Profile -> 呼叫 `saveProfilePort.save()`。
- `GetProfileService`: 實現 `GetProfileUseCase`。呼叫 `loadProfilePort.findById()` -> 轉換為 DTO -> 返回。若無則拋出 `ProfileNotFoundException`。
- `UpdateProfileService`: 實現 `UpdateProfileUseCase`。先 `findById`，若不存在拋出 `ProfileNotFoundException`；否則執行領域更新邏輯 (validate new data, check duplicate email against other profiles) -> 保存。
- `DeleteProfileService`: 實現 `DeleteProfileUseCase`。先檢查存在與否，再刪除（或直接調用 delete，由 Repository 處理 404？不，Spec 要求 DELETE 不存在的回 404，所以需先 Load）。*修正*：JPA `deleteById` 在不存時通常不回傳錯誤，但為了符合 HTTP 404 語義，Application Layer 應先 `findById`，若空則拋異常，或依賴 Adapter 的 delete 方法在找不到時拋出特定異常。這裡採用 **先查後刪** 策略以確保一致性並提供明確錯誤訊息。

## 3. Web Adapter (`adapter-web`)

### Controller
- **ProfileController**: `@RestController`, path `/profiles`.
    - `POST /profiles`: 映射到 `CreateProfileUseCase.execute()`.
        - Request Body: `CreateProfileRequestDTO` (name, email, age).
        - Response: 201 Created + Location header.
    - `GET /profiles/{id}`: 映射到 `GetProfileUseCase.execute()`.
        - Path Variable: `id`.
        - Response: 200 OK + `ProfileResponseDTO`.
    - `PUT /profiles/{id}`: 映射到 `UpdateProfileUseCase.execute()`.
        - Path Variable: `id`.
        - Request Body: `UpdateProfileRequestDTO` (name, email, age). *注意：Spec 說全量更新，所以 DTO 包含所有字段*。
        - Response: 200 OK.
    - `DELETE /profiles/{id}`: 映射到 `DeleteProfileUseCase.execute()`.
        - Path Variable: `id`.
        - Response: 204 No Content.

### DTOs
- `CreateProfileRequestDTO`: `String name`, `String email`, `int age`. (Jakarta Validation annotations)
- `UpdateProfileRequestDTO`: `String name`, `String email`, `int age`.
- `ProfileResponseDTO`: `String id`, `String name`, `String email`, `int age`.

### Error Handling (`@RestControllerAdvice`)
- `MethodArgumentNotValidException` -> **400 Bad Request**.
- `InvalidEmailException`, `InvalidAgeException`, etc. -> **400 Bad Request** (Domain validation failure on input).
- `DuplicateEmailException` -> **409 Conflict**.
- `ProfileNotFoundException` -> **404 Not Found**.
- `HttpMessageNotReadableException` -> **400 Bad Request**.

## 4. Persistence Adapter (`adapter-persistence`)

### JPA Entity
- **ProfileEntity**:
    - Fields: `id` (UUID/String), `name`, `email`, `age`.
    - Annotations: `@Entity`, `@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))`.
    - Implements `Persistable<String>`? Spec 未明確要求 UUID generation strategy in DB, but Domain uses String ID. To avoid optimistic locking issues with generated IDs vs managed IDs, we will use a simple `String` ID passed from Application layer or Generated by App Layer (UUID). Let's assume App Layer generates UUID for new profiles to keep it simple and consistent with "User provides ID" isn't the case here, User doesn't provide ID in Create. So App/Adapter must generate UUID.
    - *修正*：Spec US-1 用戶不傳 ID。因此 ID 由系統生成（UUID）。JPA Entity 使用 `@Id String id`，在轉換時由 Java 層生成 UUID 並設定給 Entity，不使用 DB auto-increment。這樣符合 `Persistable` 陷阱提示中的「Domain 若自行產生識別碼...讓 Entity 實作 Persistable」。我們將讓 `ProfileEntity` 實現 `Persistable<String>`，並在 Adapter 的 Converter 中處理 `isNew` 狀態。

### Repository
- **ProfileRepository**: extends `CrudRepository<ProfileEntity, String>`.
    - Method: `Optional<ProfileEntity> findByEmail(String email)`.

### Port Implementations (`port.out`)
- **SaveProfilePortImpl** (implements `SaveProfilePort`):
    - Convert Domain Profile -> JPA Entity.
    - Call `repository.saveAndFlush(entity)` (to trigger unique constraint immediately in test tx).
    - Catch `DataIntegrityViolationException`, wrap as `DuplicateEmailException`.
- **LoadProfilePortImpl** (implements `LoadProfilePort`):
    - Find by ID via repository.
    - Convert JPA Entity -> Domain Profile.
    - Return Optional.empty() if not found.
- **ExistsByEmailPortImpl**? Or integrate into Save? 
    - For Update case, we need to check if new email is taken by *another* profile. So a separate port or method on existing repo might be needed. Let's add `boolean existsByEmailExcludingId(String email, String id)` to the persistence layer logic, exposed via an additional port `CheckUniqueEmailPort` or simply handle it inside `UpdateProfileService` using a dedicated query in Repo. To keep ports minimal, let's add `existsByEmail(String email)` and `existsByEmailNotId(String email, String excludeId)` to the Repository interface directly if Spring Data supports it, or create a specific Port `FindProfileByEmailPort`? No, that breaks separation. 
    - Better: Add `Optional<ProfileEntity> findByEmail(String email)` to Repository. In Application Layer (`UpdateProfileService`), call this port/method to check uniqueness before updating domain object. This keeps the "Uniqueness Check" as part of the Use Case orchestration.

### Converter
- **ProfileConverter**: Static methods or Component to convert between `Domain.Profile`, `JPA.ProfileEntity`, and DTOs (though DTO conversion usually happens in Web Adapter). We'll put Domain <-> Entity conversion here.

## 5. Bootstrap

- **UseCaseConfiguration** (`@Configuration`):
    - Define beans for all Ports implementations (`SaveProfilePortImpl`, etc.).
    - Define beans for all Service classes (`CreateProfileService`, etc.), injecting their respective Ports.
    - Note: Do NOT define JPA/DB beans here; they are auto-configured by Boot. Just wire the application layer components.

## 6. Testing Strategy

- **Unit Tests (JUnit + Mockito)**:
    - `domain`: Test Value Objects constraints (e.g., invalid email throws exception).
    - `application`: Test each Service class using Mocked Ports. Verify interactions with ports and correct exceptions thrown.
        - e.g., `CreateProfileServiceTest.should_create_profile_success()`.
        - e.g., `CreateProfileServiceTest.should_throw_duplicate_email_when_exists()`.
- **Integration Tests (DataJpaTest + Testcontainers)**:
    - `adapter-persistence`: Test Repository queries and Port Implementations.
        - Ensure unique constraint works on save.
        - Ensure findById returns empty when missing.
- **BDD/Cucumber**:
    - Feature File: `bootstrap/src/test/resources/features/personal-profile-crud.feature`.
    - Step Definitions: In `bdd` package, use `@SpringBootTest` with `TestRestTemplate` to hit actual endpoints.
    - Scenarios map directly to Spec Gherkin scenarios.

## 7. Class List

| Category | Path (Relative) | Responsibility |
| :--- | :--- | :--- |
| Domain | `domain/.../domain/valueobject/Email.java` | Email value object with validation logic. |
| Domain | `domain/.../domain/valueobject/Age.java` | Age value object with range validation. |
| Domain | `domain/.../domain/valueobject/Name.java` | Name value object with length/format validation. |
| Domain | `domain/.../domain/entity/ProfileId.java` | Profile ID wrapper (UUID). |
| Domain | `domain/.../domain/entity/Profile.java` | Core domain entity holding state and business rules for update. |
| Domain | `domain/.../domain/exception/DuplicateEmailException.java` | Exception for duplicate email violations. |
| Domain | `domain/.../domain/exception/InvalidInputException.java` | Base or specific exception for invalid input data. |
| Application Port In | `application/.../port/in/CreateProfileUseCase.java` | Interface for creating a new profile. |
| Application Port In | `application/.../port/in/GetProfileUseCase.java` | Interface for retrieving a profile by ID. |
| Application Port In | `application/.../port/in/UpdateProfileUseCase.java` | Interface for updating an existing profile. |
| Application Port In | `application/.../port/in/DeleteProfileUseCase.java` | Interface for deleting a profile. |
| Application Port Out | `application/.../port/out/SaveProfilePort.java` | Interface to persist a profile. |
| Application Port Out | `application/.../port/out/LoadProfilePort.java` | Interface to load a profile from storage. |
| Application Service | `application/.../usecase/CreateProfileService.java` | Implements CreateProfileUseCase, orchestrates creation and validation. |
| Application Service | `application/.../usecase/GetProfileService.java` | Implements GetProfileUseCase, loads and converts profile. |
| Application Service | `application/.../usecase/UpdateProfileService.java` | Implements UpdateProfileUseCase, handles update logic and uniqueness check. |
| Application Service | `application/.../usecase/DeleteProfileService.java` | Implements DeleteProfileUseCase, checks existence then deletes. |
| Web Adapter | `adapter-web/.../web/controller/ProfileController.java` | REST Controller handling HTTP requests/responses. |
| Web Adapter DTO | `adapter-web/.../web/dto/CreateProfileRequestDTO.java` | Request body for creating profile. |
| Web Adapter DTO | `adapter-web/.../web/dto/UpdateProfileRequestDTO.java` | Request body for updating profile. |
| Web Adapter DTO | `adapter-web/.../web/dto/ProfileResponseDTO.java` | Response body containing profile data. |
| Web Adapter Advice | `adapter-web/.../web/advice/GlobalExceptionHandler.java` | Converts exceptions to appropriate HTTP responses. |
| Persistence Entity | `adapter-persistence/.../persistence/entity/ProfileEntity.java` | JPA Entity mapping Profile domain object. |
| Persistence Repo | `adapter-persistence/.../persistence/repository/ProfileRepository.java` | Spring Data Repository interface. |
| Persistence Port Impl | `adapter-persistence/.../persistence/port/out/SaveProfilePortImpl.java` | Saves profile via repository, handles DB constraints. |
| Persistence Port Impl | `adapter-persistence/.../persistence/port/out/LoadProfilePortImpl.java` | Loads profile from database by ID. |
| Persistence Converter | `adapter-persistence/.../persistence/converter/ProfileConverter.java` | Converts between Domain Profile and JPA Entity. |
| Bootstrap Config | `bootstrap/.../bootstrap/config/UseCaseConfiguration.java` | Wires up Application layer beans (Services & Ports). |
