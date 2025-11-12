# TapToEat API Documentation

## Base URL
```
http://localhost:9999/api
```

---

## CUSTOMER APIs

### 📋 UC-01: Nhập số bàn và bắt đầu session

#### Kiểm tra bàn
```
GET /api/tables/:tableNumber
```

**Example:**
```bash
GET /api/tables/5
```

**Response:**
```json
{
  "success": true,
  "data": {
    "_id": "...",
    "tableNumber": 5,
    "capacity": 2,
    "status": "available",
    "currentSession": null
  }
}
```

#### Tạo hoặc lấy session
```
POST /api/sessions
```

**Body:**
```json
{
  "tableNumber": 5
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tạo session mới thành công",
  "data": {
    "_id": "session_id",
    "tableNumber": 5,
    "sessionCode": "TB05-20251109-001",
    "status": "active",
    "startTime": "2025-11-09T10:00:00Z",
    "totalAmount": 0
  },
  "isNew": true
}
```

---

### 🍽️ UC-02: Xem menu và danh mục món

#### Lấy danh sách categories
```
GET /api/categories
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "_id": "...",
      "name": "Món Chính",
      "description": "Các món ăn chính phong phú",
      "displayOrder": 1,
      "isActive": true
    }
  ]
}
```

#### Lấy món theo category
```
GET /api/categories/:id/items
```

**Response:**
```json
{
  "success": true,
  "data": {
    "category": {...},
    "items": [
      {
        "_id": "...",
        "name": "Phở Bò",
        "description": "Phở bò truyền thống Hà Nội",
        "price": 50000,
        "preparationTime": 20,
        "isAvailable": true,
        "tags": ["popular", "traditional"]
      }
    ],
    "totalItems": 7
  }
}
```

#### Lấy tất cả món (có filter)
```
GET /api/menu-items?categoryId=xxx&tags=popular&search=phở
```

**Query Parameters:**
- `categoryId`: Filter theo category
- `tags`: Filter theo tags (comma-separated)
- `search`: Tìm kiếm theo tên/mô tả
- `isAvailable`: true/false (default: true)

---

### 🛒 UC-03 & UC-04: Thêm món và đặt món

#### Tạo order mới
```
POST /api/orders
```

**Body:**
```json
{
  "sessionId": "session_id",
  "tableNumber": 5,
  "items": [
    {
      "menuItemId": "menu_item_id",
      "quantity": 2,
      "note": "Ít cay"
    },
    {
      "menuItemId": "another_item_id",
      "quantity": 1,
      "note": ""
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đặt món thành công",
  "data": {
    "_id": "order_id",
    "orderNumber": "ORD-20251109-001",
    "sessionId": "...",
    "tableNumber": 5,
    "items": [
      {
        "menuItemId": "...",
        "menuItemName": "Phở Bò",
        "quantity": 2,
        "price": 50000,
        "note": "Ít cay",
        "status": "pending",
        "orderedAt": "2025-11-09T10:05:00Z"
      }
    ],
    "totalAmount": 100000,
    "status": "pending"
  }
}
```

---

### 👀 UC-05: Xem trạng thái món đã gọi

#### Lấy orders của session
```
GET /api/orders/session/:sessionId
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "_id": "...",
      "orderNumber": "ORD-20251109-001",
      "tableNumber": 5,
      "items": [
        {
          "menuItemName": "Phở Bò",
          "quantity": 2,
          "status": "preparing",
          "orderedAt": "2025-11-09T10:05:00Z",
          "preparedAt": "2025-11-09T10:06:00Z"
        }
      ],
      "status": "preparing",
      "createdAt": "2025-11-09T10:05:00Z"
    }
  ],
  "totalOrders": 1
}
```

**Item Status:**
- `pending`: ⏳ Đang chờ
- `preparing`: 👨‍🍳 Đang làm
- `ready`: ✅ Đã xong
- `served`: 🍽️ Đã phục vụ

---

## CHEF APIs

### 👨‍🍳 UC-06: Xem danh sách order mới

#### Lấy orders cho chef
```
GET /api/chef/orders?status=pending&status=preparing
```

