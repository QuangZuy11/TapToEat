# TapToEat Project Structure

```
TapToEat_FE/
│
├── 📱 app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   │
│   └── src/
│       ├── androidTest/
│       │   └── java/vn/edu/fpt/taptoeat/
│       │       └── ExampleInstrumentedTest.java
│       │
│       ├── test/
│       │   └── java/vn/edu/fpt/taptoeat/
│       │       └── ExampleUnitTest.java
│       │
│       └── main/
│           ├── 📄 AndroidManifest.xml
│           │
│           ├── ☕ java/vn/edu/fpt/taptoeat/
│           │   ├── MainActivity.java                    [Router/Splash]
│           │   ├── TableInputActivity.java              [UC-01 Main Screen]
│           │   │
│           │   ├── 📦 api/
│           │   │   └── ApiConfig.java                   [API Configuration]
│           │   │
│           │   ├── 📦 models/
│           │   │   ├── Table.java                       [Table Entity]
│           │   │   └── Session.java                     [Session Entity]
│           │   │
│           │   └── 📦 utils/
│           │       └── SessionManager.java              [Local Session]
│           │
│           └── 🎨 res/
│               ├── drawable/
│               │   ├── btn_primary.xml                  [Button Style]
│               │   └── ic_welcome_placeholder.xml       [Welcome Icon]
│               │
│               ├── layout/
│               │   ├── activity_main.xml
│               │   └── activity_table_input.xml         [Main Layout]
│               │
│               ├── values/
│               │   ├── colors.xml                       [Color Palette]
│               │   ├── strings.xml                      [Text Resources]
│               │   └── themes.xml
│               │
│               ├── values-night/
│               │   └── themes.xml
│               │
│               └── mipmap-*/
│                   └── ic_launcher*                     [App Icons]
│
├── 🔧 gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── 📚 Documentation/
│   ├── README.md                                        [Project Overview]
│   ├── API_CONTRACT.md                                  [Backend API Specs]
│   ├── SETUP_GUIDE.md                                   [Setup Instructions]
│   └── SUMMARY.md                                       [Implementation Summary]
│
├── build.gradle.kts                                     [Root Build Script]
├── settings.gradle.kts                                  [Project Settings]
├── gradle.properties                                    [Gradle Config]
├── local.properties                                     [Local SDK Path]
├── gradlew                                              [Gradle Wrapper Unix]
├── gradlew.bat                                          [Gradle Wrapper Win]
└── .gitignore

```

## 📊 Statistics

### Java Files
```
✅ Activities:       2 files
✅ Models:           2 files  
✅ Utils:            1 file
✅ API:              1 file
━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 Total:            6 files
```

### Resources
```
✅ Layouts:          2 files
✅ Drawables:        2 files
✅ Values:           2 files
✅ Documentation:    4 files
━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 Total:           10 files
```

### Dependencies
```
📦 AndroidX AppCompat
📦 Material Components
📦 ConstraintLayout
```

## 🎯 Features Status

### ✅ Completed
- [x] Màn hình nhập số bàn (UC-01)
- [x] Input validation
- [x] Session management
- [x] API configuration
- [x] Documentation

### ⏭️ Todo
- [ ] Menu screen (UC-02)
- [ ] Cart screen (UC-03)
- [ ] Order submission (UC-04)
- [ ] Order status (UC-05)
- [ ] Chef dashboard (UC-06-09)

## 🚀 Quick Start

```bash
# 1. Open in Android Studio
# 2. Wait for Gradle sync
# 3. Run app
# 4. Test with table number input
```

## 📞 Contact

For questions or issues, check:
- README.md for overview
- SETUP_GUIDE.md for detailed instructions
- API_CONTRACT.md for backend integration
