# 🍽️ TapToEat - Restaurant Ordering System

Backend API cho ứng dụng gọi món trong nhà hàng. Hệ thống cho phép khách hàng gọi món không cần đăng nhập và đầu bếp theo dõi đơn hàng real-time.

## 🎯 Use Cases Implemented

### Customer Use Cases
- ✅ **UC-01**: Nhập số bàn và bắt đầu session
- ✅ **UC-02**: Xem menu và danh mục món
- ✅ **UC-03**: Thêm món vào giỏ hàng (local)
- ✅ **UC-04**: Đặt món (Submit Order)
- ✅ **UC-05**: Xem trạng thái món đã gọi

### Chef Use Cases
- ✅ **UC-06**: Xem danh sách order mới
- ✅ **UC-07**: Bắt đầu làm món
- ✅ **UC-08**: Xác nhận món đã xong
- ✅ **UC-09**: Cập nhật trạng thái món hết

## 🛠️ Tech Stack

- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: MongoDB + Mongoose
- **Authentication**: bcrypt (for chef accounts)
- **Middleware**: CORS, Morgan, Body-parser

## 📁 Project Structure

```
TapToEat_BE/
├── config/
│   └── db.js                    # MongoDB connection
├── models/
│   ├── Table.js                 # Quản lý bàn
│   ├── Category.js              # Danh mục món
│   ├── MenuItem.js              # Món ăn
│   ├── OrderSession.js          # Phiên gọi món
│   ├── Order.js                 # Đơn hàng
│   ├── Chef.js                  # Tài khoản đầu bếp
│   ├── Notification.js          # Thông báo
│   └── index.js
├── controllers/
│   ├── categoryController.js    # UC-02
│   ├── menuItemController.js    # UC-02, UC-09
│   ├── tableController.js       # UC-01
│   ├── orderController.js       # UC-03, UC-04, UC-05
│   └── chefController.js        # UC-06, UC-07, UC-08
├── routes/
│   ├── customer/
│   │   ├── categories.js
│   │   ├── menuItems.js
│   │   ├── tables.js
│   │   ├── sessions.js
│   │   └── orders.js
│   ├── chef/
│   │   └── index.js
│   └── index.js
├── scripts/
│   └── seedData.js              # Seed dữ liệu mẫu
├── middleware/                  # (Future: authentication)
├── .env
├── .gitignore
├── server.js
├── package.json
└── API_DOCUMENTATION.md
```

## 🚀 Installation & Setup

### 1. Clone & Install
```bash
cd TapToEat_BE
npm install
```

### 2. Environment Variables
Create `.env` file:
```env
MONGO_URI=mongodb://localhost:27017/taptoeat
PORT=9999
```

### 3. Seed Database
```bash
node scripts/seedData.js
```

This will create:
- 8 tables
- 4 categories
- 23 menu items
- 3 chef accounts

### 4. Start Server
```bash
node server.js
# or with nodemon
nodemon server.js
```

Server will run at: `http://localhost:9999`

## 📊 Database Collections

### tables
```javascript
{
  tableNumber: Number,      // Số bàn (unique)
  capacity: Number,         // Số chỗ ngồi
  status: String,           // available, occupied, reserved
  currentSession: ObjectId  // Reference to orderSession
}
```

### categories
```javascript
{
  name: String,             // Tên danh mục
  description: String,
  displayOrder: Number,     // Thứ tự hiển thị
  isActive: Boolean
}
```

### menuItems
```javascript
{
  name: String,
  price: Number,
  categoryId: ObjectId,
  isAvailable: Boolean,
  preparationTime: Number,  // Phút
  tags: [String]
}
```

### orderSessions
```javascript
{
  tableNumber: Number,
  sessionCode: String,      // TB05-20251109-001
  status: String,           // active, completed, cancelled
  totalAmount: Number
}
```

### orders
```javascript
{
  sessionId: ObjectId,
  orderNumber: String,      // ORD-20251109-001
  tableNumber: Number,
  items: [{
    menuItemId: ObjectId,
    quantity: Number,
    price: Number,
    note: String,
    status: String          // pending, preparing, ready, served
  }],
  status: String
}
```

## 🔌 API Endpoints

### Customer APIs
```
GET    /api/categories                    # Lấy danh mục
GET    /api/categories/:id/items          # Lấy món theo danh mục
GET    /api/menu-items                    # Lấy tất cả món
GET    /api/tables/:tableNumber           # Kiểm tra bàn
POST   /api/sessions                      # Tạo/lấy session
POST   /api/orders                        # Đặt món
GET    /api/orders/session/:sessionId     # Xem món đã gọi
```

### Chef APIs
```
GET    /api/chef/orders                   # Xem orders
GET    /api/chef/dashboard                # Dashboard
PATCH  /api/chef/orders/:id/items/:idx/start      # Bắt đầu làm
PATCH  /api/chef/orders/:id/items/:idx/complete   # Hoàn thành
PATCH  /api/chef/menu-items/:id/availability      # Bật/tắt món
```

📖 **Full API Documentation**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🧪 Testing

### Test với curl

**1. Tạo session:**
```bash
curl -X POST http://localhost:9999/api/sessions \
  -H "Content-Type: application/json" \
  -d '{"tableNumber": 5}'
```

**2. Xem menu:**
```bash
curl http://localhost:9999/api/categories
```

**3. Đặt món:**
```bash
curl -X POST http://localhost:9999/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "YOUR_SESSION_ID",
    "tableNumber": 5,
    "items": [{
      "menuItemId": "YOUR_MENU_ITEM_ID",
      "quantity": 2,
      "note": "Ít cay"
    }]
  }'
```

### Test với Postman
Import collection từ `API_DOCUMENTATION.md`

## 👥 Default Accounts

```
Chef 1:
  username: chef1
  password: chef123

Chef 2:
  username: chef2
  password: chef123

Admin:
  username: admin
  password: admin123
```

## 🎨 Android Integration

### Base URL
```java
// Local testing
String BASE_URL = "http://10.0.2.2:9999/api/";

// Real device (thay bằng IP máy tính)
String BASE_URL = "http://192.168.1.xxx:9999/api/";
```

### Example Retrofit Setup
```java
public interface ApiService {
    @GET("categories")
    Call<CategoryResponse> getCategories();
    
    @POST("sessions")
    Call<SessionResponse> createSession(@Body SessionRequest request);
    
    @POST("orders")
    Call<OrderResponse> createOrder(@Body OrderRequest request);
    
    @GET("orders/session/{sessionId}")
    Call<OrderListResponse> getOrdersBySession(@Path("sessionId") String sessionId);
}
```

## 📈 Future Enhancements

- [ ] WebSocket for real-time updates
- [ ] Chef authentication middleware
- [ ] Payment integration
- [ ] Order history & statistics
- [ ] Push notifications
- [ ] Image upload for menu items
- [ ] QR code for table number
- [ ] Multi-language support

## 🤝 Contributing

This is a demo project for learning purposes.

## 📝 License

ISC

## 👨‍💻 Author

PRM392 Project - Semester 8

---

**Happy Coding! 🚀**