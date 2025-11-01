# pico-tea

A component library for picocli - reusable CLI components to enhance your command-line applications.

## Overview

pico-tea provides pre-built, customizable components for picocli applications, making it easy to add rich terminal UI elements to your CLI tools.

## Components

- [ProgressBar](docs/components/ProgressBar.md) - Customizable progress bars with multiple styles and display options
- [Spinner](docs/components/Spinner.md) - Animated spinners for indeterminate operations

## Quick Start

### Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":components"))
}
```

### Basic Usage

**ProgressBar:**
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

**Spinner:**
```kotlin
import com.cartobucket.picocli.components.spinner.Spinner

val spinner = Spinner.builder()
    .withLabel("Loading")
    .build()

// do work
performOperation()

spinner.finish()
```

## Examples

This project includes working examples demonstrating all components. Run them with:

```bash
# View available commands
./gradlew run

# Run progress bar examples
make testProgressBars

# Run spinner examples
make testSpinners

# Run with custom options
./gradlew run --args="progressBar --end 100 --show-rate --show-eta"
./gradlew run --args="spinner --style BRAILLE --duration 5 --with-messages"
```

## Documentation

- [Getting Started](docs/getting-started.md)
- [Components](docs/components/)
  - [ProgressBar](docs/components/ProgressBar.md)
  - [Spinner](docs/components/Spinner.md)

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

## Publishing

This library is published to Maven Central. To publish a new version:

1. Set up the required secrets in your GitHub repository:
   - `OSSRH_USERNAME`: Your Sonatype OSSRH username
   - `OSSRH_PASSWORD`: Your Sonatype OSSRH password
   - `GPG_PRIVATE_KEY`: Your GPG private key (export with `gpg --armor --export-secret-keys your-key-id`)
   - `GPG_PASSPHRASE`: Your GPG key passphrase

2. Update the version in `components/build.gradle.kts`

3. Create and publish a GitHub release, which will trigger the publication workflow

4. For SNAPSHOT versions, the workflow can be triggered manually via workflow_dispatch

## License

MIT License

## Contributing

[Add contributing guidelines here]
