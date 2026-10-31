# language: zh-TW
功能：個人資料 CRUD 管理
    作為一名使用者，我需要能夠對個人資料執行增刪改查操作，以確保資料的準確性與完整性。

    場景大劃：成功新增個人資料
        假設沒有重複的電子郵件
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "<名稱>",
              "email": "<電子郵件>"
            }
            """
        那麼 HTTP 回應狀態碼應為 201 Created
        並且回應標頭 "Location" 不應為空
        並且我可以透過該 Location 路徑查詢到剛建立的資料

        範例：
            | 名稱     | 電子郵件             |
            |----------|----------------------|
            | 王小明   | wang@example.com     |
            | 李阿美   | li@example.com       |

    場景：嘗試建立已有相同電子郵件的新資料
        假設已存在一筆電子郵件為 "duplicate@test.com" 的個人資料
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": "新用戶",
              "email": "duplicate@test.com"
            }
            """
        那麼 HTTP 回應狀態碼應為 409 Conflict
        並且錯誤訊息指出電子郵件重複

    場景：新增資料時缺少必要欄位
        當我發送 POST 請求至 "/profiles"，內容包含:
            """json
            {
              "name": ""
            }
            """
        那麼 HTTP 回應狀態碼應為 400 Bad Request
        並且錯誤訊息指出 email 欄位為必填且格式正確

    場景大劃：成功查詢存在的個人資料
        假設系統中存在 ID 為 <id> 的個人資料
        當我發送 GET 請求至 "/profiles/<id>"
        那麼 HTTP 回應狀態碼應為 200 OK
        並且回傳的 JSON 中 name 等於 "<名稱>"
        並且回傳的 JSON 中 email 等於 "<電子郵件>"

        範例：
            | id   | 名稱     | 電子郵件             |
            |------|----------|----------------------|
            | 1    | 王小明   | wang@example.com     |

    場景：查詢不存在的個人資料
        當我發送 GET 請求至 "/profiles/99999"（該 ID 不存在）
        那麼 HTTP 回應狀態碼應為 404 Not Found
        並且錯誤訊息指出找不到該筆資料

    場景大劃：成功更新個人資料
        假設系統中存在 ID 為 <id> 的個人資料，目前名稱為 "舊名字"
        當我發送 PUT 請求至 "/profiles/<id>"，內容包含:
            """json
            {
              "name": "新名字",
              "email": "<電子郵件>"
            }
            """
        那麼 HTTP 回應狀態碼應為 200 OK
        並且再次查詢該 ID，其 name 變更為 "新名字"
        並且 email 保持不變

        範例：
            | id   | 電子郵件               |
            |------|------------------------|
            | 1    | update_test@example.com|

    場景：嘗試更新不存在的資料
        當我發送 PUT 請求至 "/profiles/88888"，內容包含:
            """json
            {
              "name": "無效用戶",
              "email": "invalid@test.com"
            }
            """
        那麼 HTTP 回應狀態碼應為 404 Not Found

    場景大劃：刪除存在的個人資料
        假設系統中存在 ID 為 <id> 的個人資料
        當我發送 DELETE 請求至 "/profiles/<id>"
        那麼 HTTP 回應狀態碼應為 204 No Content
        並且隨後發送 GET 請求至同一路徑時，回傳 404 Not Found

        範例：
            | id |
            |----|
            | 1  |

    場景：刪除不存在的資料
        當我發送 DELETE 請求至 "/profiles/77777"（該 ID 不存在）
        那麼 HTTP 回應狀態碼應為 404 Not Found
