# Tasks 001 — 個人資料管理 (CRUD)

## T1 領域模型與例外定義
- 說明：建立純 Java 領域物件，包含值物件、實體與領域例外。
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileId.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/Profile.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileNotFoundException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/DuplicateEmailException.java`
  - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/InvalidEmailException.java`
- 測試：
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileIdTest.java`：驗證 UUID 不可變性與相等性。
  - `domain/src/test/java/com/github/chunpingwang/ollamaagentoutcome/domain/ProfileTest.java`：驗證建構式驗證（Email 格式）、`update` 方法建立新實例、不變條件。

## T2 Application 層 Port 介面與 Command 物件
- 說明：定義 Application 層的 Inbound/Outbound Port 介面，以及 Use Case 所需的 Command 記錄類別。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/SaveProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/LoadProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/ExistsByEmailPort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/CreateProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/GetProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/UpdateProfileCommand.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/command/DeleteProfileCommand.java`
- 測試：
  - 此階段僅定義介面與記錄類別，無需額外測試類別，或可寫極簡單元測試驗證 Command 的建構邏輯（若 Command 有驗證邏輯）。

## T3 Application 層 Use Case 實作 (Service)
- 說明：實作 Use Case 邏輯，依賴 Port 介面進行業務規則驗證與流程控制。
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileService.java`
  - `application/src/main/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileService.java`
- 測試：
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、Email 重複、Email 格式錯誤。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證成功查詢、找不到資料。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證成功更新、找不到資料、Email 重複、Email 格式錯誤。
  - `application/src/test/java/com/github/chunpingwang/ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證成功刪除、找不到資料。

## T4 Persistence 層 Entity、Repository 與 Converter
- 說明：建立 JPA Entity、Spring Data Repository 與 Domain-Entity 轉換器。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntity.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileRepository.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverter.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileConverterTest.java`：驗證 Domain 與 Entity 雙向轉換。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/ProfileEntityTest.java`：驗證 JPA 映射、`Persistable` 邏輯、唯一性約束（使用 Testcontainers）。

## T5 Persistence 層 Port 實作
- 說明：實作 Application 層定義的 Outbound Ports，依賴 Repository 與 Converter。
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPort.java`
  - `adapter-persistence/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePort.java`
- 測試：
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaSaveProfilePortTest.java`：驗證儲存邏輯與轉換。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaLoadProfilePortTest.java`：驗證載入邏輯。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaExistsByEmailPortTest.java`：驗證 Email 存在性檢查。
  - `adapter-persistence/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/persistence/JpaDeleteProfilePortTest.java`：驗證刪除邏輯。

## T6 Web Adapter Controller、DTO 與 Error Handling
- 說明：建立 REST Controller、Request/Response DTO 與全域異常處理器。
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileController.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/CreateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/UpdateProfileRequest.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileResponse.java`
  - `adapter-web/src/main/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerAdvice.java`
- 測試：
  - `adapter-web/src/test/java/com/github/chunpingwang/ollamaagentoutcome/adapter/web/ProfileControllerTest.java`：使用 `@WebMvcTest` 與 Mock Use Case Ports，驗證 HTTP 狀態碼、請求/回應格式、驗證錯誤處理。

## T7 Bootstrap 組態與 Cucumber 驗收測試
- 說明：組裝 Spring Beans，建立 Cucumber Feature 檔與步驟定義，進行端到端測試。
  - `bootstrap/src/main/java/com/github/chunpingwang/ollamaagentoutcome/bootstrap/ApplicationConfig.java`
  - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
  - `bootstrap/src/test/java/com/github/chunpingwang/ollamaagentoutcome/bdd/ProfileSteps.java`
- 測試：
  - 執行 Cucumber 測試，驗證完整的 CRUD 流程（建立、查詢、更新、刪除）與異常情境（重複 Email、找不到資料）。
