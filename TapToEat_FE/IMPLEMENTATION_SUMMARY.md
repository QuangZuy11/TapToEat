# TapToEat - Implementation Summary

## ✅ Hoàn Thành

### UC-01: Table Input & Session Creation (Đã integrate API)

**Files:**
- `TableInputActivity.java` - API integration hoàn chỉnh
- `activity_table_input.xml` - UI với deep green theme

**Chức năng:**
1. ✅ User nhập số bàn
2. ✅ Gọi API `GET /api/tables/:tableNumber` để verify bàn tồn tại
3. ✅ Kiểm tra status bàn (chỉ accept "available")
4. ✅ Gọi API `POST /api/sessions` với body `{tableNumber: X}` để tạo session
5. ✅ Nhận sessionId từ response
6. ✅ Navigate đến MenuActivity với tableNumber + sessionId
7. ✅ Error handling cho tất cả trường hợp

**API Endpoints:**
- `GET http://10.0.2.2:9999/api/tables/:tableNumber`
- `POST http://10.0.2.2:9999/api/sessions`

---

### UC-02: Menu Browsing (Đã integrate API)

**Files:**
- `MenuActivity.java` - Main activity với TabLayout + ViewPager2
- `MenuPagerAdapter.java` - Adapter cho ViewPager2
- `MenuItemsFragment.java` - Fragment hiển thị menu items theo category
- `MenuItemAdapter.java` - RecyclerView adapter với Glide image loading
- `activity_menu.xml` - CoordinatorLayout với collapsing toolbar
- `fragment_menu_items.xml` - RecyclerView grid layout
- `item_menu_card.xml` - MaterialCardView cho từng món ăn

**Chức năng:**
1. ✅ Hiển thị table number trên toolbar
2. ✅ Gọi API `GET /api/categories` để load danh sách categories
3. ✅ Filter chỉ hiển thị active categories
4. ✅ Tabs scrollable theo displayOrder
5. ✅ Mỗi tab load menu items qua API `GET /api/categories/:id/items`
6. ✅ Grid layout 2 cột cho menu items
7. ✅ Load ảnh món ăn qua Glide (placeholder: ic_restaurant)
8. ✅ Hiển thị: tên, mô tả, giá (format VND), thời gian chuẩn bị
9. ✅ Badge "Phổ Biến" cho items có tag "popular" (màu cam)
10. ✅ Overlay mờ + "Tạm hết" cho items không available
11. ✅ Filter chỉ hiển thị available items
12. ✅ Error handling với Toast messages

**API Endpoints:**
- `GET http://10.0.2.2:9999/api/categories`
- `GET http://10.0.2.2:9999/api/categories/:categoryId/items`

---

### Design System

**Theme Colors:**
```xml
<color name="primary">#1B5E20</color>          <!-- Deep Green -->
<color name="primary_dark">#0D3D11</color>     <!-- Darker Green -->
<color name="primary_light">#4CAF50</color>    <!-- Light Green -->
<color name="white">#FFFFFF</color>
<color name="text_primary">#212121</color>
<color name="text_secondary">#757575</color>
<color name="accent">#FF6F00</color>           <!-- Orange for popular tag -->
```

**Material Design:**
- Material Components 1.13.0
- Material Icons (restaurant, table_bar, eco)
- MaterialButton, MaterialCardView
- TextInputLayout (FilledBox style)
- CoordinatorLayout với AppBarLayout

**Layout Pattern:**
- Table Input: FrameLayout với background image overlay
- Menu: CoordinatorLayout + Toolbar + TabLayout + ViewPager2
- Menu Items: RecyclerView với GridLayoutManager (2 columns, 16dp spacing)
- Cards: 12dp radius, 1dp green stroke, 4dp elevation

---

### Dependencies Đã Thêm

```gradle
// ViewPager2 for tabs
implementation("androidx.viewpager2:viewpager2:1.1.0")

// Retrofit for networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.google.code.gson:gson:2.10.1")

// Glide for image loading
implementation("com.github.bumptech.glide:glide:4.16.0")
annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
```

