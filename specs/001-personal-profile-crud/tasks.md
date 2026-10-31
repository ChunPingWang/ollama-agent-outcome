# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Value Objects
- 說明：建立領域層的核心模型與值物件，包含 ID、姓名、電子郵件、年齡的不可變實作及驗證邏輯，以及相關的領域例外類別。
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/ProfileId.java` (record, UUID validation)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Name.java` (record, length/format check)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java` (record, email format regex)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Age.java` (record, range 0-150)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/Profile.java` (class with factory methods for create/update, immutable state via builder or new instance return)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidInputException.java` (Base class or specific ones like InvalidAgeException, InvalidEmailException extending it)
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java`
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/*Test.java`
    - 驗證各 Value Object 的建構子與靜態工廠在無效輸入時拋出對應例外。
    - 驗證 Profile 的建立與更新邏輯，確保狀態一致性。

## T2 Application Ports & Create/Get Use Cases
- 說明：定義應用層的 Inbound Port 介面（Create、Get），並實作對應的 Service。此階段僅處理建立與查詢的基本流程，不包含複雜的唯一性檢查業務規則（留給 Update 或後續整合）。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java` (Depends on SaveProfilePort)
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java` (Depends on LoadProfilePort)
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/*Test.java`
    - `CreateProfileServiceTest`: Mock `SaveProfilePort`, verify it is called with correct Profile object. Verify exception propagation if port throws.
    - `GetProfileServiceTest`: Mock `LoadProfilePort`. Verify return of Profile when found, and throw `ProfileNotFoundException` when not found.

## T3 Application Update & Delete Use Cases
- 說明：定義應用層的 Inbound Port 介面（Update、Delete），並實作對應的 Service。包含業務規則驗證，如更新時檢查電子郵件是否與其他記錄重複。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java` (Depends on LoadProfilePort, SaveProfilePort)
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java` (Depends on LoadProfilePort, SaveProfilePort for delete or dedicated DeletePort if needed, but reusing Save/Load pattern or adding simple Delete logic in adapter) *Note: Plan suggests checking existence then deleting. We can add a `delete` method to SaveProfilePort or create a specific port. Let's stick to minimal ports: Add `void delete(String id)` to `SaveProfilePort`? Or just use existing patterns. To keep it clean, let's assume `SaveProfilePort` handles save, and we might need a `DeleteProfilePort`. However, standard JPA repo has delete. Let's define `DeleteProfilePort` interface in application layer.* -> Correction: The plan lists `SaveProfilePort` and `LoadProfilePort`. For deletion, typically a `deleteById` is enough. But to enforce "check exists first" in App Layer, the Service calls `Load`, checks not empty, then calls a delete mechanism. Let's add `DeleteProfilePort` to Application Port Out to be explicit about the intent of removing data, or reuse `SaveProfilePort` with a delete signature? No, separate concern. I will add `DeleteProfilePort` interface here.
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java` (New for this task)
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/*Test.java`
    - `UpdateProfileServiceTest`: Mock `LoadProfilePort` and `SaveProfilePort`. Verify logic: load profile, validate new email uniqueness (mock port check if needed, or just verify domain validation), call update on domain, call save. Test duplicate email scenario throwing `DuplicateEmailException`.
    - `DeleteProfileServiceTest`: Mock `LoadProfilePort` and `DeleteProfilePort`. Verify it throws `ProfileNotFoundException` if ID doesn't exist. Verify it calls delete port when found.

## T4 Persistence Adapter & Repository
- 說明：建立 JPA Entity、Repository 介面，以及實作 Application Layer 的 Outbound Ports。處理 Domain 與 JPA Entity 之間的轉換，並確保唯一性約束觸發時拋出正確的例外。
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java` (Implements `Persistable<String>`)
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileRepository.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/converter/ProfileConverter.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/out/JpaSaveProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/out/JpaLoadProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/out/JpaDeleteProfilePort.java`
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/*Test.java`
    - `JpaSaveProfilePortTest`: Use Testcontainers. Verify save works, verify `DuplicateEmailException` is thrown on duplicate email via unique constraint.
    - `JpaLoadProfilePortTest`: Verify findById returns correct entity or empty.
    - `JpaDeleteProfilePortTest`: Verify delete removes record and handles non-existent ID appropriately (or relies on App layer check).

## T5 Bootstrap Configuration & Context Assembly
- 說明：在 Bootstrap 模組中組裝 Bean。將 Application Layer 的 Ports 實作與 Services 連接起來，並確保 Spring Boot 自動配置能掃描到 Persistence Adapter 的 Component（Repository, Entity）。**僅修改** `UseCaseConfiguration`，不新增其他 Configuration 類別或 Context 測試。
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java`
        - Define `@Bean` for all Port implementations (`JpaSaveProfilePort`, etc.) if they are not auto-detected as components? The plan says "Define beans...". Usually, we can make them `@Component` in their package or define them here. Given the strict separation, defining them in `UseCaseConfiguration` ensures explicit wiring without relying on component scanning order across modules too much, though standard Spring Boot scans main class path. Let's follow Plan: Use `@Bean` methods in `UseCaseConfiguration`.
        - Note: Need to ensure `adapter-persistence` classes are visible. Since it's a dependency, they should be scanned if configured correctly, but explicit `@Bean` is safer per instructions.
- 測試：無新檔案。依賴骨架提供的 `ApplicationContextTest` 驗證上下文啟動成功。

## T6 Web Adapter & Error Handling
- 說明：建立 REST Controller、DTOs 以及全域異常處理器。將 HTTP 請求映射到 Application Layer 的 Inbound Ports。
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/ProfileResponseDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/advice/GlobalExceptionHandler.java`
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/*Test.java`
    - `ProfileControllerTest`: Use `@WebMvcTest`. Mock the UseCase ports. Verify:
        - POST returns 201 with Location header on success, 400 on validation error, 409 on duplicate email (if domain exception is thrown).
        - GET returns 200 with body, 404 if not found.
        - PUT returns 200, 404, 409.
        - DELETE returns 204, 404.

## T7 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔與步驟定義，透過 HTTP 呼叫實際 API 進行驗收。確保所有 Spec 中的情境都能通過。
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/PersonalProfileSteps.java`
- 測試：執行 Cucumber Runner。驗證 CRUD 操作符合預期狀態碼、回應內容及資料庫最終狀態（可選，但建議在 Steps 中簡單檢查）。
