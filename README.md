<h1 align="center">Rick & Morty Characters</h1>

<p align="center">
  An offline-first Android app built with Clean Architecture, Jetpack Compose, and Paging 3 — browsing characters from the <a href="https://rickandmortyapi.com/">Rick & Morty API</a>.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-brightgreen" />
</p>

---

## ✨ Features

- 📜 Paginated character list with **staggered grid** layout
- ⚡ **Offline-first** — browsable without network using Room cache
- 🌐 **Network banner** — real-time online/offline status
- 💀 Status indicators — Alive · Dead · Unknown
- 🎨 Custom **dark portal-green** theme inspired by the show
- 🦴 Shimmer skeleton loading states

---

## 🏗️ Architecture

Clean Architecture with a strict 3-layer separation:

```
Presentation (Compose + ViewModel + MVI)
      ↓
  Domain (UseCases · Models · Repository interfaces — pure Kotlin)
      ↓
   Data (Room · Retrofit · RemoteMediator · Mappers)
```

> See [ARCHITECTURE.md](ARCHITECTURE.md) for full package structure, naming conventions, and layer rules.

---

## 🛠️ Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| DI | Hilt 2.59.2 |
| Async | Kotlin Coroutines + Flow |
| Paging | Paging 3.3.6 + RemoteMediator |
| Network | Retrofit 2.11.0, OkHttp 4.12.0 |
| Database | Room 2.7.1 |
| Images | Coil 2.7.0 |
| Annotation processing | KSP 2.2.10-2.0.2 |

---

## 🚀 Getting Started

1. **Clone the repo**
   ```bash
   git clone https://github.com/<your-username>/RickMortyAndroid.git
   ```
2. Open in **Android Studio Ladybug** or newer
3. **Sync Gradle** — no API keys required, the Rick & Morty API is public
4. **Run** on an emulator or device (API 24+)

---

## 📁 Project Structure

```
app/src/main/java/com/android/rickmortyandroid/
├── core/          # Shared DB, state models, UI components
├── di/            # Hilt modules
├── feature/
│   └── characters/
│       ├── data/      # Room, Retrofit, Mapper, RemoteMediator
│       ├── domain/    # Model, Repository interface, UseCase
│       └── ui/        # MVI contract, ViewModel, Screen
└── navigation/    # NavGraph
```

---

## 📄 License

```
MIT License — feel free to use this as a reference or starter.
```
