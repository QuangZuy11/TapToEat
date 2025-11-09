# 🚀 Hướng dẫn Setup và Chạy App

## 📋 Yêu cầu

- **Android Studio:** Arctic Fox trở lên
- **JDK:** Java 8 trở lên
- **Android SDK:** API Level 27+ (Android 8.1) đến 34 (Android 14)
- **Backend:** ExpressJS + MongoDB (xem API_CONTRACT.md)

---

## 🔧 Setup Project

### 1. Mở Project trong Android Studio

```bash
# Clone hoặc mở thư mục project
# File → Open → Chọn thư mục TapToEat_FE
```

### 2. Sync Gradle

- Android Studio sẽ tự động sync
- Hoặc click **File → Sync Project with Gradle Files**
- Đợi download dependencies

### 3. Cấu hình Backend URL

Mở file: `app/src/main/java/vn/edu/fpt/taptoeat/api/ApiConfig.java`

```java
// Cho Android Emulator
public static final String BASE_URL = "http://10.0.2.2:3000/api/";

// Cho Physical Device (thay YOUR_COMPUTER_IP)
// Cách lấy IP: cmd → ipconfig → IPv4 Address
// public static final String BASE_URL = "http://192.168.1.100:3000/api/";
```

---

## ▶️ Chạy App

### Option 1: Android Emulator

1. **Tạo Virtual Device:**
   - Tools → Device Manager
   - Create Device → Chọn Pixel 4/5/6
   - Download System Image (API 33 recommended)
   - Finish

2. **Run App:**
   - Click nút ▶️ (Run) hoặc Shift + F10
   - Chọn emulator vừa tạo
   - Wait for app to install and launch

### Option 2: Physical Device

1. **Enable Developer Mode:**
   - Settings → About Phone
   - Tap "Build Number" 7 lần
   
2. **Enable USB Debugging:**
   - Settings → Developer Options
   - Enable "USB Debugging"

3. **Connect Device:**
   - Cắm USB vào máy tính
   - Chấp nhận "Allow USB Debugging" trên điện thoại

4. **Run App:**
   - Click nút ▶️ (Run)
   - Chọn device của bạn

---

## 🧪 Test App (Không có Backend)

App hiện tại có thể chạy **MOCK** mà không cần backend:

### Flow Test:

1. **Mở app** → Tự động navigate to Table Input
2. **Nhập số bàn:** Ví dụ `5`
3. **Click "Bắt đầu"**
4. **Kết quả:** 
   - Loading 1 giây (giả lập API call)
   - Toast message: "Chào mừng đến bàn 5"
   - (Chưa navigate vì chưa có Menu screen)

### Test Cases:

| Input | Expected Result |
|-------|----------------|
| (empty) | "Vui lòng nhập số bàn" |
| `abc` | "Số bàn không hợp lệ" |
| `-1` | "Số bàn không hợp lệ" |
| `0` | "Số bàn không hợp lệ" |
| `5` | Success toast |
| `999` | Success toast (mock) |

---

## 🔗 Kết nối Backend

### 1. Setup Backend (ExpressJS)

Xem file `API_CONTRACT.md` để biết API endpoints cần implement.

### 2. Test Backend Connection

Sau khi backend chạy, test endpoints:

```bash
# Test verify table
curl -X POST http://localhost:3000/api/tables/verify \
  -H "Content-Type: application/json" \
  -d '{"tableNumber": 5}'

# Test get session
curl http://localhost:3000/api/sessions/table/5
```

### 3. Update TableInputActivity

Mở `TableInputActivity.java` và uncomment phần API call:

```java
private void verifyTableAndStartSession(int tableNumber) {
    // TODO: Replace this with actual API call
    // Uncomment code dưới khi backend ready
    
    /*
    ApiService.verifyTable(tableNumber, new Callback() {
        @Override
        public void onSuccess(Table table) {
            // Check if session exists
            ApiService.getSession(tableNumber, new Callback() {
                @Override
                public void onSuccess(Session session) {
                    if (session != null) {
                        // Use existing session
                        saveAndNavigate(table, session);
                    } else {
                        // Create new session
                        ApiService.createSession(table.getId(), tableNumber);
                    }
                }
            });
        }
    });
    */
}
```

---

## 🐛 Troubleshooting

### Lỗi: "Unable to resolve dependency"
```bash
# Giải pháp:
File → Invalidate Caches → Invalidate and Restart
```

### Lỗi: "SDK location not found"
```bash
# Tạo file local.properties:
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\sdk
```

### Lỗi: App crash khi connect backend
```bash
# Kiểm tra:
1. AndroidManifest.xml có permission INTERNET
2. android:usesCleartextTraffic="true" (for HTTP)
3. Backend đang chạy
4. URL trong ApiConfig đúng
```

### Emulator không connect được localhost
```bash
# Dùng IP đặc biệt của Android:
http://10.0.2.2:3000  # Thay vì localhost:3000
```

---

## 📱 Screenshots Expected

Khi chạy thành công, bạn sẽ thấy:

```
┌─────────────────────────┐
│  Nhà Hàng Quang Duy     │
│                         │
│      [🖼️ Image]         │
│                         │
│   Xin chào quý khách    │
│  Chúc quý khách ngon    │
│        miệng            │
│                         │
│   ┌─────────────────┐   │
│   │ Nhập số bàn     │   │
│   └─────────────────┘   │
│                         │
│   ┌─────────────────┐   │
│   │   Bắt đầu       │   │
│   └─────────────────┘   │
└─────────────────────────┘
```

---

## 🎯 Next Steps

Sau khi màn hình này chạy OK:

1. ✅ Implement Backend API (xem API_CONTRACT.md)
2. ✅ Test API connection
3. ⏭️ Implement UC-02: Menu Screen
4. ⏭️ Implement UC-03: Cart Screen
5. ⏭️ Implement UC-04: Order Submission
6. ⏭️ Implement UC-05: Order Status Screen

---

## 📞 Support

Nếu gặp vấn đề:
1. Check file README.md
2. Check file API_CONTRACT.md
3. Review code comments
4. Contact team

Happy Coding! 🚀
