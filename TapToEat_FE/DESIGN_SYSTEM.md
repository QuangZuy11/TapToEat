# 🎨 Design System - TapToEat Green Theme

## 🌿 Color Palette

### Primary Colors (Xanh lá cây đậm + Trắng)

```
Primary Deep Green:    #1B5E20  ████████
Primary Dark:          #0D3D13  ████████
Primary Medium:        #2E7D32  ████████
Primary Light:         #4CAF50  ████████

White:                 #FFFFFF  ████████
Background Light:      #F1F8E9  ████████

Accent Orange:         #FF6F00  ████████
Accent Light:          #FFA726  ████████
```

### Usage Guide

| Element | Color | Usage |
|---------|-------|-------|
| **Primary Button** | #1B5E20 | Bắt đầu, Submit, Action buttons |
| **Text on Button** | #FFFFFF | Text trên button xanh |
| **Card Background** | #FFFFFF | Background của cards |
| **Main Text** | #1B5E20 | Restaurant name, headings |
| **Secondary Text** | #558B2F | Subtitles, descriptions |
| **Icons** | #2E7D32 | Icons chính |
| **Background** | Image/Gradient | Ảnh nền hoặc gradient xanh |

---

## 🎯 Material Icons Used

### Current Icons in Project:

1. **ic_restaurant.xml** 🍴
   - Usage: Logo, Button icon
   - Color: Primary green
   - Size: 48dp (logo), 24dp (button)

2. **ic_table.xml** 🪑
   - Usage: Table number input icon
   - Color: Primary variant
   - Size: 24dp

3. **ic_leaf_accent.xml** 🍃
   - Usage: Decoration, branding
   - Color: Primary light
   - Size: 20dp - 40dp

---

## 📐 Layout Structure

### Screen Hierarchy:

```
┌─────────────────────────────────────┐
│ FrameLayout (Root)                  │
│ ├── ImageView (Background Image)    │ ← ẢNH CỦA BẠN Ở ĐÂY
│ ├── View (Overlay - 30% opacity)    │
│ └── ScrollView                       │
│     └── LinearLayout                 │
│         ├── Space (flexible)         │
│         ├── Restaurant Logo (80dp)   │ ← Icon nhà hàng
│         ├── Restaurant Name (32sp)   │ ← Tên nhà hàng
│         ├── CardView (Welcome Card)  │
│         │   ├── Welcome Icon         │
│         │   ├── "Xin chào..."        │
│         │   ├── Subtitle             │
│         │   ├── Divider             │
│         │   ├── Input Field          │
│         │   ├── Button               │
│         │   └── Error Message        │
│         ├── Space (flexible)         │
│         └── Footer                   │
└─────────────────────────────────────┘
```

---

## 📏 Dimensions & Spacing

### Text Sizes:
```
Restaurant Name:    32sp (Bold, White)
Welcome Text:       28sp (Cursive, Green)
Subtitle:           16sp (Regular, Green)
Button Text:        18sp (Bold, White)
Input Text:         20sp (Bold, Green)
Footer:             12sp (Regular, White)
```

### Spacing:
```
Screen Padding:     24dp
Card Padding:       32dp
Icon Size (Large):  80dp - 100dp
Icon Size (Medium): 40dp - 48dp
Icon Size (Small):  20dp - 24dp
Button Height:      64dp
Input Height:       56dp (auto)
Card Radius:        24dp
Button Radius:      12dp
Elevation:          8dp - 12dp
```

---

## 🎨 Components

### 1. Background
```xml
<!-- Option 1: Ảnh của bạn -->
android:src="@drawable/bg_restaurant"
android:scaleType="centerCrop"

<!-- Option 2: Gradient mặc định -->
android:src="@drawable/bg_gradient_green"
```

### 2. Welcome Card
```xml
CardView:
- Background: White
- Corner Radius: 24dp
- Elevation: 12dp
- Padding: 32dp
```

### 3. Primary Button
```xml
Button:
- Background: @drawable/btn_primary_green
- Text Color: White
- Height: 64dp
- Radius: 12dp
- Elevation: 8dp
- Icon: Restaurant icon (left)
```

### 4. Input Field
```xml
TextInputLayout:
- Style: Outlined Box
- Stroke Color: Primary green
- Stroke Width: 2dp
- Radius: 12dp
- Start Icon: Table icon
```

---

## 🖼️ Image Requirements

