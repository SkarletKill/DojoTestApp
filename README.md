# DojoTestApp

Kotlin · Coroutines/Flow · Jetpack Compose (Material 3) · MVI · Hilt · Room · Navigation 3 · JUnit + MockK + Turbine

```bash
./gradlew assembleDebug        # build
./gradlew testDebugUnitTest    # unit tests
./gradlew lintDebug            # lint (warnings are errors)
```

The SDK levels are compileSdk 36, targetSdk 36 and minSdk 26. The build needs JDK 17 or newer.

## Architecture

```
uk.skarlet.dojotestapp
├── core/
│   ├── mvi/            BaseViewModel<State, Intent, Effect>, CollectEffects
│   ├── coroutines/     @IoDispatcher, @DefaultDispatcher, @ApplicationScope
│   └── designsystem/   AppTheme
├── navigation/         Navigation 3 keys + NavDisplay (ViewModels scoped per entry)
└── feature/<name>/
    ├── domain/         models, repository interfaces (pure Kotlin)
    ├── data/           Room entities/DAOs, network DTOs, repository implementations, Hilt bindings
    └── presentation/   Contract (State/Intent/Effect), ViewModel, Route + stateless Screen
```

**Data flow (unidirectional):** `Screen --Intent--> ViewModel --> Repository`, then `Room --Flow--> Repository --> ViewModel --StateFlow<State>--> Screen`.

**Offline-first:** Room is the single source of truth. Repositories expose `Flow` from DAOs, and `refresh()` fetches from the network and writes into Room. The UI never reads the network directly.

The app starts with a single Main screen and no domain logic. Features are added once the product scope is known.
