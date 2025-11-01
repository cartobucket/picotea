# Spinner Component

A customizable spinner component for CLI applications to indicate ongoing operations with unknown duration.

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Customization](#customization)

## Quick Start

```kotlin
import com.cartobucket.picocli.components.spinner.Spinner

val spinner = Spinner.builder()
    .withLabel("Loading")
    .build()

// Perform your long-running operation
performOperation()

spinner.finish()
```

## Features

- **Multiple Animation Styles**
  - Braille (smooth Unicode characters)
  - Dots (animated dots)
  - Line (ASCII compatible)
  - Arc (curved animation)
  - Arrow (directional spinner)
  - Box (box drawing characters)
  - Bounce (bouncing animation)
  - Circle (rotating circle)
  - Growing dots (expanding pattern)
  - Ellipsis (simple dots)
  - Clock (emoji clock faces)
  - Bar (progress bar style)

- **Rich Features**
  - Optional label/prefix
  - Dynamic message updates
  - Customizable update interval
  - Thread-safe concurrent updates
  - Auto-closeable (use with `use` blocks)

- **Technical Features**
  - ANSI escape code support
  - Terminal capability detection
  - Rate-limited rendering (prevents flickering)

## API Reference

### Spinner.builder()

Creates a new SpinnerBuilder for configuring a spinner.

### SpinnerBuilder Methods

| Method | Description | Default |
|--------|-------------|---------|
| `withLabel(String)` | Set the label/prefix | "" |
| `withStyle(SpinnerStyle)` | Set animation style | BRAILLE |
| `withOutput(PrintStream)` | Set output stream | System.err |
| `withUpdateInterval(Long)` | Set update interval (ms) | style's interval |
| `build()` | Build the Spinner | - |

### Spinner Methods

| Method | Description |
|--------|-------------|
| `message(String)` | Update status message |
| `update()` | Manually trigger spinner update |
| `finish(String)` | Stop spinner with optional final message |
| `getElapsedSeconds()` | Get elapsed time since start |
| `close()` | Same as finish() (AutoCloseable) |

### SpinnerStyle

Pre-defined styles available as constants:

- `SpinnerStyle.BRAILLE` - ⠋ ⠙ ⠹ ⠸ ⠼ ⠴ ⠦ ⠧ ⠇ ⠏ (default, 80ms)
- `SpinnerStyle.DOTS` - ⠋ ⠙ ⠚ ⠞ ⠖ ⠦ ⠴ ⠲ ⠳ ⠓ (80ms)
- `SpinnerStyle.LINE` - - \ | / (100ms, ASCII)
- `SpinnerStyle.ARC` - ◜ ◠ ◝ ◞ ◡ ◟ (100ms)
- `SpinnerStyle.ARROW` - ← ↖ ↑ ↗ → ↘ ↓ ↙ (100ms)
- `SpinnerStyle.BOX` - ▖ ▘ ▝ ▗ (100ms)
- `SpinnerStyle.BOUNCE` - ⠁ ⠂ ⠄ ⡀ ⢀ ⠠ ⠐ ⠈ (80ms)
- `SpinnerStyle.CIRCLE` - ◐ ◓ ◑ ◒ (120ms)
- `SpinnerStyle.GROWING_DOTS` - ⣾ ⣽ ⣻ ⢿ ⡿ ⣟ ⣯ ⣷ (80ms)
- `SpinnerStyle.ELLIPSIS` - .   ..  ... (200ms)
- `SpinnerStyle.CLOCK` - 🕐 🕑 🕒... (100ms)
- `SpinnerStyle.BAR` - [    ] [=   ] [==  ]... (100ms)

Custom style:
```kotlin
SpinnerStyle(
    frames = listOf(".", "o", "O", "o"),
    interval = 100
)
```

## Examples

### Basic Spinner

```kotlin
val spinner = Spinner.builder()
    .withLabel("Loading")
    .build()

// Perform operation
Thread.sleep(3000)

spinner.finish()
```

### With Dynamic Messages

```kotlin
val spinner = Spinner.builder()
    .withLabel("Processing")
    .build()

spinner.message("Connecting to server...")
connectToServer()

spinner.message("Loading data...")
loadData()

spinner.message("Finalizing...")
finalize()

spinner.finish("Complete!")
```

### Different Styles

```kotlin
// ASCII compatible (for limited terminals)
val spinner = Spinner.builder()
    .withLabel("Working")
    .withStyle(SpinnerStyle.LINE)
    .build()

// Elegant Unicode
val spinner = Spinner.builder()
    .withLabel("Processing")
    .withStyle(SpinnerStyle.BRAILLE)
    .build()

// Fun emoji
val spinner = Spinner.builder()
    .withLabel("Waiting")
    .withStyle(SpinnerStyle.CLOCK)
    .build()
```

### Using AutoCloseable

```kotlin
Spinner.builder()
    .withLabel("Downloading")
    .build().use { spinner ->
        files.forEach { file ->
            spinner.message("Downloading ${file.name}")
            download(file)
        }
    } // Automatically calls finish()
```

### Custom Update Interval

```kotlin
val spinner = Spinner.builder()
    .withLabel("Loading")
    .withStyle(SpinnerStyle.BRAILLE)
    .withUpdateInterval(50) // Update every 50ms
    .build()
```

### Manual Updates

```kotlin
val spinner = Spinner.builder()
    .withLabel("Processing")
    .build()

while (hasMoreWork()) {
    doSomeWork()
    spinner.update() // Manually trigger spinner animation
}

spinner.finish()
```

### With Elapsed Time

```kotlin
val spinner = Spinner.builder()
    .withLabel("Running")
    .build()

performOperation()

val elapsed = spinner.getElapsedSeconds()
spinner.finish("Completed in ${elapsed}s")
```

## Customization

### CLI Command Example

```kotlin
@Command(name = "spinner")
class SpinnerCommand : Runnable {
    @Option(names = ["--duration"], defaultValue = "3")
    private var durationSeconds: Int = 3

    @Option(names = ["--style"], defaultValue = "BRAILLE")
    private var styleName: String = "BRAILLE"

    @Option(names = ["--label"], defaultValue = "Loading")
    private var label: String = "Loading"

    override fun run() {
        val style = when (styleName.uppercase()) {
            "LINE" -> SpinnerStyle.LINE
            "ARC" -> SpinnerStyle.ARC
            "ARROW" -> SpinnerStyle.ARROW
            else -> SpinnerStyle.BRAILLE
        }

        val spinner = Spinner.builder()
            .withLabel(label)
            .withStyle(style)
            .build()

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < durationSeconds * 1000L) {
            spinner.update()
            Thread.sleep(style.interval)
        }

        spinner.finish()
    }
}
```

## Terminal Compatibility

The spinner automatically detects terminal capabilities:

- **Interactive terminals**: Uses ANSI escape codes for in-place updates
- **Non-interactive (CI/logs)**: Falls back to newline-per-update mode
- **No TERM variable**: Disables interactive mode

For maximum compatibility, use `SpinnerStyle.LINE` which uses only ASCII characters.

## Thread Safety

The Spinner component is thread-safe and can be updated from multiple threads:

```kotlin
val spinner = Spinner.builder()
    .withLabel("Processing")
    .build()

val threads = (1..5).map { threadId ->
    thread {
        repeat(10) {
            doWork(threadId)
            spinner.message("Thread $threadId working...")
            Thread.sleep(100)
        }
    }
}

threads.forEach { it.join() }
spinner.finish("All threads complete!")
```

## Performance

- Updates are rate-limited (default: 80-200ms depending on style) to prevent excessive rendering
- Efficient string building with minimal allocations
- Automatic throttling prevents terminal flickering

## When to Use

Use a **Spinner** when:
- The operation has unknown or indeterminate duration
- You want to show that the application is working, not frozen
- You need simple, elegant loading indicators

Use a **ProgressBar** instead when:
- You know the total work to be done
- You want to show percentage complete or ETA
- You need detailed progress metrics

## See Also

- [ProgressBar Component](./ProgressBar.md)
- [Getting Started Guide](../getting-started.md)
- [Main Documentation](../../README.md)
