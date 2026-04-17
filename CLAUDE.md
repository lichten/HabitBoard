# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew clean                  # Clean build artifacts
```

Requires Java 11+. JVM heap is set to 2048m in `gradle.properties`.

## Architecture Overview

HabitBoard is an Android habit-tracking app (Japanese UI) with a home screen widget. It uses a clean layered architecture with no DI framework.

### Data Layer (`data/`)

- **Entities**: `Habit` (id, name, order) and `HabitRecord` (habitId, date, isDone) with composite PK `[habitId, date]`
- **Room DAOs**: expose both `Flow`-based reactive queries and `suspend` sync variants
- **TypeConverter**: serializes `java.time.LocalDate` ↔ ISO string for Room
- **`HabitRepository`**: single repository wrapping both DAOs; exposes `Flow`-based and suspend APIs

### UI Layer (`ui/`)

- `MainScreen` + `MainViewModel`: displays today's habits, toggles completion
- `ManageScreen` + `ManageViewModel`: add/edit/delete habits
- ViewModels use `combine(habitsFlow, recordsFlow).stateIn(WhileSubscribed(5000))` to merge DB streams into a single `StateFlow<UiState>`
- Navigation: single `MainActivity` with a `NavHost`, routes `main` → `manage`

### Widget (`widget/`)

- `HabitWidget` uses Glance (Compose-style widget API); reads DB **synchronously** via `getAllSync()` / `getRecordsForDateSync()` inside `provideGlance()`
- Displays up to the first 5 habits; click opens `MainActivity`
- **Widget updates are not automatic**: ViewModels must call `HabitWidget().updateAll(context)` after any DB mutation

### Key Patterns

- No dependency injection — `HabitRepository(context)` is instantiated directly in ViewModels
- All DB writes go through `viewModelScope.launch { }` in the ViewModel, followed by `HabitWidget().updateAll(context)`
- Widget uses a custom dark color scheme (not Material3 tokens)
- Design document with UI specs is at `habit_app_design.md` (written in Japanese)
