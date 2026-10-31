# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Exceptions
- 說明：建立領域層核心模型與自訂例外。包含 Value Objects (`ProfileId`, `Email`, `Age`)、Domain Entity (`Profile`) 以及四個 Domain Exception (`ProfileNotFoundException`, `DuplicateEmailException`, `InvalidAgeException`, `InvalidEmailFormatException`)。所有類別直接位於 `domain` 套件下。
- 測試：`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ValueObjectTests.java` (驗證 VO 不變條件)、`domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileEntityTests.java` (驗證 Profile 狀態變更邏輯)。

## T2 Application Ports & Use Cases (Create, Get)
- 說明：定義 Inbound Port (`CreateProfileUseCase`, `GetProfileUseCase`) 與 Outbound Port (`SaveProfilePort`, `LoadProfilePort`)，並實作對應的 Service (`CreateProfileService`, `GetProfileService`)。建立輸入 DTO (`CreateProfileRequest`, `UpdateProfileCommand`)。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`。

## T3 Application Use Cases (Update, Delete)
- 說明：定義剩餘 Inbound Port (`UpdateProfileUseCase`, `DeleteProfileUseCase`) 與 Outbound Port (`RemoveProfilePort`)，並實作對應的 Service (`UpdateProfileService`, `DeleteProfileService`)。
- 測試：`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`、`application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`。

## T4 Persistence Adapter & Configuration
- 說明：建立 JPA Entity (`ProfileEntity`)、Repository (`ProfileRepository`)、Port 實作類別 (`SaveProfilePortImpl`, `LoadProfilePortImpl`, `RemoveProfilePortImpl`) 及轉換邏輯。在 Bootstrap 模組的 `UseCaseConfiguration` 中註冊所有 Application Layer Bean。
- 測試：`adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/PortImplementationTests.java` (驗證 Port 實作與 DB 互動)、`bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationContextTest.java` (骨架已提供，確保 Context 啟動無誤)。

## T5 Web Adapter & Error Handling
- 說明：建立 REST Controller (`ProfileController`)、Web DTOs (`CreateProfileRequestDto`, `UpdateProfileRequestDto`, `ProfileResponseDto`) 以及全域異常處理器 (`GlobalExceptionHandler`)。
- 測試：`adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java` (使用 MockMvc 模擬 Use Case 行為並驗證 HTTP 狀態碼與回應格式)。

## T6 BDD Acceptance Tests
- 說明：撰寫 Cucumber Feature 檔 (`personal-profile-crud.feature`) 與 Step Definitions (`PersonalProfileSteps`)，透過 TestRestTemplate 進行端到端整合測試，涵蓋 CRUD 成功路徑與各種錯誤情境。
- 測試：執行 Cucumber Runner，驗證所有 Scenario 通過。
