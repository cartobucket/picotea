# Table Component

A customizable table component for CLI applications with support for multiple visual styles, column alignment, auto-sizing, and overflow handling.

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Customization](#customization)

## Quick Start

```kotlin
import com.cartobucket.picocli.components.table.Table
import com.cartobucket.picocli.components.table.Alignment

data class User(val name: String, val email: String, val age: Int)

val users = listOf(
    User("Alice", "alice@example.com", 30),
    User("Bob", "bob@example.com", 25)
)

Table.builder<User>()
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .addColumn("Age", align = Alignment.RIGHT) { it.age }
    .render(users)
```

## Features

- **Visual Styles**
  - Unicode (box-drawing characters)
  - ASCII (maximum compatibility)
  - Minimal (no borders)
  - Markdown (GitHub-compatible)
  - Compact (minimal spacing)
  - Custom (define your own)

- **Smart Column Sizing**
  - Auto-sizing based on content
  - Fixed-width columns
  - Proportional shrinking for narrow terminals
  - Minimum width enforcement

- **Text Alignment**
  - Left alignment (default)
  - Right alignment (numbers, dates)
  - Center alignment (headers, labels)

- **Overflow Handling**
  - Automatic truncation with ellipsis (...)
  - Configurable per-column
  - Respects terminal width

- **Technical Features**
  - Auto-detects terminal width
  - Type-safe generic columns
  - Builder pattern configuration
  - Auto-closeable (use with `use` blocks)

## API Reference

### Table.builder<T>()

Creates a new TableBuilder for configuring a table with data type T.

### TableBuilder Methods

| Method | Description | Default |
|--------|-------------|---------|
| `addColumn(header, accessor)` | Add a column with header and accessor function | - |
| `addColumn(header, width, align, truncate, accessor)` | Add a column with full configuration | - |
| `withStyle(TableStyle)` | Set visual style | UNICODE |
| `withWidth(Int?)` | Set fixed width (null = auto-detect) | null |
| `withOutput(PrintStream)` | Set output stream | System.out |
| `withTruncateOverflow(Boolean)` | Enable/disable truncation | true |
| `build(List<T>)` | Build the Table with data | - |
| `render(List<T>)` | Build and immediately render | - |

### Table.Column<T>

Column definition:

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `header` | String | Column header text | - |
| `accessor` | (T) -> Any? | Function to extract value from row | - |
| `width` | Int? | Fixed column width (null = auto) | null |
| `align` | Alignment | Text alignment (LEFT/CENTER/RIGHT) | LEFT |
| `truncate` | Boolean | Truncate overflow with ellipsis | true |

### TableStyle

Pre-defined styles available as constants:

- `TableStyle.UNICODE` - Unicode box-drawing characters
- `TableStyle.ASCII` - ASCII characters for compatibility
- `TableStyle.MINIMAL` - No borders, clean spacing
- `TableStyle.MARKDOWN` - Markdown-compatible format
- `TableStyle.COMPACT` - Minimal spacing

Custom style:
```kotlin
TableStyle(
    borderChars = BorderChars.UNICODE,
    showBorders = true,
    showHeaderSeparator = true,
    padding = 1,
    compactMode = false
)
```

### Alignment

Text alignment options:
- `Alignment.LEFT` - Left-aligned text
- `Alignment.CENTER` - Center-aligned text
- `Alignment.RIGHT` - Right-aligned text

## Examples

### Basic Table

```kotlin
data class Product(val name: String, val price: Double, val stock: Int)

val products = listOf(
    Product("Laptop", 999.99, 15),
    Product("Mouse", 24.99, 150),
    Product("Keyboard", 79.99, 45)
)

Table.builder<Product>()
    .addColumn("Product") { it.name }
    .addColumn("Price", align = Alignment.RIGHT) { "$${it.price}" }
    .addColumn("Stock", align = Alignment.RIGHT) { it.stock }
    .render(products)
```

Output:
```
┌──────────┬──────────┬─────────┐
│ Product  │   Price  │  Stock  │
├──────────┼──────────┼─────────┤
│ Laptop   │ $999.99  │     15  │
│ Mouse    │  $24.99  │    150  │
│ Keyboard │  $79.99  │     45  │
└──────────┴──────────┴─────────┘
```

### Fixed Column Widths

```kotlin
Table.builder<User>()
    .addColumn("Name", width = 15) { it.name }
    .addColumn("Email", width = 25) { it.email }
    .addColumn("Age", width = 5, align = Alignment.RIGHT) { it.age }
    .render(users)
```

### Different Styles

```kotlin
// ASCII style
Table.builder<User>()
    .withStyle(TableStyle.ASCII)
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .render(users)

// Minimal style
Table.builder<User>()
    .withStyle(TableStyle.MINIMAL)
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .render(users)

// Markdown style
Table.builder<User>()
    .withStyle(TableStyle.MARKDOWN)
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .render(users)
```

### Complex Data Types

```kotlin
data class Task(
    val id: Int,
    val title: String,
    val status: String,
    val assignee: String?,
    val dueDate: LocalDate?
)

val tasks = listOf(
    Task(1, "Fix bug in auth", "In Progress", "Alice", LocalDate.now()),
    Task(2, "Add logging", "Done", "Bob", null),
    Task(3, "Update docs", "Todo", null, LocalDate.now().plusDays(7))
)

Table.builder<Task>()
    .addColumn("ID", width = 5, align = Alignment.RIGHT) { it.id }
    .addColumn("Title", width = 30) { it.title }
    .addColumn("Status", width = 12, align = Alignment.CENTER) { it.status }
    .addColumn("Assignee", width = 10) { it.assignee ?: "Unassigned" }
    .addColumn("Due Date", width = 12, align = Alignment.RIGHT) {
        it.dueDate?.toString() ?: "N/A"
    }
    .render(tasks)
```

### Using AutoCloseable

```kotlin
Table.builder<User>()
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .build(users).use { table ->
        table.render()
    }
```

### Custom Style

```kotlin
val customBorders = BorderChars(
    topLeft = "╔",
    topRight = "╗",
    bottomLeft = "╚",
    bottomRight = "╝",
    horizontal = "═",
    vertical = "║",
    headerLeft = "╠",
    headerRight = "╣",
    headerCross = "╬",
    leftCross = "╠",
    rightCross = "╣",
    cross = "╬"
)

val customStyle = TableStyle(
    borderChars = customBorders,
    showBorders = true,
    showHeaderSeparator = true,
    padding = 2
)

Table.builder<User>()
    .withStyle(customStyle)
    .addColumn("Name") { it.name }
    .addColumn("Email") { it.email }
    .render(users)
```

### Output to File

```kotlin
import java.io.PrintStream
import java.io.FileOutputStream

PrintStream(FileOutputStream("output.txt")).use { output ->
    Table.builder<User>()
        .withOutput(output)
        .addColumn("Name") { it.name }
        .addColumn("Email") { it.email }
        .render(users)
}
```

## Real-World Use Cases

### Makefile Targets Display

Display available Makefile targets with descriptions:

```kotlin
data class MakeTarget(
    val name: String,
    val description: String,
    val dependencies: List<String>
)

fun parseMakefile(path: String): List<MakeTarget> {
    val targets = mutableListOf<MakeTarget>()
    var currentComment = ""

    File(path).readLines().forEach { line ->
        when {
            line.trimStart().startsWith("##") -> {
                currentComment = line.substringAfter("##").trim()
            }
            line.matches(Regex("^[a-zA-Z0-9_-]+:.*")) -> {
                val parts = line.split(":")
                val name = parts[0].trim()
                val deps = parts[1].split(" ").filter { it.isNotBlank() }
                targets.add(MakeTarget(name, currentComment, deps))
                currentComment = ""
            }
        }
    }
    return targets
}

@Command(name = "make-help")
class MakeHelpCommand : Runnable {
    @Option(names = ["-f", "--file"], defaultValue = "Makefile")
    private var makefilePath: String = "Makefile"

    override fun run() {
        val targets = parseMakefile(makefilePath)

        Table.builder<MakeTarget>()
            .withStyle(TableStyle.UNICODE)
            .addColumn("Target", width = 20) { it.name }
            .addColumn("Description", width = 50) { it.description }
            .addColumn("Dependencies") {
                it.dependencies.joinToString(", ").ifEmpty { "-" }
            }
            .render(targets)
    }
}
```

### Build Task Status

Display build tasks with execution status:

```kotlin
data class BuildTask(
    val name: String,
    val status: String,
    val duration: Long,
    val output: String?
)

fun executeMakeTasks(tasks: List<String>): List<BuildTask> {
    return tasks.map { task ->
        val startTime = System.currentTimeMillis()
        val result = ProcessBuilder("make", task)
            .redirectErrorStream(true)
            .start()

        val exitCode = result.waitFor()
        val duration = System.currentTimeMillis() - startTime
        val status = if (exitCode == 0) "✓ PASS" else "✗ FAIL"

        BuildTask(task, status, duration, null)
    }
}

@Command(name = "make-run")
class MakeRunCommand : Runnable {
    @Parameters(description = "Targets to run")
    private var targets: List<String> = emptyList()

    override fun run() {
        val results = executeMakeTasks(targets)

        Table.builder<BuildTask>()
            .withStyle(TableStyle.UNICODE)
            .addColumn("Task", width = 20) { it.name }
            .addColumn("Status", width = 10, align = Alignment.CENTER) { it.status }
            .addColumn("Duration", align = Alignment.RIGHT) {
                "${it.duration}ms"
            }
            .render(results)
    }
}
```

Output:
```
┌──────────────────────┬────────────┬──────────┐
│        Task          │   Status   │ Duration │
├──────────────────────┼────────────┼──────────┤
│ clean                │  ✓ PASS    │    145ms │
│ build                │  ✓ PASS    │   2341ms │
│ test                 │  ✓ PASS    │   1892ms │
│ package              │  ✓ PASS    │    567ms │
└──────────────────────┴────────────┴──────────┘
```

### Dependency Analysis

Show Gradle/Make dependencies:

```kotlin
data class Dependency(
    val name: String,
    val version: String,
    val type: String,
    val size: String
)

@Command(name = "deps")
class DependencyListCommand : Runnable {
    @Option(names = ["--format"], defaultValue = "unicode")
    private var format: String = "unicode"

    override fun run() {
        val dependencies = listOf(
            Dependency("picocli", "4.7.5", "runtime", "1.2 MB"),
            Dependency("kotlin-stdlib", "1.9.22", "runtime", "1.7 MB"),
            Dependency("junit", "5.10.1", "test", "0.8 MB")
        )

        val style = when (format) {
            "ascii" -> TableStyle.ASCII
            "markdown" -> TableStyle.MARKDOWN
            else -> TableStyle.UNICODE
        }

        Table.builder<Dependency>()
            .withStyle(style)
            .addColumn("Name", width = 25) { it.name }
            .addColumn("Version", width = 12, align = Alignment.CENTER) { it.version }
            .addColumn("Type", width = 10) { it.type }
            .addColumn("Size", align = Alignment.RIGHT) { it.size }
            .render(dependencies)
    }
}
```

### CI/CD Pipeline Status

```kotlin
data class PipelineStage(
    val stage: String,
    val status: String,
    val duration: String,
    val artifacts: Int
)

val stages = listOf(
    PipelineStage("Checkout", "✓", "12s", 0),
    PipelineStage("Build", "✓", "2m 34s", 3),
    PipelineStage("Test", "✓", "1m 18s", 1),
    PipelineStage("Deploy", "⧗", "...", 0)
)

Table.builder<PipelineStage>()
    .withStyle(TableStyle.COMPACT)
    .addColumn("Stage", width = 15) { it.stage }
    .addColumn("Status", width = 8, align = Alignment.CENTER) { it.status }
    .addColumn("Duration", width = 10, align = Alignment.RIGHT) { it.duration }
    .addColumn("Artifacts", align = Alignment.RIGHT) { it.artifacts }
    .render(stages)
```

### File System Summary

```kotlin
data class DirectoryInfo(
    val path: String,
    val files: Int,
    val size: String,
    val lastModified: String
)

@Command(name = "dir-summary")
class DirectorySummaryCommand : Runnable {
    @Parameters(description = "Directories to analyze")
    private var directories: List<String> = listOf(".")

    override fun run() {
        val info = directories.map { dir ->
            val path = File(dir)
            val files = path.walk().filter { it.isFile }.count()
            val size = path.walk()
                .filter { it.isFile }
                .sumOf { it.length() }
            val sizeStr = formatBytes(size)
            val modified = SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(Date(path.lastModified()))

            DirectoryInfo(dir, files, sizeStr, modified)
        }

        Table.builder<DirectoryInfo>()
            .addColumn("Directory", width = 30) { it.path }
            .addColumn("Files", align = Alignment.RIGHT) { it.files }
            .addColumn("Size", align = Alignment.RIGHT) { it.size }
            .addColumn("Modified") { it.lastModified }
            .render(info)
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}
```

## Customization

### CLI Command Example

```kotlin
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "list-users")
class ListUsersCommand : Runnable {
    @Option(names = ["--style"], defaultValue = "UNICODE")
    private var styleName: String = "UNICODE"

    @Option(names = ["--compact"])
    private var compact: Boolean = false

    override fun run() {
        val style = when (styleName.uppercase()) {
            "ASCII" -> TableStyle.ASCII
            "MINIMAL" -> TableStyle.MINIMAL
            "MARKDOWN" -> TableStyle.MARKDOWN
            "COMPACT" -> TableStyle.COMPACT
            else -> TableStyle.UNICODE
        }

        val users = fetchUsers()

        Table.builder<User>()
            .withStyle(style)
            .addColumn("ID", align = Alignment.RIGHT) { it.id }
            .addColumn("Name") { it.name }
            .addColumn("Email") { it.email }
            .addColumn("Role") { it.role }
            .render(users)
    }
}
```

### Dynamic Columns

```kotlin
data class Record(val data: Map<String, Any>)

val records = listOf(
    Record(mapOf("name" to "Alice", "age" to 30)),
    Record(mapOf("name" to "Bob", "age" to 25))
)

val builder = Table.builder<Record>()
val keys = records.firstOrNull()?.data?.keys ?: emptySet()

keys.forEach { key ->
    builder.addColumn(key.uppercase()) { record ->
        record.data[key]
    }
}

builder.render(records)
```

### Conditional Formatting

```kotlin
data class Transaction(val amount: Double, val type: String)

val transactions = listOf(
    Transaction(100.0, "credit"),
    Transaction(-50.0, "debit")
)

Table.builder<Transaction>()
    .addColumn("Type") { it.type.uppercase() }
    .addColumn("Amount", align = Alignment.RIGHT) {
        val symbol = if (it.amount >= 0) "+" else ""
        "$symbol$${it.amount}"
    }
    .render(transactions)
```

## Terminal Compatibility

The table component automatically detects terminal width:

- **Interactive terminals**: Auto-detects width using `tput cols`
- **Fixed width**: Use `withWidth()` to set explicit width
- **Default fallback**: 80 characters when detection fails

## Column Width Calculation

1. **Fixed widths** are allocated first
2. **Auto-sized columns** measure content length
3. **Proportional shrinking** when total exceeds terminal width
4. **Minimum width** of 3 characters enforced

## Performance

- Efficient column width calculation
- Minimal string allocations
- Single-pass rendering
- No dynamic resizing after initial calculation

## See Also

- [ProgressBar Component](./ProgressBar.md)
- [Spinner Component](./Spinner.md)
- [Getting Started Guide](../getting-started.md)
- [Main Documentation](../../README.md)
