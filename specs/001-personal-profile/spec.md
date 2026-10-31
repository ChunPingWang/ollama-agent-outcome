# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳號資料。此功能是使用者帳戶管理的核心基礎。

## 名詞
- **個人資料 (Profile)**：代表單一使用者的基本資訊集合，包含唯一識別碼、電子郵件地址、顯示名稱與簡介。
- **電子郵件 (Email)**：用於識別使用者的唯一憑證，格式需符合標準 Email 規範且在全域中必須唯一。
- **顯示名稱 (Display Name)**：使用者希望對外展示的名稱，允許為空或僅含空白字符（視為空值處理），長度有限制。
- **簡介 (Bio)**：關於使用者的簡短介紹文字，可選填欄位。

## 使用者情境

### US-1 新增個人資料
身為**新使用者**，我想要**建立我的個人資料**，以便**在系統中擁有唯一的身份記錄**。

### US-2 查詢個人資料
身為**已登入的使用者**，我想要**根據 ID 查詢我的個人資料**，以便**確認我的當前資訊狀態**。

### US-3 更新個人資料
身為**已登入的使用者**，我想要**修改我的個人資料**，以便**保持資訊的準確性與時效性**。

### US-4 刪除個人資料
身為**使用者**，我想要**永久刪除我的個人資料**，以便**行使被遺忘權或停止使用服務**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為一名使用者
    我希望能夠管理我的個人資料
    以便維護我的個人資訊

    場景: 成功建立新的個人資料
        假設 沒有其他資料夾存在相同電子郵件 "user@example.com"
        當 我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "user@example.com",
              "displayName": "Test User",
              "bio": "Hello World"
            }
            """
        那麼 HTTP 回應狀態碼應為 201 Created
        並且 回應標頭 "Location" 應指向新建立的資源 URL
        並且 我可以透過該 Location URL 查詢到相同的資料

    場景: 建立失敗 - 重複的電子郵件地址
        假設 已存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當 我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "duplicate@test.com",
              "displayName": "Another User",
              "bio": "Duplicate Test"
            }
            """
        那麼 HTTP 回應狀態碼應為 409 Conflict
        並且 錯誤訊息指出電子郵件已被使用

    場景: 建立失敗 - 無效的電子郵件格式
        當 我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "invalid-email",
              "displayName": "Bad Email",
              "bio": ""
            }
            """
        那麼 HTTP 回應狀態碼應為 400 Bad Request
        並且 錯誤訊息指出電子郵件格式不正確

    場景: 成功查詢存在的個人資料
        假設 系統中存在 ID 為 "123e4567-e89b-12d3-a456-426614174000" 的個人資料
        且 該資料的電子郵件為 "query@example.com"
        當 我發送 GET 請求至 "/api/profiles/123e4567-e89b-12d3-a456-426614174000"
        那麼 HTTP 回應狀態碼應為 200 OK
        並且 回應主體中的 email 欄位值為 "query@example.com"

    場景: 查詢失敗 - 查無此筆資料
        當 我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼 HTTP 回應狀態碼應為 404 Not Found
        並且 錯誤訊息指出找不到指定的個人資料

    場景: 成功更新個人資料
        假設 系統中存在 ID 為 "update-id" 的個人資料，其顯示名稱為 "Old Name"
        當 我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
            """
            {
              "email": "updated@test.com",
              "displayName": "New Name",
              "bio": "Updated Bio"
            }
            """
        那麼 HTTP 回應狀態碼應為 200 OK
        並且 再次查詢該 ID 時，顯示名稱變更為 "New Name"
        並且 電子郵件已更新為 "updated@test.com"

    場景: 更新失敗 - 目標不存在
        當 我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
            """
            {
              "email": "test@example.com",
              "displayName": "Name",
              "bio": ""
            }
            """
        那麼 HTTP 回應狀態碼應為 404 Not Found

    場景: 刪除存在的個人資料
        假設 系統中存在 ID 為 "delete-id" 的個人資料
        當 我發送 DELETE 請求至 "/api/profiles/delete-id"
        那麼 HTTP 回應狀態碼應為 204 No Content
        並且 隨後嘗試 GET 查詢該 ID 會返回 404 Not Found

    場景: 刪除不存在的資料
        當 我發送 DELETE 請求至 "/api/profiles/non-existent-id"
        那麼 HTTP 回應狀態碼應為 404 Not Found
```

## API 概要

| HTTP Method | Path                  | Description          | Success Status | Error Status       |
|-------------|-----------------------|----------------------|----------------|--------------------|
| POST        | /api/profiles         | 建立新個人資料       | 201 Created    | 400, 409           |
| GET         | /api/profiles/{id}    | 根據 ID 查詢資料     | 200 OK         | 404                |
| PUT         | /api/profiles/{id}    | 全量更新指定資料     | 200 OK         | 400, 404, 409      |
| DELETE      | /api/profiles/{id}    | 永久刪除指定資料     | 204 No Content | 404                |

## 範圍外
- 使用者認證與授權（假設所有呼叫皆來自已驗證用戶，或本 Spec 僅關注資料結構本身，後續 Spec 處理權限）。
- 檔案上傳功能。
- 複雜的搜尋與過濾條件（如依姓名模糊搜尋），僅支援單一 ID 精確查詢。
- 軟刪除機制（目前採用硬刪除）。

FEATURE_NAME: personal-profile-crud
