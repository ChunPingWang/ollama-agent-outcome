# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新已存在的資訊以及刪除不再需要的帳號或資料。這確保了數據的準確性與用戶對自身資料的控制權。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本資訊集合，包含唯一的識別碼、電子郵件地址、顯示名稱等核心屬性。
- **電子郵件 (Email)**：用於唯一標識用戶的身份憑證，格式需符合標準 Email 規範且在全域中必須唯一。
- **顯示名稱 (Display Name)**：用戶希望公開顯示的名稱，允許為空或僅含空白字符時視為未設定。

## 使用者情境

### US-1 新增個人資料
身為**新用戶**，我想要**建立我的個人資料**，以便**開始使用系統服務**。

### US-2 查詢個人資料
身為**已登入用戶**，我想要**根據 ID 查詢我的個人資料**，以便**確認當前儲存的資訊是否正確**。

### US-3 修改個人資料
身為**已登入用戶**，我想要**更新我的個人資料（如顯示名稱）**，以便**保持資訊的最新狀態**。

### US-4 刪除個人資料
身為**已登入用戶**，我想要**永久刪除我的個人資料**，以便**行使被遺忘權或停止使用服務**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為一名用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "user@example.com",
              "displayName": "測試用戶"
            }
            """
        那麼回應的狀態碼應該是 201 (Created)
        並且回應標頭 "Location" 應指向新建立的資源路徑
        
    場景: 嘗試建立重複電子郵件的個人資料失敗
        假設已存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "duplicate@test.com",
              "displayName": "另一個用戶"
            }
            """
        那麼回應的狀態碼應該是 409 (Conflict)
        
    場景: 嘗試建立無效格式電子郵件的個人資料失敗
        當我發送 POST 請求至 "/api/profiles"，內容包含:
            """
            {
              "email": "invalid-email",
              "displayName": "壞資料"
            }
            """
        那麼回應的狀態碼應該是 400 (Bad Request)

    場景: 成功查詢存在的個人資料
        假設系統中已存在 ID 為 "550e8400-e29b-41d4-a716-446655440000" 的個人資料
        且其電子郵件為 "query@example.com"
        當我發送 GET 請求至 "/api/profiles/550e8400-e29b-41d4-a716-446655440000"
        那麼回應的狀態碼應該是 200 (OK)
        並且回應主體應包含正確的 email 與 displayName
        
    場景: 查詢不存在的個人資料
        當我發送 GET 請求至 "/api/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功更新個人資料
        假設系統中已存在 ID 為 "update-id" 的個人資料
        且目前顯示名稱為 "舊名稱"
        當我發送 PUT 請求至 "/api/profiles/update-id"，內容包含:
            """
            {
              "email": "updated@test.com",
              "displayName": "新名稱"
            }
            """
        那麼回應的狀態碼應該是 200 (OK)
        並且再次查詢該資源時，顯示名稱應變更為 "新名稱"
        
    場景: 嘗試更新不存在的個人資料
        當我發送 PUT 請求至 "/api/profiles/missing-id"，內容包含:
            """
            {
              "email": "test@test.com",
              "displayName": "無效更新"
            }
            """
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功刪除個人資料
        假設系統中已存在 ID 為 "delete-id" 的個人資料
        當我發送 DELETE 請求至 "/api/profiles/delete-id"
        那麼回應的狀態碼應該是 204 (No Content)
        並且隨後查詢該 ID 時應返回 404
        
    場景: 刪除不存在的個人資料
        當我發送 DELETE 請求至 "/api/profiles/non-existent-delete-id"
        那麼回應的狀態碼應該是 404 (Not Found)
```

## API 概要

| HTTP 方法 | 路徑 | 描述 | 成功狀態碼 | 失敗狀態碼 |
| :--- | :--- | :--- | :--- | :--- |
| POST | `/api/profiles` | 建立新個人資料 | 201 Created | 400 Bad Request, 409 Conflict |
| GET | `/api/profiles/{id}` | 根據 ID 查詢資料 | 200 OK | 404 Not Found |
| PUT | `/api/profiles/{id}` | 全量更新資料 | 200 OK | 400 Bad Request, 404 Not Found |
| DELETE | `/api/profiles/{id}` | 刪除指定資料 | 204 No Content | 404 Not Found |

## 範圍外
- 用戶認證與授權（假設所有呼叫皆來自已驗證用戶，或本 Spec 僅關注資料結構本身）。
- 個人資料的複雜關聯（如頭像上傳、地址列表等），目前僅包含核心識別資訊。
- 分頁與搜尋功能。

FEATURE_NAME: personal-profile-crud
