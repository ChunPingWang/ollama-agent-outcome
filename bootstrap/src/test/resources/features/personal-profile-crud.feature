# language: zh-TW

功能: 個人資料 CRUD
    作為一名使用者，我需要能夠管理我的個人資料記錄。

    場景大綱: 成功新增個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """
            { "email": "<email>", "displayName": "<name>" }
            """
        那麼回應的狀態碼應該是 <statusCode>
        並且回應標頭 "Location" 應該存在且指向剛建立的資源

        例子:
            | email                  | name       | statusCode |
            | user@example.com       | John Doe   | 201        |
            | test@test.org          | Jane Smith | 201        |

    場景: 新增重複電子郵件的個人資料應失敗
        假設系統中已存在電子郵件為 "duplicate@example.com" 的個人資料
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
        假設系統中存在 ID 為 "<id>"、電子郵件為 "<email>" 的個人資料
        當我發送 GET 請求至 "/profiles/<id>"
        那麼回應的狀態碼應該是 200 (OK)
        並且回應主體中的 email 欄位等於 "<email>"

        例子:
            | id   | email              |
            | 1    | user@example.com   |

    場景: 查詢不存在的個人資料應返回 404
        當我發送 GET 請求至 "/profiles/00000000-0000-0000-0000-000000000001"
        那麼回應的狀態碼應該是 404 (Not Found)
        並且錯誤訊息指出找不到該資料

    場景: 成功更新個人資料
        假設系統中存在 ID 為 "<id>" 的個人資料，其顯示名稱為 "Old Name"
        當我發送 PUT 請求至 "/profiles/<id>"，內容包含:
            """
            { "displayName": "New Name" }
            """
        那麼回應的狀態碼應該是 200 (OK)
        並且再次查詢該 ID 時，顯示名稱已變更為 "New Name"

        例子:
            | id   |
            | 1    |

    場景: 更新不存在的個人資料應返回 404
        當我發送 PUT 請求至 "/profiles/00000000-0000-0000-0000-000000000002"，內容包含:
            """
            { "displayName": "Ghost User" }
            """
        那麼回應的狀態碼應該是 404 (Not Found)

    場景: 成功刪除個人資料
        假設系統中存在 ID 為 "<id>" 的個人資料
        當我發送 DELETE 請求至 "/profiles/<id>"
        那麼回應的狀態碼應該是 204 (No Content)
        並且隨後嘗試 GET 查詢該 ID 將返回 404

        例子:
            | id   |
            | 1    |

    場景: 刪除不存在的資料應返回 404
        當我發送 DELETE 請求至 "/profiles/00000000-0000-0000-0000-000000000003"
        那麼回應的狀態碼應該是 404 (Not Found)