---

### API Configuration

**Base URL:**
```java
http://10.0.2.2:9999/api/
```
- `10.0.2.2` = localhost trong Android Emulator
- Backend phải chạy trên port 9999

**API Service:**
- `RetrofitClient.java` - Singleton với Gson converter
- `ApiService.java` - Interface với @GET/@POST annotations
- Response wrapper: `ApiResponse<T>` với success, data, message

**Models:**
- `Category.java` - _id, name, description, displayOrder, isActive
- `MenuItem.java` - _id, name, description, price, image, categoryId, preparationTime, isAvailable, tags

---

### AndroidManifest

```xml
<!-- Permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Cleartext traffic for localhost -->
android:usesCleartextTraffic="true"

<!-- Activities -->
<activity android:name=".TableInputActivity" />
<activity android:name=".MenuActivity" />
```

---

## 🔄 Flow Hoàn Chỉnh

1. **User mở app** → MainActivity (splash/router)
2. **Navigate to TableInputActivity**
3. **User nhập số bàn** (ví dụ: 5)
4. **Tap "Bắt đầu"**
5. **API verify table** → GET /api/tables/5
   - Nếu không tồn tại: Error "Bàn không tồn tại"
   - Nếu status != "available": Error "Bàn đang được sử dụng"
6. **API create session** → POST /api/sessions {tableNumber: 5}
   - Nhận sessionId
7. **Navigate to MenuActivity** với tableNumber=5, sessionId=xxx
8. **Load categories** → GET /api/categories
   - Hiển thị tabs (Món Chính, Món Phụ, Đồ Uống, Tráng Miệng)
9. **User tap tab "Món Chính"**
10. **Load menu items** → GET /api/categories/:id/items
    - Hiển thị grid 2 cột
    - Show ảnh (Glide), tên, giá, prep time, tags
11. **User có thể browse các category khác nhau**

---

## 📝 Notes

### Image Loading
- Glide tự động cache ảnh
- Placeholder: `R.drawable.ic_restaurant` (Material icon)
- Error fallback: cùng placeholder
- centerCrop để fit ImageView

### Error Handling
- Network errors → Toast với thông báo rõ ràng
- Table không tồn tại → "Bàn không tồn tại"
- Table đang dùng → "Bàn X đang được sử dụng"
- Server errors → "Lỗi kết nối server"

### Performance
- RecyclerView với ViewHolder pattern
- Glide cache ảnh
- ViewPager2 lazy load fragments
- GridLayoutManager spanCount=2

### Backend Requirements
- Server phải chạy trên port 9999
- CORS enabled cho Android app
- Response format:
```json
{
  "success": true,
  "data": [...],
  "message": "optional"
}
```

---

## 🚀 Next Steps (Chưa làm)

### UC-03: Add to Cart (TODO)
- Floating cart button
- Item detail dialog
- Quantity picker
- Add to cart API

### UC-04: View Cart & Order (TODO)
- Cart screen
- Order summary
- Submit order API
- Order confirmation

### UC-05: Order Status (TODO)
- Real-time order tracking
- WebSocket or polling
- Status updates

---

## 🛠️ Build & Run

```bash
# Build debug APK
.\gradlew assembleDebug

# Install to emulator
.\gradlew installDebug

# Run app
adb shell am start -n vn.edu.fpt.taptoeat/.MainActivity
```

**APK Location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## ✨ Design Highlights

- **Deep Green Theme** - Professional, natural, appetite-friendly
- **Background Images** - Customizable restaurant ambiance
- **Material Design 3** - Modern, familiar UX
- **Grid Layout** - Efficient space usage, easy browsing
- **Visual Feedback** - Popular badges, unavailable overlays
- **Smooth Navigation** - Tabs, swipe gestures
- **Error Resilience** - Clear messages, graceful degradation

---

**Build Status:** ✅ BUILD SUCCESSFUL
**Last Build:** Nov 9, 2025
**Gradle Version:** 8.0
**Min SDK:** 27 (Android 8.1)
**Target SDK:** 34 (Android 14)
