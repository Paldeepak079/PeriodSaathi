# Period Saathi 🌸

Your empathetic Android period companion — built with Kotlin, Jetpack Compose, and Clean Architecture.

> **saathi** (साथी) — *companion, friend* (Hindi)
![Uploading image.png…]()

## Architecture

```
ui/        → ViewModels + Compose Screens
domain/    → UseCases + Domain Models
data/      → Repositories → Room DAOs → SQLite
di/        → Hilt Modules
```

- **Pattern**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room (local-only, no network)
- **State**: StateFlow + `collectAsStateWithLifecycle`
- **Navigation**: Compose Navigation with type-safe routes
- **Animations**: `spring()` physics everywhere

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2026.02.01) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.51.1 |
| Database | Room 2.7.0 |
| Navigation | Compose Navigation 2.8.5 |
| State | StateFlow + DataStore |
| Graphics | Canvas, Lottie 6.4.0, Coil 2.7.0 |

## Design System

- **Primary**: `#FFB5C8` (Blush Pink)
- **Secondary**: `#C9B8FF` (Lavender)
- **Tertiary**: `#B8DCFF` (Baby Blue)
- **Background**: `#FFF8F5` (Cream)
- **Cards**: White at 0.45 alpha, 28dp rounded
- **Buttons**: Pill shape (50dp), gradient backgrounds
- **Animations**: Spring physics on all interactions

## Screens

| Screen | Status |
|--------|--------|
| Splash | ✅ Done |
| Onboarding | ✅ Done |
| Login | ✅ Done |
| Home | ✅ Done |
| Calendar | ✅ Done |
| Wellness | ✅ Done |
| More | ✅ Done |

## Features

- 📊 **Cycle Tracking** — Log flow intensity, symptoms, mood
- 🔮 **Period Prediction** — ML-based prediction using last 6 cycles
- 💧 **Hydration Reminder** — Track daily water intake
- 🧘 **Wellness Tips** — Phase-specific self-care recommendations
- 🚨 **Rest Day Alerts** — Auto-detect heavy flow days
- 🐼 **Mascot** — Emotional support companion with mood-based reactions
- 📅 **Calendar View** — Visual cycle overview
- 🔒 **100% Offline** — All data stays on device

## Building

```bash
./gradlew assembleDebug
```

Requires Android Studio Ladybug Feature Drop (2025.3.1+) or later.

## License

MIT
