# TapToEat - Restaurant Ordering System

## 📱 Màn hình đã triển khai

### ✅ Màn hình 1: Nhập số bàn (UC-01)

**Chức năng:**
- Khách hàng nhập số bàn để bắt đầu session
- Validate số bàn hợp lệ
- Kiểm tra bàn có tồn tại trong hệ thống
- Tạo hoặc lấy session hiện tại của bàn

**Files đã tạo:**

#### Layouts:
- `activity_table_input.xml` - Giao diện màn hình nhập số bàn

#### Activities:
- `MainActivity.java` - Màn hình splash/router kiểm tra session
- `TableInputActivity.java` - Xử lý logic nhập số bàn

#### Models:
- `models/Table.java` - Model cho bàn ăn
- `models/Session.java` - Model cho session đặt món

#### Utils:
- `utils/SessionManager.java` - Quản lý session local
- `api/ApiConfig.java` - Configuration cho API endpoints

#### Resources:
- `drawable/btn_primary.xml` - Button style
- `drawable/ic_welcome_placeholder.xml` - Welcome image placeholder
- `values/colors.xml` - Màu sắc app
- `values/strings.xml` - Chuỗi text

## 🚀 Cách chạy

### 1. Setup Backend (ExpressJS)
```bash
# TODO: Thêm hướng dẫn setup backend
# Backend cần có các endpoints:
# - POST /api/tables/verify - Kiểm tra bàn có tồn tại
# - GET /api/sessions/table/:tableNumber - Lấy session của bàn
# - POST /api/sessions - Tạo session mới
```

### 2. Cấu hình API URL
Mở file `ApiConfig.java` và cập nhật BASE_URL:

```java
// For Android Emulator
public static final String BASE_URL = "http://10.0.2.2:3000/api/";

// For Physical Device - Thay YOUR_IP bằng IP máy tính chạy backend
// public static final String BASE_URL = "http://YOUR_IP:3000/api/";
```

### 3. Chạy app
1. Mở project trong Android Studio
2. Sync Gradle
3. Chạy app trên emulator hoặc device

## 📝 TODO - Các màn hình tiếp theo

- [ ] UC-02: Màn hình Menu và Categories
- [ ] UC-03: Màn hình Chi tiết món và Giỏ hàng
- [ ] UC-04: Màn hình Đặt món
- [ ] UC-05: Màn hình Trạng thái món đã gọi
- [ ] UC-06-09: Màn hình Chef (Web hoặc Admin app)

## 🔧 Dependencies hiện tại

```gradle
implementation("androidx.appcompat:appcompat:1.7.1")
implementation("com.google.android.material:material:1.13.0")
implementation("androidx.constraintlayout:constraintlayout:2.2.1")
```

## 📦 Dependencies cần thêm cho các màn hình tiếp theo

```gradle
// Retrofit for API calls
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Glide for image loading
implementation 'com.github.bumptech.glide:glide:4.16.0'

// RecyclerView (should be included in appcompat)
// implementation 'androidx.recyclerview:recyclerview:1.3.2'

// CardView
implementation 'androidx.cardview:cardview:1.0.0'

// Lifecycle components
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'
```

## 📱 Screenshots

### Màn hình Nhập số bàn
- Header: "Nhà Hàng Quang Duy"
- Welcome image placeholder
- Text: "Xin chào quý khách"
- Subtitle: "Chúc quý khách ngon miệng"
- Input field: Nhập số bàn
- Button: "Bắt đầu"

## 🎨 Design System

### Colors
- Primary: #2196F3 (Blue)
- Accent: #FF5722 (Deep Orange)
- Background: #F5F5F5 (Light Grey)
- Status Colors:
  - Pending: #FFC107 (Amber)
  - Preparing: #2196F3 (Blue)
  - Ready: #4CAF50 (Green)
  - Served: #9E9E9E (Grey)

## 📞 Support
Liên hệ team phát triển nếu có vấn đề!
