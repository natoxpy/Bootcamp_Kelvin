# Workshop4

A desktop application (JavaFX + WebView) that combines a Java 17 backend with a Vue 3 + TypeScript frontend. The Vue app is built with Vite, bundled into the Java build, and displayed inside a JavaFX `WebView`. Data is stored in SQLite via JDBI.

## Project Structure

```
.
├── app/                  # Java application (Gradle subproject)
│   ├── data/app.db       # SQLite database (created/used at runtime)
│   └── src/main/java/net/tarou/
│       ├── Main.java     # JavaFX entry point, hosts the WebView
│       ├── api/Api.java  # Object exposed to JavaScript as `window.api`
│       └── db/           # JDBI DAOs, records, and Database wrapper
├── frontend/             # Vue 3 + Vite + TypeScript + Tailwind frontend
├── gradle/               # Version catalog and Gradle wrapper
├── build.gradle.kts / settings.gradle.kts
└── gradlew / gradlew.bat
```

## Requirements

- **JDK 17** (the Java toolchain is set to 17; Gradle's foojay resolver can download one automatically if missing)
- **Node.js** `^22.18.0` or `>=24.12.0` (per `frontend/package.json` `engines`)
- **npm** (ships with Node)
- **Gradle** is optional; the wrapper (`./gradlew`) downloads Gradle 9.8.2 automatically
- **Linux:** JavaFX WebView requires GTK/WebKit system libraries (e.g. `libgtk-3-0`, `libwebkit2gtk-4.1-0` on Debian/Ubuntu)
- **Windows:** no extra system libraries are needed; use `gradlew.bat` instead of `./gradlew`

## Getting Started

### 1. Install and build the frontend

From `frontend/`:

```bash
cd frontend
npm install
npm run build
```

### 2. Run the application

From the project root:

```bash
cd ..
gradle run          # or: ./gradlew :app:run
```

`gradle run` copies the frontend build output (`frontend/dist`) into the Java build under `web/`, then launches the JavaFX app. On Windows use `gradlew.bat :app:run`.

### 4. Build a runnable JAR

```bash
./gradlew :app:build
```

The output is written to `app/build/libs/app.jar`.

## Other Useful Commands

Run these from `frontend/`:

| Command | Purpose |
| --- | --- |
| `npm run build` | Type-check and build production assets |
| `npm run test:unit` | Run Vitest unit tests |
| `npm run lint` | Run oxlint and ESLint (with auto-fix) |
| `npm run format` | Format `src/` with Prettier |

From the project root:

```bash
./gradlew test      # JUnit 5 tests
```

## Work in Progress

This project is under active development. The following items are known to be incomplete:

- **Backend API is a stub.** `Api.java` only stores a `Database` reference and exposes no methods yet.
- **Frontend calls a missing method.** `frontend/src/views/HomeView.vue` calls `window.api.greet('Tanya')`, but no `greet` method exists in `Api.java`, so this call will fail at runtime.
- **Database layer is partially implemented.** `Database.java` has its query method commented out, and `DealershipDao.createTable()` is not invoked from `Main.java`. The `Dealership` record only maps `name` (`address` and `phone` are `null`), and the vehicle table definition is commented out.
- **Frontend is still the Vue starter template.** Components such as `HelloWorld`, `TheWelcome`, and the `AboutView` page have not been replaced with application UI.
- **Window title is `TSS`** while the Gradle project name is `tanya`; the naming should be aligned.
- **Duplicate/unused data files.** `app/data/app.db`, `app/src/main/resources/sqlite.db`, and `app/database` exist; it is not yet clear which one is the source of truth.
- **Unit tests:** `frontend/src/components/__tests__/HelloWorld.spec.ts` is template code and no Java tests exist yet.
