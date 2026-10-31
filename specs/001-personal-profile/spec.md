# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供個人基本資訊的管理能力，包括建立新的個人資料、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳號資料。這確保了數據的完整性與用戶對自身隱私的控制權。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本資訊集合，包含唯一的識別碼、電子郵件地址和顯示名稱。
- **電子郵件 (Email)**：用於唯一標識用戶的身份憑證，必須符合標準 Email 格式且在全域中不可重複。
- **顯示名稱 (Display Name)**：用戶希望對外展示的暱稱或真實姓名，允許為空字符串以外的任意有效字符。

## 使用者情境

### US-1 新增個人資料
身為一名新用戶，我想要建立我的個人資料以便開始使用系統功能。

### US-2 查詢個人資料
身為一名用戶，我想要透過 ID 查詢我的個人資料以便確認當前狀態。

### US-3 更新個人資料
身為一名用戶，我想要修改我的顯示名稱以便保持資訊最新。

### US-4 刪除個人資料
身為一名用戶，我想要刪除我的帳戶以保護隱私並停止服務。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為系統用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "user@example.com",
              "displayName": "測試用戶"
            }
            """
        那麼回應的狀態碼應該是 201 Created
        並且回應標頭 "Location" 應該存在且指向該資源
        
    場景: 嘗試建立重複電子郵件的資料失敗
        假設已存在電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "duplicate@test.com",
              "displayName": "另一個用戶"
            }
            """
        那麼回應的狀態碼應該是 409 Conflict
        並且錯誤訊息應指出電子郵件已存在
        
    場景: 使用無效格式電子郵件建立資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "invalid-email",
              "displayName": "壞數據"
            }
            """
        那麼回應的狀態碼應該是 400 Bad Request
        並且錯誤訊息應指出輸入格式不合法

    場景: 成功查詢存在的個人資料
        假設系統中存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        且該資料的 email 為 "query@example.com"，display_name 為 "查詢者"
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 200 OK
        並且回應主體中的 id 應該匹配所查詢的 ID
        並且回應主體中的 email 應該為 "query@example.com"
        
    場景: 查詢不存在的個人資料失敗
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404 Not Found
        
    場景: 成功更新顯示名稱
        假設系統中存在 ID 為 "update-id" 的個人資料
        且當前 display_name 為 "舊名字"
        當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
            """
            {
              "email": "update@test.com",
              "displayName": "新名字"
            }
            """
        那麼回應的狀態碼應該是 200 OK
        並且再次查詢該資源時，display_name 應變更為 "新名字"
        並且 email 保持不變
        
    場景: 嘗試更新不存在的資料失敗
        當我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
            """
            {
              "email": "test@test.com",
              "displayName": "無效更新"
            }
            """
        那麼回應的狀態碼應該是 404 Not Found

    場景: 成功刪除個人資料
        假設系統中存在 ID 為 "delete-id" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/delete-id"
        那麼回應的狀態碼應該是 204 No Content
        並且隨後查詢該 ID 應該返回 404 Not Found
        
    場景: 刪除不存在的資料失敗
        當我發送 DELETE 請求至 "/api/profiles/non-existent-delete-id"
        那麼回應的狀態碼應該是 404 Not Found
```

## API 概要

| HTTP 方法 | 路徑 | 描述 | 成功狀態碼 | 錯誤狀態碼 |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/api/profiles` | 建立新個人資料 | 201 Created | 400 Bad Request, 409 Conflict |
| GET | `/api/profiles/{id}` | 根據 ID 查詢資料 | 200 OK | 404 Not Found |
| PUT | `/api/profiles/{id}` | 全量更新資料 | 200 OK | 400 Bad Request, 404 Not Found |
| DELETE | `/api/profiles/{id}` | 刪除指定資料 | 204 No Content | 404 Not Found |

## 範圍外
- 密碼管理與認證機制（假設由外部 Auth Service 處理）。
- 檔案上傳或頭像功能。
- 批量操作（Bulk Create/Update/Delete）。
- 軟刪除（Soft Delete）邏輯，目前僅支援物理刪除。

FEATURE_NAME: personal-profile-crud
