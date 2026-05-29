# Architecture Guide — RickMortyCharacters

> Monolith Clean Architecture · MVVM + MVI · Offline-First · Kotlin · Jetpack Compose

---

## 1. Package Structure

```
com.android.rickmortyandroid
├── core/
│   ├── data/
│   │   ├── db/              # AppDatabase
│   │   └── util/            # NetworkMonitor
│   ├── domain/
│   │   └── state/           # ScreenState sealed interface
│   └── ui/
│       └── components/      # Shared composables
├── di/                      # Hilt modules (App, Database, Network, Character)
├── feature/
│   └── characters/
│       ├── data/
│       │   ├── local/       # Room entities + DAOs
│       │   ├── remote/      # Retrofit DTOs + API interface
│       │   ├── mapper/      # DTO → Entity → Domain
│       │   ├── mediator/    # RemoteMediator
│       │   └── repository/  # CharacterRepositoryImpl
│       ├── domain/
│       │   ├── model/       # CharacterModel (pure Kotlin)
│       │   ├── repository/  # CharacterRepository interface
│       │   └── usecase/     # GetCharactersUseCase
│       └── ui/
│           └── list/        # MVI contract, ViewModel, Screen, Content
├── navigation/              # AppNavGraph
└── MainActivity.kt
```

---

## 2. Layer Responsibilities

| Layer | Knows about | Must NOT know about |
|---|---|---|
| `domain` | Pure Kotlin only | Android, Room, Retrofit, Hilt |
| `data` | `domain`, Room, Retrofit | `ui` |
| `ui` | `domain` (via ViewModel) | `data` directly |

---

## 3. MVVM + MVI Contract

Each screen defines three types in `*Contract.kt`:

```kotlin
sealed interface CharacterListIntent  // user actions
data class CharacterListUiState(...)  // what the UI renders
sealed interface CharacterListEffect  // one-shot events (navigation, toasts)
```

- `ViewModel` processes `Intent` → updates `UiState` → emits `Effect`
- Screen composables are **stateless** — they receive state and emit intents only

---

## 4. Offline-First Strategy

```
App launch
  └─ Room cache exists?
       ├─ YES → SKIP_INITIAL_REFRESH  (serve Room instantly, background sync)
       └─ NO  → LAUNCH_INITIAL_REFRESH (fetch from network, populate Room)

Network error during load
  └─ IOException → MediatorResult.Error (cache preserved, user stays online)

Pull-to-refresh / explicit refresh
  └─ Clears Room only after a successful network response
```

---

## 5. Dependency Rules (Hilt)

- `DatabaseModule` → provides `AppDatabase`, `CharacterDao`, `CharacterRemoteKeysDao`
- `NetworkModule` → provides `OkHttpClient`, `Retrofit`, `CharacterApi`
- `CharacterModule` → `@Binds CharacterRepository → CharacterRepositoryImpl`
- `AppModule` → provides `NetworkMonitor`

---

## 6. Naming Conventions

| Type | Suffix | Example |
|---|---|---|
| Room entity | `Entity` | `CharacterEntity` |
| Network DTO | `Dto` | `CharacterDto` |
| Domain model | `Model` | `CharacterModel` |
| Repository interface | `Repository` | `CharacterRepository` |
| Repository impl | `RepositoryImpl` | `CharacterRepositoryImpl` |
| Use case | `UseCase` | `GetCharactersUseCase` |
| ViewModel | `ViewModel` | `CharacterListViewModel` |
| Screen composable | `Screen` | `CharacterListScreen` |
| Stateless content | `Content` | `CharacterListContent` |

---

## 7. Git Workflow

- `main` is always **buildable** — every merge must pass `./gradlew assembleDebug`
- Branches are cut from `main` and merged back via PR
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/): `feat(scope):`, `chore(scope):`, `fix(scope):`, `docs:`
- Branch order: `initial_setup` → `RM-02` → `RM-03` → … → `RM-10`
