# Getting Started with pico-tea

This guide will help you get started with pico-tea, a component library for picocli applications.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Your First Component](#your-first-component)
- [Running Examples](#running-examples)
- [Next Steps](#next-steps)

## Prerequisites

Before you begin, ensure you have:

- Java 21 or higher
- Gradle 8.10.2 or higher
- Basic knowledge of Kotlin
- Familiarity with picocli

## Installation

### Using as a Library

If you want to use pico-tea components in your project:

1. Clone the repository:
```bash
git clone <repository-url>
cd pico-tea
```

2. Build the components module:
```bash
./gradlew :components:build
```

3. Add the dependency to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":components"))
    implementation("info.picocli:picocli:4.7.6")
}
```

### Exploring Examples

To explore the examples included in this project:

1. Clone the repository:
```bash
git clone <repository-url>
cd pico-tea
```

2. Build the project:
```bash
./gradlew build
```

3. Run the main application:
```bash
./gradlew run
```

## Project Structure

```
pico-tea/
├── components/                  # Component library module
│   └── src/main/kotlin/
│       └── com/cartobucket/picocli/components/
│           ├── ProgressBar.kt
│           ├── ProgressBarBuilder.kt
│           ├── ProgressBarStyle.kt
│           └── ProgressRenderer.kt
├── src/main/kotlin/            # Examples and demos
│   └── com/cartobucket/examples/
│       ├── Main.kt
│       └── ProgressBarCommand.kt
├── docs/                       # Documentation
│   ├── components/
│   │   └── ProgressBar.md
│   └── getting-started.md
├── build.gradle.kts           # Root build configuration
├── settings.gradle.kts        # Multi-module setup
└── Makefile                   # Convenience targets
```

## Your First Component

Let's create a simple CLI application using the ProgressBar component.

### Step 1: Create a Command

```kotlin
import com.cartobucket.picocli.components.ProgressBar
import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess

@Command(
    name = "my-app",
    mixinStandardHelpOptions = true,
    description = ["My first pico-tea application"]
)
class MyApp : Runnable {
    override fun run() {
        val progress = ProgressBar.builder()
            .withTotal(100)
            .withLabel("Processing")
            .showRate()
            .build()

        repeat(100) {
            // Simulate work
            Thread.sleep(50)
            progress.step()
        }

        progress.finish()
        println("Done!")
    }
}

fun main(args: Array<String>) {
    exitProcess(CommandLine(MyApp()).execute(*args))
}
```

### Step 2: Add to build.gradle.kts

```kotlin
plugins {
    kotlin("jvm") version "2.1.0"
    application
}

dependencies {
    implementation(project(":components"))
    implementation("info.picocli:picocli:4.7.6")
}

application {
    mainClass.set("com.example.MyAppKt")
}
```

### Step 3: Run Your Application

```bash
./gradlew run
```

You should see a progress bar animating in your terminal!

## Running Examples

The pico-tea project includes several examples demonstrating component usage.

### View Available Commands

```bash
./gradlew run
```

Output:
```
pico-tea - picocli component library

Available commands:
  progressBar  - Demonstrate customizable progress bars

Use 'pico-tea <command> --help' for more information about a command.
```

### Run Progress Bar Examples

Use the Makefile for quick testing:

```bash
make testProgressBars
```

Or run individual examples:

```bash
# Basic progress bar
./gradlew run --args="progressBar --end 50"

# With rate and ETA
./gradlew run --args="progressBar --show-rate --show-eta"

# ASCII style
./gradlew run --args="progressBar --style ASCII"

# Get help
./gradlew run --args="progressBar --help"
```

## Component Usage Patterns

### Pattern 1: Simple Progress

```kotlin
val progress = ProgressBar.builder()
    .withTotal(items.size.toLong())
    .build()

items.forEach { item ->
    processItem(item)
    progress.step()
}
progress.finish()
```

### Pattern 2: With AutoCloseable

```kotlin
ProgressBar.builder()
    .withTotal(100)
    .build().use { progress ->
        repeat(100) {
            doWork()
            progress.step()
        }
    } // Automatically calls finish()
```

### Pattern 3: With Status Updates

```kotlin
val progress = ProgressBar.builder()
    .withTotal(files.size.toLong())
    .withLabel("Processing")
    .build()

files.forEach { file ->
    progress.message("Processing ${file.name}")
    process(file)
    progress.step()
}
progress.finish()
```

### Pattern 4: Indeterminate Progress

```kotlin
val progress = ProgressBar.builder()
    .indeterminate()
    .withLabel("Loading")
    .build()

performUnknownDurationTask()
progress.finish()
```

## Next Steps

Now that you've got the basics, here are some things to explore:

1. **Learn More About ProgressBar**
   - Read the [ProgressBar documentation](components/ProgressBar.md)
   - Experiment with different styles
   - Try rate and ETA tracking

2. **Integrate into Your Project**
   - Add pico-tea to your existing picocli application
   - Create custom visual styles
   - Build your own components following the same patterns

3. **Explore the Source Code**
   - Check out the implementation in `components/src/`
   - Look at example commands in `src/main/kotlin/com/cartobucket/examples/`
   - See how thread safety and rate limiting work

4. **Contribute**
   - Add new components
   - Improve existing ones
   - Share your use cases

## Common Issues

### Progress Bar Not Updating

Make sure you're calling `progress.step()` or `progress.stepTo()` to update the progress.

### Progress Bar Printing Multiple Lines

This can happen in non-interactive environments (like CI). The component automatically detects this and falls back to newline mode.

### ANSI Codes Showing in Output

Ensure your terminal supports ANSI escape codes and the `TERM` environment variable is set.

## Getting Help

- Check the [component documentation](components/)
- Look at the [examples](../src/main/kotlin/com/cartobucket/examples/)
- Open an issue on GitHub

## Additional Resources

- [picocli Documentation](https://picocli.info/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [ANSI Escape Codes Reference](https://en.wikipedia.org/wiki/ANSI_escape_code)
