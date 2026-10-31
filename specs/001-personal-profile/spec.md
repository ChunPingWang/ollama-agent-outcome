# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能。用戶可以建立自己的個人資料，並隨時查看、修改或刪除這些資訊。這確保了數據的準確性與用戶對自身數據的控制權。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本靜態資訊集合，包含唯一識別碼、姓名、電子郵件地址和年齡。
- **電子郵件**：用於標識用戶的唯一憑證，格式需符合標準 Email 規範，且在系統中必須保持唯一。
- **年齡**：整數值，代表用戶的實際年齡。

## 使用者情境

### US-1 新增個人資料
身為一名新用戶，我想要建立我的個人資料以便開始使用系統服務。

### US-2 查詢個人資料
身為一名已登入用戶，我想要根據 ID 查詢我的個人資料以便確認當前資料狀態。

### US-3 更新個人資料
身為一名已登入用戶，我想要修改我的個人資料（如改名或改郵箱）以便保持資訊最新。

### US-4 刪除個人資料
身為一名已登入用戶，我想要刪除我的個人資料以便停止使用該帳戶或保護隱私。

## 驗收條件

```gherkin
功能: 個人資料 CRUD 操作
    作為一名用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "王小明",
              "email": "xiaoming@example.com",
              "age": 25
            }
            """
        那麼回應的狀態碼應該是 201 (Created)
        並且回應標頭 "Location" 應該存在
        
    場景: 嘗試建立重複電子郵件的個人資料應失敗
        假設已經存在一個電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "李大明",
              "email": "duplicate@test.com",
              "age": 30
            }
            """
        那麼回應的狀態碼應該是 409 (Conflict)
        
    場景: 嘗試建立無效格式電子郵件的個人資料應失敗
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "測試用戶",
              "email": "invalid-email",
              "age": 20
            }
            """
        那麼回應的狀態碼應該是 400 (Bad Request)

    場景: 成功查詢存在的個人資料
        假設系統中已有一個 ID 為 "profile-uuid-001" 且姓名為 "王小明" 的個人資料
        當我發送 GET 請求至 "/profiles/profile-uuid-001"
        那麼回應的狀態碼應該是 200 (OK)
        並且回應內容中的 "name" 欄位應該等於 "王小明"
        
    場景: 查詢不存在的個人資料應返回 404
        當我發送 GET 請求至 "/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功更新個人資料
        假設系統中已有一個 ID 為 "profile-uuid-001" 的個人資料，當前姓名為 "舊名字"
        當我發送 PUT 請求至 "/profiles/profile-uuid-001"，內容包含:
            """json
            {
              "name": "新名字",
              "email": "new@example.com",
              "age": 26
            }
            """
        那麼回應的狀態碼應該是 200 (OK)
        並且再次查詢該 ID 時，姓名應變更為 "新名字"
        
    場景: 嘗試更新不存在 ID 的資料應返回 404
        當我發送 PUT 請求至 "/profiles/missing-id"，內容包含:
            """json
            {
              "name": "無名氏",
              "email": "test@test.com",
              "age": 18
            }
            """
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功刪除個人資料
        假設系統中已有一個 ID 為 "profile-to-delete" 的個人資料
        當我發送 DELETE 請求至 "/profiles/profile-to-delete"
        那麼回應的狀態碼應該是 204 (No Content)
        並且隨後查詢該 ID 時應返回 404
        
    場景: 刪除不存在的資料應返回 404
        當我發送 DELETE 請求至 "/profiles/non-existent-id"
        那麼回應的狀態碼應該是 404 (Not Found)
```

## API 概要

| HTTP Method | Path                  | Description          | Success Status | Error Status       |
|-------------|-----------------------|----------------------|----------------|--------------------|
| POST        | /profiles             | Create new profile   | 201 Created    | 400 Bad Request, 409 Conflict |
| GET         | /profiles/{id}        | Get profile by ID    | 200 OK         | 404 Not Found      |
| PUT         | /profiles/{id}        | Update full profile  | 200 OK         | 400 Bad Request, 404 Not Found |
| DELETE      | /profiles/{id}        | Delete profile       | 204 No Content | 404 Not Found      |

## 範圍外
- 用戶認證與授權（假設所有操作均由已驗證用戶發起，或本 Spec 僅關注資料結構邏輯）。
- 個人資料的歷史版本追蹤。
- 檔案上傳（如頭像）。
- 分頁與列表查詢功能。

FEATURE_NAME: personal-profile-crud
