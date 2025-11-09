# Quick Test Guide

## ✅ Đã hoàn thành triển khai 9 Use Cases

Server đang chạy tại: `http://localhost:9999`

---

## 🧪 Quick Tests

### 1️⃣ Test API Root
```bash
curl http://localhost:9999/api
```

### 2️⃣ Test UC-02: Xem Categories
```bash
curl http://localhost:9999/api/categories
```

### 3️⃣ Test UC-02: Xem Menu Items
```bash
curl http://localhost:9999/api/menu-items
```

### 4️⃣ Test UC-01: Kiểm tra bàn
```bash
curl http://localhost:9999/api/tables/5
```

### 5️⃣ Test UC-01: Tạo Session
```bash
curl -X POST http://localhost:9999/api/sessions \
  -H "Content-Type: application/json" \
  -d "{\"tableNumber\": 5}"
```

**Lưu sessionId từ response để dùng cho bước tiếp theo!**

### 6️⃣ Test UC-04: Đặt món (cần sessionId và menuItemId)

Trước tiên lấy menuItemId:
```bash
curl http://localhost:9999/api/menu-items | grep _id
```

Sau đó đặt món:
```bash
curl -X POST http://localhost:9999/api/orders \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\": \"YOUR_SESSION_ID\", \"tableNumber\": 5, \"items\": [{\"menuItemId\": \"YOUR_MENU_ITEM_ID\", \"quantity\": 2, \"note\": \"Ít cay\"}]}"
```

### 7️⃣ Test UC-05: Xem món đã gọi
```bash
curl http://localhost:9999/api/orders/session/YOUR_SESSION_ID
```

### 8️⃣ Test UC-06: Chef xem orders
```bash
curl http://localhost:9999/api/chef/orders
```

### 9️⃣ Test UC-07: Chef bắt đầu làm món
```bash
curl -X PATCH http://localhost:9999/api/chef/orders/YOUR_ORDER_ID/items/0/start
```

### 🔟 Test UC-08: Chef hoàn thành món
```bash
curl -X PATCH http://localhost:9999/api/chef/orders/YOUR_ORDER_ID/items/0/complete
```

---

## 📱 Test Flow - Complete Scenario

### Scenario: Khách ở bàn 5 gọi món Phở Bò và Cà Phê

**Step 1: Lấy danh sách categories**
```bash
curl http://localhost:9999/api/categories
```

**Step 2: Xem món trong category "Món Chính"**
```bash
curl http://localhost:9999/api/categories/CATEGORY_ID/items
```

**Step 3: Khách nhập số bàn và tạo session**
```bash
curl -X POST http://localhost:9999/api/sessions \
  -H "Content-Type: application/json" \
  -d '{"tableNumber": 5}'
```

**Step 4: Đặt món**
```bash
curl -X POST http://localhost:9999/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "SESSION_ID_FROM_STEP_3",
    "tableNumber": 5,
    "items": [
      {
        "menuItemId": "PHO_BO_ID",
        "quantity": 1,
        "note": "Ít hành"
      },
      {
        "menuItemId": "CA_PHE_ID",
        "quantity": 1,
        "note": ""
      }
    ]
  }'
```

**Step 5: Khách kiểm tra trạng thái món**
```bash
curl http://localhost:9999/api/orders/session/SESSION_ID
```

**Step 6: Chef xem order mới**
```bash
curl http://localhost:9999/api/chef/orders?status=pending
```

**Step 7: Chef bắt đầu làm Phở Bò (item index 0)**
```bash
curl -X PATCH http://localhost:9999/api/chef/orders/ORDER_ID/items/0/start
```

**Step 8: Khách check lại → thấy món "preparing" 👨‍🍳**
```bash
curl http://localhost:9999/api/orders/session/SESSION_ID
```

**Step 9: Chef hoàn thành Phở Bò**
```bash
curl -X PATCH http://localhost:9999/api/chef/orders/ORDER_ID/items/0/complete
```

**Step 10: Khách check lại → thấy món "ready" ✅**
```bash
curl http://localhost:9999/api/orders/session/SESSION_ID
```

---

## 🎯 Test với Postman/Insomnia

### Collection Structure
```
TapToEat API
├── Customer
│   ├── Get Categories
│   ├── Get Menu Items
│   ├── Check Table
│   ├── Create Session
│   ├── Create Order
│   └── Get Orders by Session
└── Chef
    ├── Get Orders
    ├── Get Dashboard
    ├── Start Item
    ├── Complete Item
    └── Toggle Menu Item
```

---

## 🐛 Common Issues

### Issue: "Không tìm thấy bàn"
**Solution**: Chạy seed data trước
```bash
node scripts/seedData.js
```

### Issue: "Món không có sẵn"
**Solution**: Check `isAvailable` field hoặc re-seed

### Issue: "Session không còn active"
**Solution**: Tạo session mới

### Issue: CORS error từ Android
**Solution**: CORS đã được enable trong server.js

---

## 📊 Expected Data After Seed

- **Tables**: 8 bàn (số 1-8)
- **Categories**: 4 danh mục (Khai Vị, Món Chính, Đồ Uống, Tráng Miệng)
- **Menu Items**: 23 món ăn
- **Chef Accounts**: 3 accounts (chef1, chef2, admin)

---

## ✨ Features Implemented

✅ Customer không cần login
✅ Nhiều khách cùng bàn có thể gọi món (cùng session)
✅ Tracking trạng thái món real-time
✅ Chef dashboard với thống kê
✅ Highlight orders chờ quá lâu
✅ Notification system
✅ Status flow: pending → preparing → ready → served
✅ Lưu giá món tại thời điểm order
✅ Validation đầy đủ

---

## 🚀 Next Steps for Android App

1. **Setup Retrofit** với base URL
2. **Create Models** tương ứng với API responses
3. **Implement Customer Flow**:
   - Screen: Input số bàn
   - Screen: Menu categories
   - Screen: Menu items
   - Screen: Cart (local)
   - Screen: Order tracking
   
4. **Implement Chef Flow** (Optional):
   - Screen: Orders list (tabs: pending/preparing/ready)
   - Screen: Order details
   - Action: Start/Complete items

5. **Add Polling** để update trạng thái món (mỗi 5-10s)

---

**Ready to integrate with Android! 🎉**