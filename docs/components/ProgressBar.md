# ProgressBar Component

A customizable progress bar component for CLI applications with support for multiple visual styles, rate tracking, and status messages.

## Demo

<video width="800" controls>
  <source src="./recordings/progressBar.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Customization](#customization)

## Quick Start

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

## Features

- **Multiple Display Modes**
  - Determinate (known total)
  - Indeterminate (unknown total, spinner animation)

- **Visual Styles**
  - Unicode (smooth block characters)
  - ASCII (maximum compatibility)
  - Minimal (simple design)
  - Dots (circular indicators)
  - Custom (define your own)

- **Rich Information Display**
  - Percentage completion
  - Current/total count
  - Processing rate (items/sec)
  - Estimated time to completion (ETA)
  - Status messages

- **Technical Features**
  - Thread-safe concurrent updates
  - Rate-limited rendering (prevents flickering)
  - ANSI escape code support
  - Auto-closeable (use with `use` blocks)

## API Reference

### ProgressBar.builder()

Creates a new ProgressBarBuilder for configuring a progress bar.

### ProgressBarBuilder Methods

| Method | Description | Default |
|--------|-------------|---------|
| `withTotal(Long)` | Set the total number of items | 100 |
| `withLabel(String)` | Set the label/prefix | "" |
| `withWidth(Int?)` | Set fixed width (null = auto-detect) | null |
| `withStyle(ProgressBarStyle)` | Set visual style | UNICODE |
| `indeterminate()` | Enable indeterminate mode | false |
| `showRate()` | Show processing rate | false |
| `showEta()` | Show estimated time remaining | false |
| `showPercentage(Boolean)` | Show/hide percentage | true |
| `showCount(Boolean)` | Show/hide count | true |
| `withUnit(String)` | Set unit name for rate | "items" |
| `withOutput(PrintStream)` | Set output stream | System.err |
| `withUpdateInterval(Long)` | Set update interval (ms) | 100 |
| `build()` | Build the ProgressBar | - |

### ProgressBar Methods

| Method | Description |
|--------|-------------|
| `step(Long)` | Increment progress by amount (default: 1) |
| `stepTo(Long)` | Set progress to absolute value |
| `message(String)` | Update status message |
| `finish()` | Complete and cleanup |
| `close()` | Same as finish() (AutoCloseable) |

### ProgressBarStyle

Pre-defined styles available as constants:

- `ProgressBarStyle.UNICODE` - `[████████░░░░]`
- `ProgressBarStyle.ASCII` - `[####----]`
- `ProgressBarStyle.MINIMAL` - `[====    ]`
- `ProgressBarStyle.DOTS` - `[●●●●○○○○]`

Custom style:
```kotlin
ProgressBarStyle(
    leftBracket = "[",
    rightBracket = "]",
    filledChar = "█",
    unfilledChar = "░"
)
```

## Examples

### Basic Progress Bar

```kotlin
val progress = ProgressBar.builder()
    .withTotal(50)
    .withLabel("Processing")
    .build()

repeat(50) {
    Thread.sleep(50)
    progress.step()
}
progress.finish()
```

### With Rate and ETA

```kotlin
val progress = ProgressBar.builder()
    .withTotal(200)
    .withLabel("Processing files")
    .withUnit("files")
    .showRate()
    .showEta()
    .build()

files.forEach { file ->
    processFile(file)
    progress.step()
}
progress.finish()
```

### With Status Messages

```kotlin
val progress = ProgressBar.builder()
    .withTotal(files.size.toLong())
    .withLabel("Downloading")
    .showRate()
    .withUnit("files")
    .build()

files.forEach { file ->
    progress.message("Downloading ${file.name}")
    downloadFile(file)
    progress.step()
}
progress.finish()
```

### Indeterminate Mode

```kotlin
val progress = ProgressBar.builder()
    .withLabel("Processing")
    .indeterminate()
    .build()

// For operations with unknown duration
performLongOperation()
progress.finish()
```

### Using AutoCloseable

```kotlin
ProgressBar.builder()
    .withTotal(100)
    .build().use { progress ->
        repeat(100) {
            progress.step()
        }
    } // Automatically calls finish()
```

### Custom Style

```kotlin
val customStyle = ProgressBarStyle(
    leftBracket = "<",
    rightBracket = ">",
    filledChar = "=",
    unfilledChar = " "
)

val progress = ProgressBar.builder()
    .withTotal(50)
    .withStyle(customStyle)
    .build()
```

## Customization

### CLI Command Example

```kotlin
@Command(name = "progressBar")
class ProgressBarCommand : Runnable {
    @Option(names = ["--end"], defaultValue = "100")
    private var end: Long = 100

    @Option(names = ["--style"], defaultValue = "UNICODE")
    private var styleName: String = "UNICODE"

    @Option(names = ["--show-rate"])
    private var showRate: Boolean = false

    override fun run() {
        val style = when (styleName.uppercase()) {
            "ASCII" -> ProgressBarStyle.ASCII
            "MINIMAL" -> ProgressBarStyle.MINIMAL
            else -> ProgressBarStyle.UNICODE
        }

        val progress = ProgressBar.builder()
            .withTotal(end)
            .withStyle(style)
            .apply { if (showRate) showRate() }
            .build()

        repeat(end.toInt()) {
            progress.step()
            Thread.sleep(50)
        }
        progress.finish()
    }
}
```

## Terminal Compatibility

The progress bar automatically detects terminal capabilities:

- **Interactive terminals**: Uses ANSI escape codes for in-place updates
- **Non-interactive (CI/logs)**: Falls back to newline-per-update mode
- **No TERM variable**: Disables interactive mode

## Thread Safety

The ProgressBar component is thread-safe and can be updated from multiple threads:

```kotlin
val progress = ProgressBar.builder()
    .withTotal(1000)
    .build()

(1..10).map { threadId ->
    thread {
        repeat(100) {
            doWork()
            progress.step()
        }
    }
}.forEach { it.join() }

progress.finish()
```

## Performance

- Updates are rate-limited (default: 100ms interval) to prevent excessive rendering
- Efficient string building with minimal allocations
- Automatic throttling prevents terminal flickering

## See Also

- [Getting Started Guide](../getting-started.md)
- [Main Documentation](../../README.md)
