# Capstone 1

**Capstone one build with Rosu. A generic UI library designed to provide an abstraction for building user interfaces independently of their underlying rendering implementation.**

The project separates UI definitions, core functionality, and rendering, allowing applications to build interfaces without being tightly coupled to a particular rendering backend.

## Project Structure

Rosu is organized into several Gradle modules:

| Module | Description |
|---|---|
| `core/` | Core library functionality. |
| `generic-user-interface/` | Generic UI abstractions and interface definitions. |
| `renderer/` | UI rendering implementation. |
| `demo/` | The Capstone |

## Demo has the Capstone 1 Work

The `demo/` directory contains the actual projects built with Rosu.

These projects serve as practical examples of how the library can be used and demonstrate its capabilities through real implementations.

See [`demo/`](./demo/) to explore the projects.

## Building

Rosu uses Gradle as its build system and requires **Java 17** or newer.

Clone the repository:

```bash
git clone https://github.com/natoxpy/rosu.git
cd rosu
```

Build the project:

```bash
./gradlew build
```

### Running the Demo

The demo application can be packaged into a standalone distribution using Gradle's `installDist` task.

```bash
./gradlew :demo:installDist
```

Once built, the executable and its runtime dependencies will be available under:

```text
demo/build/install/
```

On Linux, navigate to the generated `bin/` directory and execute the application launcher:

```bash
./demo/build/install/<application-name>/bin/<application-name>
```

The generated distribution includes the application and its runtime dependencies, so there is no need to manually construct a Java classpath.

## Technologies

- Java 17
- Gradle
- JLine
