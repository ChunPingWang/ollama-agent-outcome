# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供個人基本資訊的管理能力，包括建立新的個人資料、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳戶資料。這確保了數據的完整性與用戶對自身數據的控制權。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本資訊集合，包含唯一的識別碼、電子郵件地址和顯示名稱。
- **電子郵件 (Email)**：用於唯一標識用戶的身份驗證憑證，必須符合標準 Email 格式且在全域中唯一。
- **顯示名稱 (Display Name)**：用戶希望公開顯示的名稱，允許為空或僅含空白字符（視為空值處理），長度有限制。

## 使用者情境

### US-1 新增個人資料
身為一名新用戶，我想要建立我的個人資料以便開始使用系統功能。

### US-2 查詢個人資料
身為一名已登入用戶，我想要根據 ID 查詢我的個人資料以便確認當前狀態。

### US-3 更新個人資料
身為一名已登入用戶，我想要修改我的顯示名稱以便保持資訊最新。

### US-4 刪除個人資料
身為一名用戶，我想要刪除我的帳號及相關資料以便徹底離開系統。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為系統用戶
    我希望能夠管理我的個人資料
    以便維護個人信息的準確性與隱私

場景: 成功建立新的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "user@example.com",
          "displayName": "Test User"
        }
        """
    那麼回應的狀態碼應該是 201 Created
    並且回應標頭 "Location" 應該存在且指向該資源路徑
    並且我可以透過 GET 請求獲取到剛建立的資料，其 email 為 "user@example.com"

場景: 建立重複電子郵件的個人資料應失敗
    假設已經存在一個 email 為 "duplicate@test.com" 的個人資料
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "duplicate@test.com",
          "displayName": "Another User"
        }
        """
    那麼回應的狀態碼應該是 409 Conflict
    並且錯誤訊息指出電子郵件已存在

場景: 建立無效格式的電子郵件應失敗
    當我發送 POST 請求至 "/api/profiles"，內容包含:
        """json
        {
          "email": "invalid-email",
          "displayName": "Bad Email"
        }
        """
    那麼回應的狀態碼應該是 400 Bad Request
    並且錯誤訊息指出電子郵件格式不正確

場景: 查詢存在的個人資料成功
    假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
    且其 email 為 "find@example.com"
    當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
    那麼回應的狀態碼應該是 200 OK
    並且回應主體中的 email 欄位值為 "find@example.com"

場景: 查無資料時返回 404
    當我發送 GET 請求至 "/api/profiles/non-existent-id"
    那麼回應的狀態碼應該是 404 Not Found

場景: 更新存在的個人資料成功
    假設系統中存在 ID 為 "update-id" 的個人資料，當前顯示名稱為 "Old Name"
    當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
        """json
        {
          "email": "update@test.com",
          "displayName": "New Name"
        }
        """
    那麼回應的狀態碼應該是 200 OK
    並且再次查詢該資源時，display_name 應變更為 "New Name"
    且 email 保持不變（若未提供則不修改，此處假設全量更新需傳入所有必填字段）

場景: 刪除存在的個人資料成功
    假設系統中存在 ID 為 "delete-id" 的個人資料
    當我發送 DELETE 請求至 "/api/profiles/delete-id"
    那麼回應的狀態碼應該是 204 No Content
    並且隨後嘗試 GET 查詢該 ID 將返回 404 Not Found

場景: 刪除不存在的資料返回 404
    當我發送 DELETE 請求至 "/api/profiles/non-existent-delete-id"
    那麼回應的狀態碼應該是 404 Not Found
```

## API 概要

| HTTP Method | Path                  | Description              | Success Status | Error Status       |
|-------------|-----------------------|--------------------------|----------------|--------------------|
| POST        | /api/profiles         | Create new profile       | 201 Created    | 400 Bad Request, 409 Conflict |
| GET         | /api/profiles/{id}    | Get profile by ID        | 200 OK         | 404 Not Found      |
| PUT         | /api/profiles/{id}    | Update full profile info | 200 OK         | 400 Bad Request, 404 Not Found |
| DELETE      | /api/profiles/{id}    | Delete profile           | 204 No Content | 404 Not Found      |

## 範圍外
- 用戶認證與授權（假設所有操作均來自已驗證用戶，或本 Spec 僅關注數據層邏輯，權限控制由後續 Auth Spec 處理）。
- 個人資料的複雜嵌套結構（如地址、電話等多對多關係），目前僅包含 Email 和 Display Name。
- 分頁與列表查詢功能（僅提供單筆精確查詢）。
- 軟刪除機制（採用硬刪除）。

FEATURE_NAME: personal-profile-crud
