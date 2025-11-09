# 🎨 Hướng dẫn thêm ảnh Background cho App

## 📸 Cách thêm ảnh background của bạn

### Bước 1: Chuẩn bị ảnh

Chọn một ảnh đẹp về nhà hàng hoặc món ăn. Khuyến nghị:
- **Kích thước:** 1080 x 1920 pixels (hoặc tương đương tỷ lệ 9:16)
- **Định dạng:** JPG hoặc PNG
- **Dung lượng:** < 500KB để tối ưu hiệu suất
- **Nội dung:** Ảnh nền nhà hàng, bàn ăn, hoặc món ăn đẹp

### Bước 2: Đặt tên file

Đổi tên file ảnh của bạn thành:
```
bg_restaurant.jpg
```
hoặc
```
bg_restaurant.png
```

### Bước 3: Copy ảnh vào project

#### Option A: Sử dụng Android Studio
1. Trong Android Studio, chọn **Project view** (góc trái trên)
2. Navigate đến: `app/src/main/res/drawable/`
3. Right-click vào folder `drawable`
4. Chọn **Show in Explorer** (Windows) hoặc **Reveal in Finder** (Mac)
5. Copy file `bg_restaurant.jpg` vào folder này

#### Option B: Copy trực tiếp
1. Mở Windows Explorer
2. Navigate đến: 
   ```
   D:\Semester_8\PRM392\TapToEat\TapToEat_FE\app\src\main\res\drawable\
   ```
3. Paste file `bg_restaurant.jpg` vào đây

### Bước 4: Cập nhật layout

Mở file: `activity_table_input.xml`

Tìm dòng:
```xml
<ImageView
    android:id="@+id/ivBackground"
    ...
    android:src="@drawable/bg_gradient_green"
```

Thay đổi thành:
```xml
<ImageView
    android:id="@+id/ivBackground"
    ...
    android:src="@drawable/bg_restaurant"
```

### Bước 5: Sync và Run

1. Trong Android Studio: **File → Sync Project with Gradle Files**
2. Run app (Shift + F10)

---

## 🎨 Tùy chỉnh Overlay (làm tối/sáng ảnh nền)

Nếu ảnh nền quá sáng hoặc quá tối, bạn có thể điều chỉnh overlay:

Trong file `activity_table_input.xml`, tìm:

```xml
<View
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_overlay"
    android:alpha="0.3" />
```

**Điều chỉnh độ tối:**
- `android:alpha="0.0"` → Không có overlay (ảnh gốc)
- `android:alpha="0.3"` → Hơi tối (mặc định)
- `android:alpha="0.5"` → Tối vừa
- `android:alpha="0.7"` → Rất tối

**Thay đổi màu overlay:**

Mở file `colors.xml` và chỉnh:
```xml
<!-- Overlay đen -->
<color name="background_overlay">#80000000</color>

<!-- Overlay xanh lá -->
<color name="background_overlay">#801B5E20</color>

<!-- Overlay trắng (làm sáng) -->
<color name="background_overlay">#80FFFFFF</color>
```

---

## 🖼️ Gợi ý ảnh nền đẹp

### Nơi tìm ảnh miễn phí:
1. **Unsplash** - https://unsplash.com/
   - Tìm: "restaurant", "dining table", "food"
   
2. **Pexels** - https://www.pexels.com/
   - Tìm: "restaurant interior", "elegant dining"

3. **Pixabay** - https://pixabay.com/
   - Tìm: "restaurant", "cuisine"

### Phong cách phù hợp với theme xanh lá:
- ✅ Bàn ăn gỗ tự nhiên
- ✅ Không gian nhà hàng với cây xanh
- ✅ Món ăn tươi ngon trên nền sáng
- ✅ Không gian tối giản, sang trọng
- ❌ Tránh ảnh quá rối, quá nhiều chi tiết
- ❌ Tránh ảnh có chữ hoặc logo

---

## 🎯 Nếu không muốn dùng ảnh

Bạn có thể dùng gradient xanh lá đẹp mắt (đã có sẵn):

```xml
android:src="@drawable/bg_gradient_green"
```

Hoặc tạo gradient mới với màu xanh đậm hơn/nhạt hơn bằng cách chỉnh file `bg_gradient_green.xml`

---

## 📱 Tối ưu ảnh cho App

Nếu ảnh quá nặng, bạn có thể giảm size bằng các công cụ online:

1. **TinyPNG** - https://tinypng.com/
2. **Squoosh** - https://squoosh.app/
3. **CompressJPEG** - https://compressjpeg.com/

**Target:** 
- Size: < 500KB
- Resolution: 1080 x 1920 px (hoặc nhỏ hơn)

---

## ✨ Kết quả

Sau khi thêm ảnh, màn hình sẽ có:
- ✅ Ảnh nền đẹp của nhà hàng bạn
- ✅ Overlay làm tối nhẹ để text dễ đọc
- ✅ Card trắng nổi bật trên nền
- ✅ Theme màu xanh lá chuyên nghiệp
- ✅ Icons và decoration hài hòa

---

## 🆘 Troubleshooting

### Lỗi: "Cannot resolve symbol bg_restaurant"
**Giải pháp:**
1. Kiểm tra tên file chính xác: `bg_restaurant.jpg`
2. Đặt đúng folder: `app/src/main/res/drawable/`
3. Sync Gradle: File → Sync Project with Gradle Files
4. Clean project: Build → Clean Project
5. Rebuild: Build → Rebuild Project

### Ảnh bị méo, không vừa màn hình
**Giải pháp:**
```xml
android:scaleType="centerCrop"  <!-- Crop để phủ kín -->
android:scaleType="fitXY"       <!-- Stretch để vừa -->
android:scaleType="center"      <!-- Giữ nguyên size -->
```

### Ảnh quá sáng, text không đọc được
**Giải pháp:** Tăng alpha của overlay:
```xml
android:alpha="0.5"   <!-- Tối hơn -->
```

---

## 🎨 Demo Images Locations

Các vị trí có thể đặt ảnh custom:

1. **Background:** `drawable/bg_restaurant.jpg` ← **ĐÂY LÀ CHÍNH**
2. Logo: `drawable/ic_restaurant_logo.png` (optional)
3. Welcome icon: Đang dùng vector (có thể thay bằng PNG)

---

**Happy Customizing! 🎨**
