# API Contract - TapToEat Backend

## Base URL
```
http://localhost:3000/api
```

## 📋 API Endpoints cho UC-01: Nhập số bàn

### 1. Verify Table (Kiểm tra bàn có tồn tại)

**Endpoint:** `POST /api/tables/verify`

**Request Body:**
```json
{
  "tableNumber": 5
}
```

**Response Success (200):**
```json
{
  "success": true,
  "data": {
    "id": "table_001",
    "tableNumber": 5,
    "capacity": 4,
    "status": "available",
    "currentSessionId": null
  }
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Không tìm thấy bàn này"
}
```

---

### 2. Get or Create Session (Lấy hoặc tạo session cho bàn)

**Endpoint:** `GET /api/sessions/table/:tableNumber`

**Response Success (200) - Session đã tồn tại:**
```json
{
  "success": true,
  "data": {
    "id": "session_001",
    "tableId": "table_001",
    "tableNumber": 5,
    "startTime": "2024-11-09T10:30:00Z",
    "endTime": null,
    "status": "active",
    "orderIds": ["order_001", "order_002"],
    "totalAmount": 250000
  }
}
```

**Response Success (200) - Không có session:**
```json
{
  "success": true,
  "data": null
}
```

---

### 3. Create Session (Tạo session mới)

**Endpoint:** `POST /api/sessions`

**Request Body:**
```json
{
  "tableId": "table_001",
  "tableNumber": 5
}
```

**Response Success (201):**
```json
{
  "success": true,
  "data": {
    "id": "session_001",
    "tableId": "table_001",
    "tableNumber": 5,
    "startTime": "2024-11-09T10:30:00Z",
    "endTime": null,
    "status": "active",
    "orderIds": [],
    "totalAmount": 0
  }
}
```

---

## 📦 MongoDB Schema Suggestions

### Tables Collection
```javascript
{
  _id: ObjectId,
  tableNumber: Number,      // unique
  capacity: Number,
  status: String,           // "available", "occupied", "reserved"
  currentSessionId: ObjectId,
  createdAt: Date,
  updatedAt: Date
}
```

### Sessions Collection
```javascript
{
  _id: ObjectId,
  tableId: ObjectId,
  tableNumber: Number,
  startTime: Date,
  endTime: Date,
  status: String,           // "active", "completed", "cancelled"
  orderIds: [ObjectId],
  totalAmount: Number,
  createdAt: Date,
  updatedAt: Date
}
```

---

## 🔄 Business Logic Flow

### Khi khách nhập số bàn:

1. **Frontend gọi:** `POST /api/tables/verify`
   - Kiểm tra bàn có tồn tại không
   - Nếu không → hiển thị lỗi
   - Nếu có → tiếp tục bước 2

2. **Frontend gọi:** `GET /api/sessions/table/:tableNumber`
   - Kiểm tra có session active không
   - Nếu có → sử dụng session đó
   - Nếu không → tạo session mới (bước 3)

3. **Frontend gọi:** `POST /api/sessions` (nếu cần)
   - Tạo session mới
   - Cập nhật status bàn → "occupied"
   - Lưu sessionId vào bàn

4. **Lưu session local** (SharedPreferences)
   - Lưu tableNumber, sessionId, tableId
   - Navigate to Menu screen

---

## ⚠️ Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 404 | Table not found | Bàn không tồn tại |
| 400 | Invalid table number | Số bàn không hợp lệ |
| 500 | Server error | Lỗi server |

---

## 🧪 Test Cases

### Test Case 1: Bàn hợp lệ, chưa có session
```
Input: tableNumber = 5
Expected: 
- Verify table → Success
- Get session → null
- Create session → Success
- Navigate to Menu
```

### Test Case 2: Bàn hợp lệ, đã có session
```
Input: tableNumber = 5
Expected:
- Verify table → Success
- Get session → Session exists
- Navigate to Menu với session cũ
```

### Test Case 3: Bàn không tồn tại
```
Input: tableNumber = 999
Expected:
- Verify table → 404 Error
- Show error message
```

### Test Case 4: Input không hợp lệ
```
Input: tableNumber = "" hoặc "abc" hoặc -1
Expected:
- Show validation error
- Don't call API
```
