# 💰 Finsight — Smart Expense Tracker

![Android](https://img.shields.io/badge/Platform-Android-teal?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange)
![License](https://img.shields.io/badge/License-MIT-green)

> A production-grade personal finance Android app built for young Indian working professionals. Track expenses, set budgets, and take control of your money — all offline, all private.

---

## 📱 Screenshots

| Onboarding | Home | Add Transaction |
|---|---|---|
| ![Onboarding](screenshots/onboarding.png) | ![Home](screenshots/home.png) | ![Add](screenshots/add_transaction.png) |

| History | Budget | Settings |
|---|---|---|
| ![History](screenshots/history.png) | ![Budget](screenshots/budget.png) | ![Settings](screenshots/settings.png) |

---

## ✨ Features

- 🎯 **Smart Onboarding** — 3-step setup with name, salary, and currency preference
- 💳 **Transaction Tracking** — Add income and expenses with categories, notes, and dates
- 📊 **Budget Management** — Set category-wise budgets with real-time progress tracking
- 🔍 **Transaction History** — Search, filter by type, and browse grouped by date
- ⚡ **Reactive UI** — Every screen updates automatically when data changes — no manual refresh
- 🔒 **100% Offline** — All data stored locally using Room. No account needed, no data shared
- 👤 **Profile Settings** — Edit name, salary, and view app info

---

## 🏗️ Architecture

Finsight follows **Clean Architecture + MVVM** with a reactive data layer:

```
app/
├── data/
│   ├── local/
│   │   ├── entity/          # Room entities (Transaction, Budget, Category)
│   │   ├── dao/             # DAOs with Flow-based queries
│   │   └── database/        # AppDatabase
│   └── repository/          # Repository implementations
├── domain/
│   ├── model/               # Domain models
│   └── usecase/             # Business logic use cases
├── presentation/
│   ├── onboarding/          # Onboarding flow
│   ├── home/                # Home screen + ViewModel
│   ├── addtransaction/      # Add transaction screen + ViewModel
│   ├── history/             # History screen + ViewModel
│   ├── budget/              # Budget screen + ViewModel
│   └── settings/            # Settings screen + ViewModel
├── di/                      # Hilt modules + UserPreferences
└── ui/theme/                # Colors, Typography, Theme
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose, Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (KSP) |
| **Database** | Room |
| **Async** | Coroutines + StateFlow |
| **Navigation** | Compose Navigation |
| **Preferences** | DataStore |
| **Build** | Gradle Version Catalogs |

---

## 🔄 Data Flow

```
UI (Composable)
    ↕ StateFlow / collectAsState()
ViewModel
    ↕ Flow
Repository
    ↕ Flow / suspend fun
Room DAO
    ↕
SQLite Database
```

Every database change automatically propagates up to the UI through Kotlin Flow — no manual refresh, no polling.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+
- Kotlin 2.1.21

### Clone and Run

```bash
git clone https://github.com/vedant2307/Finsight.git
cd Finsight
```

1. Open in **Android Studio**
2. Let Gradle sync complete
3. Run on emulator or physical device (API 26+)

---

## 📦 Key Dependencies

```toml
[versions]
kotlin = "2.1.21"
ksp = "2.1.21-2.0.1"
composeBom = "2026.02.00"
hilt = "2.55"
room = "2.6.1"
coroutines = "1.10.2"
datastore = "1.1.2"
```

---

## 🧠 What I Learned

- Implementing **Clean Architecture** in a real Android project — separating data, domain, and presentation layers
- Using **Kotlin Flow + combine()** to reactively merge multiple data streams (e.g. budget + spending across categories)
- Managing **DataStore** for lightweight user preferences alongside **Room** for structured data
- Building **Unidirectional Data Flow (UDF)** with StateFlow and immutable UiState data classes
- Handling **edge cases** in date filtering, amount input, and reactive budget progress tracking

---

## 👨‍💻 Author

**Vedant Malpani**
Android Developer | Kotlin | Jetpack Compose | Fiserv (Clover Division)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin)](https://linkedin.com/in/vedantmalpani-4b18b3231)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?logo=github)](https://github.com/vedant2307)

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

<p align="center">Built with ❤️ using Kotlin & Jetpack Compose</p>