**Query Parameters:**
- `status`: Filter theo status (có thể nhiều status)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "_id": "...",
      "orderNumber": "ORD-20251109-001",
      "tableNumber": 5,
      "items": [...],
      "status": "pending",
      "createdAt": "2025-11-09T10:05:00Z",
      "waitTime": 15,
      "isUrgent": false
    }
  ],
  "stats": {
    "total": 5,
    "pending": 3,
    "preparing": 2,
    "ready": 0
  }
}
```

#### Dashboard thống kê
```
GET /api/chef/dashboard
```

**Response:**
```json
{
  "success": true,
  "data": {
    "currentOrders": {
      "pending": 3,
      "preparing": 2,
      "ready": 1,
      "total": 6
    },
    "urgentOrders": [...],
    "todayStats": {
      "totalOrders": 25,
      "completed": 20
    }
  }
}
```

---

### 🔥 UC-07: Bắt đầu làm món

#### Bắt đầu làm 1 món
```
PATCH /api/chef/orders/:orderId/items/:itemIndex/start
```

**Example:**
```
PATCH /api/chef/orders/67890/items/0/start
```

**Response:**
```json
{
  "success": true,
  "message": "Đã bắt đầu làm món \"Phở Bò\"",
  "data": {...}
}
```

#### Bắt đầu làm toàn bộ order
```
PATCH /api/chef/orders/:orderId/start
```

---

### ✅ UC-08: Hoàn thành món

#### Hoàn thành 1 món
```
PATCH /api/chef/orders/:orderId/items/:itemIndex/complete
```

**Response:**
```json
{
  "success": true,
  "message": "Món \"Phở Bò\" đã sẵn sàng",
  "data": {...}
}
```

---

### 🔧 UC-09: Cập nhật trạng thái món

#### Bật/tắt món
```
PATCH /api/chef/menu-items/:id/availability
```

**Body:**
```json
{
  "isAvailable": false
}
```

**Response:**
```json
{
  "success": true,
  "message": "Món đã được tắt",
  "data": {...}
}
```

---

## Status Flow

### Order Status Flow:
```
pending → preparing → ready → served
```

### Item Status Flow:
```
pending → preparing → ready → served
```

---

## Error Responses

```json
{
  "success": false,
  "message": "Error message in Vietnamese",
  "error": "Technical error details"
}
```

**Common Status Codes:**
- `200`: Success
- `201`: Created
- `400`: Bad Request (validation error)
- `404`: Not Found
- `500`: Internal Server Error

---

## Testing với curl

### Test UC-01: Tạo session
```bash
curl -X POST http://localhost:9999/api/sessions \
  -H "Content-Type: application/json" \
  -d '{"tableNumber": 5}'
```

### Test UC-04: Đặt món
```bash
curl -X POST http://localhost:9999/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "YOUR_SESSION_ID",
    "tableNumber": 5,
    "items": [
      {
        "menuItemId": "YOUR_MENU_ITEM_ID",
        "quantity": 2,
        "note": "Ít cay"
      }
    ]
  }'
```

### Test UC-07: Bắt đầu làm món
```bash
curl -X PATCH http://localhost:9999/api/chef/orders/ORDER_ID/items/0/start
```

---

---

### 💳 UC-10: Thanh toán và kết thúc session

#### Thanh toán và giải phóng bàn
```
POST /api/sessions/:sessionId/payment
```

**Body:**
```json
{
  "paymentMethod": "cash"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Thanh toán thành công! Cảm ơn quý khách.",
  "data": {
    "sessionId": "session_id",
    "sessionCode": "TB05-20251112-001",
    "tableNumber": 5,
    "totalAmount": 450000,
    "paymentMethod": "cash",
    "startTime": "2025-11-12T10:00:00Z",
    "endTime": "2025-11-12T12:30:00Z"
  }
}
```

**Response (Error - Món chưa sẵn sàng):**
```json
{
  "success": false,
  "message": "Vẫn còn món chưa sẵn sàng. Không thể thanh toán!"
}
```

**Note:** 
- Chỉ có thể thanh toán khi tất cả món ở trạng thái `ready`
- Sau khi thanh toán, session sẽ chuyển sang `completed`
- Bàn sẽ được giải phóng về trạng thái `available`
- Tất cả orders sẽ được cập nhật sang `completed`

---

## Notes for Android Development

1. **Base URL**: Thay `localhost` bằng IP máy tính khi test trên thiết bị thật
   ```
   http://192.168.1.xxx:9999/api
   ```

2. **Content-Type**: Luôn set header `Content-Type: application/json`

3. **Error Handling**: Check `success` field trong response

4. **Real-time Updates**: Implement polling hoặc WebSocket để cập nhật trạng thái món

5. **Local Storage**: Lưu `sessionId` trong SharedPreferences để khách có thể tiếp tục session

6. **Session Management**: 
   - Khi nhập số bàn, kiểm tra session hiện tại
   - Cho phép truy cập session `occupied` để xem giỏ hàng và trạng thái món
   - Khi thanh toán, session sẽ được kết thúc và bàn được giải phóng

---

## Sequence Diagram - Customer Flow

```
Customer → Check Table → Create/Get Session → View Menu → Add to Cart → Create Order → Track Status → Payment → End Session
```

## Sequence Diagram - Chef Flow

```
Chef → View Orders → Start Preparing → Complete Item → Notify Customer
```