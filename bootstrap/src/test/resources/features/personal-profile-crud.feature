# language: zh-TW

功能: 個人資料 CRUD
	作為一名用戶
	我希望能夠管理我的個人資料
	
	場景: 成功建立新的個人資料
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
