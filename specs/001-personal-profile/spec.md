# Spec 001 — 個人資料管理 (Personal Profile CRUD)

## 背景
系統需要為註冊用戶提供維護個人基本資訊的功能，包括建立新的個人資料記錄、查詢現有資料、更新個人資訊以及刪除不再需要的資料。這確保了數據的準確性與完整性，並符合隱私保護的基本要求。

## 名詞
- **個人資料 (Profile)**：代表單一使用者的核心基本資訊集合，包含唯一的識別碼、電子郵件地址、顯示名稱等。
- **電子郵件 (Email)**：用於唯一標識使用者帳號的字串，格式需符合標準 Email 規範。
- **顯示名稱 (Display Name)**：使用者希望公開顯示的名稱，允許為空或僅含空白字符（視為無名稱），但長度有限制。

## 使用者情境

### US-1 新增個人資料
身為**新使用者**，我想要**建立我的個人資料**，以便**系統能識別我並儲存我的基本資訊**。

### US-2 查詢個人資料
身為**已登入的使用者**，我想要**根據 ID 查詢我的個人資料**，以便**確認系統中儲存的資訊是否正確**。

### US-3 修改個人資料
身為**擁有資料的使用者**，我想要**更新我的顯示名稱或其他欄位**，以便**保持個人資訊的最新狀態**。

### US-4 刪除個人資料
身為**希望移除帳戶的使用者**，我想要**永久刪除我的個人資料**，以便**徹底清除我在系統中的痕跡**。

## 驗收條件

```gherkin
功能: 個人資料 CRUD
    作為一名使用者，我需要能夠管理我的個人資料記錄。

    場景大纲: 成功新增個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """
            { "email": "<email>", "displayName": "<name>" }
            """
        那麼回應的狀態碼應該是 <statusCode>
        並且回應標頭 "Location" 應該存在且指向剛建立的資源

        範例:
            | email                  | name       | statusCode |
            | user@example.com       | John Doe   | 201        |
            | test@test.org          | Jane Smith | 201        |

    場景: 新增重複電子郵件的個人資料應失敗
        假設已存在電子郵件為 "duplicate@example.com" 的個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """
            { "email": "duplicate@example.com", "displayName": "New User" }
            """
        那麼回應的狀態碼應該是 409 (Conflict)
        並且錯誤訊息指出電子郵件已被使用

    場景: 新增格式無效的電子郵件應拒絕
        當我發送 POST 請求至 "/profiles"，內容包含:
            """
            { "email": "invalid-email", "displayName": "Bad Email" }
            """
        那麼回應的狀態碼應該是 400 (Bad Request)
        並且錯誤訊息指出電子郵件格式不正確

    場景: 成功查詢存在的個人資料
        假設系統中存在 ID 為 <id>、電子郵件為 "<email>" 的個人資料
        當我發送 GET 請求至 "/profiles/<id>"
        那麼回應的狀態碼應該是 200 (OK)
        並且回應主體中的 email 欄位等於 "<email>"

        範例:
            | id   | email              |
            | 1    | user@example.com   |

    場景: 查詢不存在的個人資料應返回 404
        當我發送 GET 請求至 "/profiles/999999"
        那麼回應的狀態碼應該是 404 (Not Found)
        並且錯誤訊息指出找不到該資料

    場景: 成功更新個人資料
        假設系統中存在 ID 為 <id> 的個人資料，其顯示名稱為 "Old Name"
        當我發送 PUT 請求至 "/profiles/<id>"，內容包含:
            """
            { "displayName": "New Name" }
            """
        那麼回應的狀態碼應該是 200 (OK)
        並且再次查詢該 ID 時，顯示名稱已變更為 "New Name"

        範例:
            | id   |
            | 1    |

    場景: 更新不存在的個人資料應返回 404
        當我發送 PUT 請求至 "/profiles/888888"，內容包含:
            """
            { "displayName": "Ghost User" }
            """
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功刪除個人資料
        假設系統中存在 ID 為 <id> 的個人資料
        當我發送 DELETE 請求至 "/profiles/<id>"
        那麼回應的狀態碼應該是 204 (No Content)
        並且隨後嘗試 GET 查詢該 ID 將返回 404

        範例:
            | id   |
            | 1    |

    場景: 刪除不存在的資料應返回 404
        當我發送 DELETE 請求至 "/profiles/777777"
        那麼回應的狀態碼應該是 404 (Not Found)
```

## API 概要

| HTTP Method | Path              | Description                  | Success Status | Failure Status       |
|-------------|-------------------|------------------------------|----------------|----------------------|
| POST        | /profiles         | 建立新個人資料               | 201 Created    | 400 Bad Request      |
|             |                   |                              |                | 409 Conflict         |
| GET         | /profiles/{id}    | 根據 ID 查詢個人資料         | 200 OK         | 404 Not Found        |
| PUT         | /profiles/{id}    | 全量更新個人資料             | 200 OK         | 400 Bad Request      |
|             |                   |                              |                | 404 Not Found        |
| DELETE      | /profiles/{id}    | 刪除指定 ID 的個人資料       | 204 No Content | 404 Not Found        |

## 範圍外
- 使用者認證與授權（假設所有操作均由合法持有有效 Token 的使用者發起，後續 Spec 處理）。
- 密碼管理、頭像上傳、社交連結等進階欄位。
- 分頁或搜尋功能（僅支援單一 ID 精確查詢）。
- 軟刪除（Soft Delete），本版本採用物理刪除。

FEATURE_NAME: personal-profile-crud
