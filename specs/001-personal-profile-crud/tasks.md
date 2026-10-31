# Tasks 001 — Personal Profile CRUD

## T1 Domain Model & Value Objects
- 說明：建立領域層的核心模型與值物件。包含識別碼、值物件的不變條件驗證，以及實體的基本結構與例外定義。
    - `domain/src/main/java/com/github/chunpingwang/ollamaagentoutcome/domain/model/ProfileId.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/Email.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/DisplayName.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/Bio.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/model/Profile.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/exception/DuplicateEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/exception/InvalidEmailException.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/exception/InvalidDisplayNameException.java`
    - `domain/src/main/java/com/github/chunpingwang.ollamaagentoutcome/domain/exception/ProfileNotFoundException.java`
- 測試：
    - `domain/src/test/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/EmailTest.java`：驗證格式、長度與 null 檢查。
    - `domain/src/test/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/DisplayNameTest.java`：驗證空白處理與長度限制。
    - `domain/src/test/java/com/github/chunpingwang.ollamaagentoutcome/domain/valueobject/BioTest.java`：驗證長度限制。
    - `domain/src/test/java/com/github/chunpingwang.ollamaagentoutcome/domain/model/ProfileIdTest.java`：驗證 UUID 有效性。
    - `domain/src/test/java/com/github/chunpingwang.ollamaagentoutcome/domain/model/ProfileTest.java`：驗證建構邏輯與 `update()` 方法的不變條件觸發。

## T2 Application Ports & Use Cases (Create, Get)
- 說明：定義應用層的 Inbound/Outbound Port 介面，並實作建立與查詢的業務邏輯。此階段不依賴資料庫，僅使用 Mockito 模擬 Port。
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/in/CreateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/in/GetProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/out/SaveProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/out/LoadProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/CreateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/GetProfileService.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/CreateProfileServiceTest.java`：驗證成功建立、重複 Email 異常傳遞、輸入無效異常拋出。
    - `application/src/test/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/GetProfileServiceTest.java`：驗證找到資料回傳 Result、找不到資料拋出例外。

## T3 Application Use Cases (Update, Delete) & Converters
- 說明：實作更新與刪除的業務邏輯，並定義 Persistence Adapter 所需的 Converter（Domain <-> Entity）。此階段仍為純 Java 測試，不啟動 Spring。
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/in/UpdateProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/in/DeleteProfileUseCase.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/port/out/DeleteProfilePort.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/UpdateProfileService.java`
    - `application/src/main/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/DeleteProfileService.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/converter/DomainToEntityConverter.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/converter/EntityToDomainConverter.java`
- 測試：
    - `application/src/test/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/UpdateProfileServiceTest.java`：驗證更新成功、找不到資料異常、重複 Email 異常傳遞。
    - `application/src/test/java/com/github/chunpingwang.ollamaagentoutcome/application/usecase/DeleteProfileServiceTest.java`：驗證刪除存在資料、找不到資料拋出例外。
    - `adapter-persistence/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/converter/DomainToEntityConverterTest.java`：驗證欄位映射正確性（含 null 值處理）。
    - `adapter-persistence/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/converter/EntityToDomainConverterTest.java`：驗證反向映射正確性。

## T4 Persistence Adapter (JPA)
- 說明：實作 JPA Entity、Repository 與 Outbound Port 的資料庫操作層。使用 Testcontainers PostgreSQL 進行整合測試，確保唯一性約束與 Persistable 邏輯正常運作。
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/entity/ProfileEntity.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/repository/ProfileRepository.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePort.java`
    - `adapter-persistence/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfilePort.java`
- 測試：
    - `adapter-persistence/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaSaveProfilePortTest.java`：驗證儲存成功、重複 Email 觸發 DataIntegrityViolationException 並轉換為 Domain Exception。
    - `adapter-persistence/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaLoadProfilePortTest.java`：驗證查詢正確性與空值處理。
    - `adapter-persistence/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/persistence/port/JpaDeleteProfilePortTest.java`：驗證刪除操作正常執行。

## T5 Bootstrap Configuration (Bean Assembly)
- 說明：在 Spring Boot 組態中註冊所有 Use Case Service 與 Persistence Port Bean，確保依賴注入鏈完整。**僅修改**現有的 `UseCaseConfiguration`，不新增新的 Context 測試（骨架已提供 `ApplicationContextTest`）。
    - `bootstrap/src/main/java/com/github/chunpingwang.ollamaagentoutcome/bootstrap/config/UseCaseConfiguration.java`
- 測試：無新檔案產生。依賴骨架提供的 `bootstrap/src/test/java/com/github/chunpingwang.ollamaagentoutcome/bootstrap/ApplicationContextTest.java` 來驗證整個應用程式上下文能否順利啟動且 Bean 可被解析。若該測試通過，即代表組裝正確。

## T6 Web Adapter & Error Handling
- 說明：實作 REST Controller、DTOs 與全域異常處理器。使用 MockMvc 進行單元測試，模擬 HTTP 請求並驗證狀態碼與回應內容。此階段不連接真實資料庫，Use Case 均為 Mockito 模擬。
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/dto/CreateProfileRequestDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/dto/UpdateProfileRequestDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/dto/ProfileResponseDto.java`
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/controller/ProfileController.java`
    - `adapter-web/src/main/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/error/GlobalExceptionHandler.java`
- 測試：
    - `adapter-web/src/test/java/com/github/chunpingwang.ollamaagentoutcome/adapter/web/controller/ProfileControllerTest.java`：驗證 POST (201, Location Header), GET (200, 404), PUT (200, 404, 409), DELETE (204, 404) 以及 Jakarta Validation 錯誤 (400)。

## T7 BDD Acceptance Tests (Cucumber)
- 說明：撰寫 Cucumber Feature 檔與步驟定義，透過真實的 HTTP 呼叫（使用 TestRestTemplate）與嵌入式資料庫容器進行端到端驗收。確保從 Web Layer 到 Persistence Layer 的全流程正確性。
    - `bootstrap/src/test/resources/features/personal-profile-crud.feature`
    - `bootstrap/src/test/java/com/github/chunpingwang.ollamaagentoutcome/bdd/PersonalProfileStepDefinitions.java`
- 測試：執行 Cucumber Runner，驗證所有 Gherkin 場景通過。包含正常 CRUD、重複 Email 衝突、找不到資料等情境。
