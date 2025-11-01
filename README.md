# pico-tea

A component library for picocli - reusable CLI components to enhance your command-line applications.

## Overview

pico-tea provides pre-built, customizable components for picocli applications, making it easy to add rich terminal UI elements to your CLI tools.

## Components

- [ProgressBar](docs/components/ProgressBar.md) - Customizable progress bars with multiple styles and display options

## Quick Start

### Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":components"))
}
```

### Basic Usage

```kotlin
import com.cartobucket.picocli.components.ProgressBar

val progress = ProgressBar.builder()
    .withTotal(100)
    .withLabel("Processing")
    .build()

repeat(100) {
    // do work
    progress.step()
}
progress.finish()
```

## Examples

This project includes working examples demonstrating all components. Run them with:

```bash
# View available commands
./gradlew run

# Run progress bar examples
make testProgressBars

# Run with custom options
./gradlew run --args="progressBar --end 100 --show-rate --show-eta"
```

## Documentation

- [Getting Started](docs/getting-started.md)
- [Components](docs/components/)
  - [ProgressBar](docs/components/ProgressBar.md)

## Building

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run examples
./gradlew run
```

## Requirements

- Java 21+
- Kotlin 2.1.0+
- Gradle 8.10.2+

## License

[Add your license here]

## Contributing

[Add contributing guidelines here]
