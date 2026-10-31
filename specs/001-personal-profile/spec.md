# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供個人基本資訊的管理能力。用戶可以建立自己的個人資料，並隨時查看、修改或刪除這些資訊。這是最基礎的用戶數據持久化需求，後續的功能模組將依賴此資料進行權限判斷與業務邏輯處理。

## 名詞
- **個人資料 (Profile)**：代表單一用戶的基本靜態資訊集合，包含唯一識別碼、姓名、電子郵件地址與年齡。
- **電子郵件 (Email)**：用於標識用戶的唯一聯絡方式，格式需符合標準 Email 規範，且在系統中必須保持全域唯一。
- **年齡 (Age)**：用戶的實際年齡，必須為非負整數。

## 使用者情境

### US-1 新增個人資料
身為**新用戶**，我想要**建立我的個人資料**，以便**讓系統記錄我的基本資訊**。

### US-2 查詢個人資料
身為**已登入用戶**，我想要**根據 ID 查詢我的個人資料**，以便**確認我儲存的資訊是否正確**。

### US-3 更新個人資料
身為**擁有個人資料的用戶**，我想要**修改我的姓名或年齡**，以便**確保我的資訊是最新的**。

### US-4 刪除個人資料
身為**希望退出服務的用戶**，我想要**永久刪除我的個人資料**，以便**保護我的隱私並釋放資源**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD
    作為一名用戶
    我希望能夠管理我的個人資料
    
    場景: 成功建立新的個人資料
        假設 沒有使用相同電子郵件的其他資料存在
        當 我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "王小明",
              "email": "xiaoming@example.com",
              "age": 25
            }
            """
        那麼 HTTP 狀態碼應為 201 Created
        並且 回應中包含新建立的 profile id
        
    場景: 建立失敗 - 缺少必要欄位
        當 我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "李大明"
            }
            """
        那麼 HTTP 狀態碼應為 400 Bad Request
        並且 錯誤訊息指出 email 和 age 是必填項
        
    場景: 建立失敗 - 重複的電子郵件
        假設 已存在一筆 email 為 "duplicate@test.com" 的資料
        當 我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "張三",
              "email": "duplicate@test.com",
              "age": 30
            }
            """
        那麼 HTTP 狀態碼應為 409 Conflict
        並且 錯誤訊息指出該電子郵件已被使用

    場景: 成功查詢存在的個人資料
        假設 系統中已存在 ID 為 "profile-uuid-001" 且 name 為 "王小明" 的資料
        當 我發送 GET 請求至 "/profiles/profile-uuid-001"
        那麼 HTTP 狀態碼應為 200 OK
        並且 回應中的 name 應為 "王小明"
        
    場景: 查詢失敗 - 查無此資料
        當 我發送 GET 請求至 "/profiles/non-existent-id"
        那麼 HTTP 狀態碼應為 404 Not Found
        
    場景: 成功更新個人資料
        假設 系統中已存在 ID 為 "profile-uuid-001" 的資料
        當 我發送 PUT 請求至 "/profiles/profile-uuid-001"，內容包含:
            """json
            {
              "name": "王小名",
              "email": "xiaoming@example.com",
              "age": 26
            }
            """
        那麼 HTTP 狀態碼應為 200 OK
        並且 回應中的 name 應變更為 "王小名"
        並且 age 應變更為 26
        
    場景: 更新失敗 - 欄位驗證錯誤 (年齡為負數)
        假設 系統中已存在 ID 為 "profile-uuid-001" 的資料
        當 我發送 PUT 請求至 "/profiles/profile-uuid-001"，內容包含:
            """json
            {
              "name": "王小明",
              "email": "xiaoming@example.com",
              "age": -5
            }
            """
        那麼 HTTP 狀態碼應為 400 Bad Request
        並且 錯誤訊息指出 age 必須大於或等於零

    場景: 成功刪除個人資料
        假設 系統中已存在 ID 為 "profile-uuid-001" 的資料
        當 我發送 DELETE 請求至 "/profiles/profile-uuid-001"
        那麼 HTTP 狀態碼應為 204 No Content
        並且 再次查詢該 ID 時應返回 404 Not Found
```

## API 概要

| HTTP Method | Path | Description | Success Status | Error Status |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/profiles` | 建立新個人資料 | 201 Created | 400 (驗證失敗), 409 (重複) |
| `GET` | `/profiles/{id}` | 根據 ID 查詢資料 | 200 OK | 404 (查無此筆) |
| `PUT` | `/profiles/{id}` | 全量更新資料 | 200 OK | 400 (驗證失敗), 404 (查無此筆) |
| `DELETE` | `/profiles/{id}` | 刪除指定資料 | 204 No Content | 404 (查無此筆) |

## 範圍外
- 用戶認證與授權（假設所有操作均來自已驗證的合法請求，後續模組處理）。
- 個人資料的分頁列表查詢。
- 歷史版本追蹤或軟刪除機制。
- 檔案上傳（如頭像）。

FEATURE_NAME: personal-profile-crud
