# Tasks 001 — Personal Profile CRUD (個人資料管理)

## T1 Domain Model & Value Objects
- 說明：建立領域層的核心模型與值對象。包含 `ProfileId`, `Email`, `DisplayName`, `Bio` 四個 Value Object，以及 `Profile` Entity。定義業務規則驗證邏輯（如 Email 格式、Bio 長度限制）。同時建立相關的 Domain Exceptions (`InvalidEmailException`, `DuplicateEmailException`, `ProfileNotFoundException`, `BioTooLongException`)。
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/ProfileId.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Email.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/DisplayName.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/Bio.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/Profile.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/InvalidEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/DuplicateEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/ProfileNotFoundException.java`
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/exception/BioTooLongException.java`
- 測試：
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/EmailTest.java`：驗證 Email 格式合法性與非法輸入拋出例外。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/valueobject/BioTest.java`：驗證 Bio 長度限制（如超過 500 字元拋出例外）。
    - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/entity/ProfileTest.java`：驗證 Profile 建立時必填欄位檢查、更新 displayName/bio 的行為，以及狀態一致性。

## T2 Application Ports & Use Cases (Create, Get)
- 說明：定義應用層的 Inbound Ports (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Ports (`SaveProfilePort`, `FindProfileByIdPort`, `ValidateEmailUniquePort`)。實作 `CreateProfileService` 與 `GetProfileService`。建立 Command DTO (`CreateProfileCommand`, `UpdateProfileCommand`) 與 Response DTO (`ProfileResponseDTO`)。此階段僅處理 Create 和 Get 的邏輯流程。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/FindProfileByIdPort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ValidateEmailUniquePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/CreateProfileCommand.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/dto/ProfileResponseDTO.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、重複 Email (Mock Port 拋出例外)、無效輸入。使用 Mockito Mock Outbound Ports。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證找到資料回傳 DTO、找不到資料拋出 `ProfileNotFoundException`。

## T3 Application Use Cases (Update, Delete) & Commands
- 說明：完成剩餘的 Inbound Ports (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Ports (`RemoveProfilePort`)。實作 `UpdateProfileService`（包含檢查舊資料、Email 唯一性校驗邏輯）與 `DeleteProfileService`。補充 Command DTO (`UpdateProfileCommand`)。
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/RemoveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/UpdateProfileCommand.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證成功更新、找不到資料 (404)、修改 Email 且重複 (409)、修改 Email 但不重複。
    - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證成功刪除、找不到資料拋出例外。

## T4 Persistence Adapter & Configuration
- 說明：建立 JPA Entity (`ProfileEntity`)，實作 Spring Data Repository (`ProfileJpaRepository`)，並實作所有 Outbound Ports (`JpaSaveProfilePort`, `JpaFindProfileByIdPort`, `JpaValidateEmailUniquePort`, `JpaRemoveProfilePort`)。處理 Domain 與 JPA Entity 之間的轉換邏輯。在 Bootstrap 模組的 `UseCaseConfiguration` 中宣告所有 Application Service 與 Port Implementation 為 Bean，確保 Spring Context 可啟動。
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/repository/ProfileJpaRepository.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaFindProfileByIdPort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaValidateEmailUniquePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaRemoveProfilePort.java`
    - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java`
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePortTest.java`：使用 Testcontainers PostgreSQL，驗證儲存成功、重複 Email 拋出 `DuplicateEmailException`。
    - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/port/JpaFindProfileByIdPortTest.java`：驗證查詢存在與不存在的資料。
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java`：簡單的 `@SpringBootTest`，驗證所有 Bean (Services, Ports) 可正常注入且 Context 啟動無誤。

## T5 Web Adapter & Exception Handling
- 說明：建立 REST Controller (`ProfileController`)，定義 Request DTOs (`CreateProfileRequestDTO`, `UpdateProfileRequestDTO`)，並實作全域異常處理器 (`GlobalExceptionHandler`)。將 HTTP 請求映射到 Application Inbound Ports，並將 Domain Exceptions 轉換為對應的 HTTP Status Code。
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileController.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDTO.java`
    - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/handler/GlobalExceptionHandler.java`
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`：使用 `@WebMvcTest` + Mockito Mock Inbound Ports。驗證 POST (201, Location Header), GET (200, 404), PUT (200, 404, 409), DELETE (204, 404) 的狀態碼與回應內容。驗證 Jakarta Validation 錯誤回傳 400。

## T6 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`profile-crud.feature`)，涵蓋 US-1 到 US-4 的所有驗收場景（建立、查詢、更新、刪除、異常情況）。實作 Step Definitions (`ProfileSteps`)，透過 HTTP Client 呼叫實際 API 並斷言結果。確保端到端流程正確運作。
    - `bootstrap/src/test/resources/features/profile-crud.feature`
    - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/stepdefs/ProfileSteps.java`
- 測試：
    - 執行 Cucumber Runner，確認所有 Scenario Passed。包含正常流與邊界條件（如重複 Email、找不到 ID）的整合測試。
