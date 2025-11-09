# 🚀 Quick Start - Giao Diện Mới

## ✨ Những gì đã thay đổi

### 🎨 Màu sắc
- ❌ Cũ: Xanh dương (#2196F3)
- ✅ Mới: **Xanh lá cây đậm (#1B5E20) + Trắng**

### 🖼️ Background
- ❌ Cũ: Màu xám nhạt đơn giản
- ✅ Mới: **Hỗ trợ ảnh background + Overlay**

### 🎯 Icons
- ❌ Cũ: Placeholder đơn giản
- ✅ Mới: **Material Icons chuyên nghiệp**
  - 🍴 Restaurant icon
  - 🪑 Table icon
  - 🍃 Leaf decoration

### 📦 Components
- ❌ Cũ: Layout đơn giản
- ✅ Mới: **Professional CardView design**
  - Welcome card với shadow
  - Rounded corners (24dp)
  - Elevation effects
  - Better spacing

---

## 📁 Files mới tạo

### 🎨 Drawables (Icons & Backgrounds):
```
✅ bg_gradient_green.xml          - Gradient xanh default
✅ bg_restaurant_default.xml      - Background với pattern
✅ btn_primary_green.xml          - Button style mới
✅ bg_input_field.xml             - Input field style
✅ bg_card_welcome.xml            - Card style
✅ ic_restaurant.xml              - Icon nhà hàng 🍴
✅ ic_table.xml                   - Icon bàn ăn 🪑
✅ ic_leaf_accent.xml             - Icon lá cây 🍃
✅ pattern_dots.xml                - Pattern trang trí
✅ pattern_dots_base.xml          - Base pattern
```

### 📝 Documentation:
```
✅ BACKGROUND_GUIDE.md            - Hướng dẫn thêm ảnh
✅ DESIGN_SYSTEM.md               - Chi tiết design system
```

### 🎨 Updated Files:
```
✅ colors.xml                      - Theme màu xanh lá
✅ strings.xml                     - Thêm strings mới
✅ activity_table_input.xml       - Layout hoàn toàn mới
✅ build.gradle.kts               - Thêm CardView dependency
```

---

## 🎯 Chạy ngay bây giờ

### Option 1: Dùng gradient mặc định (RECOMMENDED để test)

App đã sẵn sàng! Chỉ cần:

```bash
# 1. Sync Gradle
File → Sync Project with Gradle Files

# 2. Run app
Shift + F10
```

**Kết quả:** Màn hình với gradient xanh lá đẹp mắt ✅

---

### Option 2: Thêm ảnh của bạn (PROFESSIONAL)

#### Bước 1: Chuẩn bị ảnh
- Chọn ảnh đẹp về nhà hàng/món ăn
- Đổi tên thành: `bg_restaurant.jpg`
- Kích thước: 1080x1920 px
- Dung lượng: < 500KB

#### Bước 2: Copy ảnh vào project
```
D:\Semester_8\PRM392\TapToEat\TapToEat_FE\app\src\main\res\drawable\
```

#### Bước 3: Update layout
Mở: `activity_table_input.xml`

Tìm dòng:
```xml
android:src="@drawable/bg_gradient_green"
```

Đổi thành:
```xml
android:src="@drawable/bg_restaurant"
```

#### Bước 4: Sync & Run
```bash
File → Sync Project with Gradle Files
Shift + F10
```

**Kết quả:** Màn hình với ảnh của bạn! 🎨

> 💡 **Chi tiết:** Xem file `BACKGROUND_GUIDE.md`

---

## 🎨 Preview Giao Diện Mới

### Structure:
```
┌─────────────────────────────────┐
│  [Ảnh nền của bạn hoặc gradient]│
│  [Overlay 30% tối]              │
│                                 │
│      🍴                         │  ← Logo icon
│   Nhà Hàng Quang Duy           │  ← Tên (trắng, bold)
│                                 │
│  ┌─────────────────────────┐   │
│  │    [White Card]         │   │
│  │                         │   │
│  │     🪑                  │   │  ← Table icon lớn
│  │  Xin chào quý khách    │   │  ← Cursive text
│  │ Chúc quý khách ngon    │   │
│  │       miệng            │   │
│  │  ─────────────         │   │  ← Divider
│  │                         │   │
│  │ 🪑 [Nhập số bàn....]   │   │  ← Input với icon
│  │                         │   │
│  │ 🍴 [Bắt đầu]           │   │  ← Button xanh đậm
│  │                         │   │
│  └─────────────────────────┘   │
│                                 │
│  🍃 TapToEat - Quick Ordering  │  ← Footer
└─────────────────────────────────┘
```

---

## 🎨 Điều chỉnh độ tối của ảnh nền

Nếu ảnh quá sáng/tối, chỉnh overlay:

**File:** `activity_table_input.xml`

```xml
<View
    android:alpha="0.3" />  ← Đổi giá trị này
```

| Value | Effect |
|-------|--------|
| 0.0 | Không tối (ảnh gốc) |
| 0.3 | Tối nhẹ (mặc định) ✅ |
| 0.5 | Tối vừa |
| 0.7 | Rất tối |

---

## 🎯 Color Theme

### Primary Colors:
- **Deep Green:** #1B5E20 (Buttons, text)
- **White:** #FFFFFF (Cards, text on green)
- **Light Green:** #4CAF50 (Icons, borders)

### Sử dụng:
```
Buttons:          Deep Green (#1B5E20)
Text on Buttons:  White
Cards:            White
Main Text:        Deep Green
Icons:            Medium/Light Green
Background:       Image hoặc Gradient
```

---

## 📊 So sánh Trước/Sau

### Trước (Blue theme):
- Background: Xám nhạt
- Primary: Xanh dương
- Icons: Placeholder đơn giản
- Layout: Flat, không card

### Sau (Green theme):
- ✅ Background: Ảnh/Gradient
- ✅ Primary: Xanh lá cây chuyên nghiệp
- ✅ Icons: Material Design icons
- ✅ Layout: Card với elevation, shadows
- ✅ Spacing: Cải thiện hierarchy
- ✅ Typography: Better sizes & weights

---

## 🚀 Next Steps

### Immediate:
1. ✅ Run app với gradient mặc định
2. ⏳ Thêm ảnh background của bạn
3. ⏳ Test trên device/emulator

### Customize:
1. 📸 Thay ảnh nền → `BACKGROUND_GUIDE.md`
2. 🎨 Hiểu design system → `DESIGN_SYSTEM.md`
3. 🔧 Chỉnh overlay opacity
4. 📝 Đổi tên nhà hàng trong `strings.xml`

### Future Screens:
- Menu screen (UC-02)
- Cart screen (UC-03)
- Order tracking (UC-05)

---

## 🆘 Troubleshooting

### Lỗi build after sync?
```bash
Build → Clean Project
Build → Rebuild Project
```

### CardView không hiển thị?
Kiểm tra dependency:
```kotlin
implementation("androidx.cardview:cardview:1.0.0")
```

### Icons không hiển thị?
- Sync Gradle
- Clean project
- Check file names (lowercase, no spaces)

### Layout bị lỗi?
- Check XML tags đóng đúng
- Check all `android:id` unique
- Rebuild project

---

## 📱 Test Checklist

- [ ] App build thành công
- [ ] Gradient/ảnh nền hiển thị đẹp
- [ ] Card trắng nổi bật
- [ ] Icons hiển thị đúng màu
- [ ] Input field có border xanh
- [ ] Button xanh đậm, text trắng
- [ ] Nhập số bàn thành công
- [ ] Error message hiển thị đúng

---

## 🎉 Kết quả

Bạn đã có một màn hình:
- ✅ **Chuyên nghiệp** với CardView & elevation
- ✅ **Đẹp mắt** với theme xanh lá tự nhiên
- ✅ **Linh hoạt** có thể thêm ảnh của bạn
- ✅ **Hiện đại** với Material Design icons
- ✅ **Dễ đọc** với hierarchy rõ ràng

---

**Happy Coding! 🌿**

Nếu cần hỗ trợ:
- Chi tiết design → `DESIGN_SYSTEM.md`
- Thêm ảnh → `BACKGROUND_GUIDE.md`
- Setup project → `SETUP_GUIDE.md`