### Background Image (bg_restaurant)

**Required:**
- Location: `app/src/main/res/drawable/bg_restaurant.jpg`
- Format: JPG or PNG
- Size: < 500KB
- Resolution: 1080 x 1920 px (9:16 ratio)

**Recommended:**
- Bright but not overwhelming
- Restaurant interior or food
- Natural green tones preferred
- Not too busy/cluttered
- Good contrast for white text

**Examples:**
- ✅ Restaurant dining area
- ✅ Wooden table with food
- ✅ Green plants in restaurant
- ✅ Modern dining space
- ❌ Too dark images
- ❌ Images with text
- ❌ Too busy patterns

### Logo (Optional)
- Location: `drawable/ic_restaurant_logo.png`
- Format: PNG with transparency
- Size: 512 x 512 px
- Use: Replace ic_restaurant.xml

---

## 🎯 Visual Hierarchy

### Level 1 - Highest Priority:
1. **Primary Button** (Bắt đầu)
   - Largest touch target (64dp)
   - Highest contrast (Green on White card)
   - Icon + Text

### Level 2 - Important:
2. **Input Field** (Nhập số bàn)
   - Clear label with icon
   - Bold stroke
   - Large text size

### Level 3 - Context:
3. **Welcome Messages**
   - Cursive friendly text
   - Secondary green color
   - Center aligned

### Level 4 - Branding:
4. **Restaurant Name & Logo**
   - White text with shadow
   - Top of screen
   - Large bold font

---

## 🌟 Design Principles

### 1. Natural & Fresh
- Deep green = Natural, healthy, appetite
- White = Clean, professional
- Gradients = Depth, modern

### 2. Professional
- Consistent spacing (8dp grid)
- Proper hierarchy
- Card elevation
- Clear typography

### 3. User-Friendly
- Large touch targets (min 48dp)
- High contrast
- Clear error states
- Loading indicators

### 4. Brand Identity
- Consistent green theme
- Restaurant/food icons
- Vietnamese language
- Friendly tone

---

## 📱 Responsive Design

### Adapts to:
- Different screen sizes (wrap_content, match_parent)
- Scrollable content (ScrollView)
- Keyboard appearance (adjustResize)
- Portrait orientation (locked)

### Flexible Elements:
- Space weights for centering
- Card fills width with margin
- Text wraps properly
- Images scale correctly

---

## 🎨 Theme Customization

### Để thay đổi màu chủ đạo:

**File:** `values/colors.xml`

```xml
<!-- Thay xanh lá bằng màu khác -->
<color name="primary">#YOUR_COLOR</color>
<color name="primary_dark">#DARKER_VERSION</color>
<color name="primary_light">#LIGHTER_VERSION</color>
```

**Gợi ý màu khác:**
- Red theme: #C62828 (Sang trọng)
- Blue theme: #1565C0 (Tin cậy)
- Orange theme: #E65100 (Năng động)
- Brown theme: #4E342E (Ấm cúng)

---

## ✨ Animation Ideas (Future)

Có thể thêm sau:
- Fade in animation cho card
- Button ripple effect (đã có)
- Input focus animation
- Error shake animation
- Loading spinner
- Page transitions

---

## 📊 Accessibility

### Current Support:
- ✅ Content descriptions for images
- ✅ High contrast ratios
- ✅ Large touch targets (64dp button)
- ✅ Clear error messages
- ✅ Readable font sizes (16sp+)

### Future Improvements:
- [ ] Screen reader optimization
- [ ] Dark mode support
- [ ] Font scaling support
- [ ] Reduced motion option

---

## 🎨 Design Tools Used

### Created with:
- Material Design guidelines
- Vector drawables (scalable)
- XML layouts (flexible)
- Resource management (organized)

### Color Inspiration:
- Nature (green leaves)
- Fresh vegetables
- Healthy dining
- Eco-friendly concepts

---

## 📝 Design Checklist

Before launch:
- [x] Color palette defined
- [x] Icons created
- [x] Layouts responsive
- [x] Typography set
- [x] Spacing consistent
- [ ] Background image added (BY USER)
- [x] Error states designed
- [x] Loading states handled
- [ ] Animations added (optional)
- [ ] Dark mode (future)

---

**Design Status:** ✅ READY FOR CUSTOMIZATION

**Next:** Add your restaurant background image!

See: `BACKGROUND_GUIDE.md` for instructions.
