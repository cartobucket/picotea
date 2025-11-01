package com.cartobucket.examples

import com.cartobucket.picocli.components.table.Table
import com.cartobucket.picocli.components.table.TableStyle
import com.cartobucket.picocli.components.table.Alignment
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "table",
    description = ["Demonstrate table rendering with customizable options"]
)
class TableCommand : Runnable {

    @Option(
        names = ["--style"],
        description = ["Visual style: UNICODE, ASCII, MINIMAL, MARKDOWN, COMPACT (default: UNICODE)"],
        defaultValue = "UNICODE"
    )
    private var styleName: String = "UNICODE"

    @Option(
        names = ["--example"],
        description = ["Example to run: users, products, tasks, dependencies, makefile, pipeline (default: users)"],
        defaultValue = "users"
    )
    private var exampleName: String = "users"

    @Option(
        names = ["--rows"],
        description = ["Number of rows to display (default: 5)"],
        defaultValue = "5"
    )
    private var rowCount: Int = 5

    override fun run() {
        val style = when (styleName.uppercase()) {
            "ASCII" -> TableStyle.ASCII
            "MINIMAL" -> TableStyle.MINIMAL
            "MARKDOWN" -> TableStyle.MARKDOWN
            "COMPACT" -> TableStyle.COMPACT
            else -> TableStyle.UNICODE
        }

        when (exampleName.lowercase()) {
            "users" -> renderUsersExample(style)
            "products" -> renderProductsExample(style)
            "tasks" -> renderTasksExample(style)
            "dependencies" -> renderDependenciesExample(style)
            "makefile" -> renderMakefileExample(style)
            "pipeline" -> renderPipelineExample(style)
            else -> renderUsersExample(style)
        }
    }

    private fun renderUsersExample(style: TableStyle) {
        data class User(val id: Int, val name: String, val email: String, val age: Int, val role: String)

        val users = listOf(
            User(1, "Alice Johnson", "alice@example.com", 30, "Developer"),
            User(2, "Bob Smith", "bob@example.com", 25, "Designer"),
            User(3, "Carol White", "carol@example.com", 35, "Manager"),
            User(4, "David Brown", "david@example.com", 28, "Developer"),
            User(5, "Eve Davis", "eve@example.com", 32, "DevOps"),
            User(6, "Frank Miller", "frank@example.com", 29, "Developer"),
            User(7, "Grace Lee", "grace@example.com", 31, "QA Engineer")
        ).take(rowCount)

        println("\nUser Directory:")
        println("=" .repeat(60))
        println()

        Table.builder<User>()
            .withStyle(style)
            .addColumn("ID", width = 5, align = Alignment.RIGHT) { it.id }
            .addColumn("Name", width = 18) { it.name }
            .addColumn("Email", width = 25) { it.email }
            .addColumn("Age", width = 5, align = Alignment.RIGHT) { it.age }
            .addColumn("Role", width = 12) { it.role }
            .render(users)
    }

    private fun renderProductsExample(style: TableStyle) {
        data class Product(val name: String, val price: Double, val stock: Int, val category: String)

        val products = listOf(
            Product("Laptop Pro 15\"", 1299.99, 15, "Electronics"),
            Product("Wireless Mouse", 24.99, 150, "Accessories"),
            Product("Mechanical Keyboard", 89.99, 45, "Accessories"),
            Product("USB-C Hub", 49.99, 78, "Accessories"),
            Product("Monitor 27\"", 399.99, 22, "Electronics"),
            Product("Webcam HD", 79.99, 64, "Electronics"),
            Product("Desk Lamp", 34.99, 91, "Office")
        ).take(rowCount)

        println("\nProduct Inventory:")
        println("=" .repeat(60))
        println()

        Table.builder<Product>()
            .withStyle(style)
            .addColumn("Product", width = 25) { it.name }
            .addColumn("Price", width = 10, align = Alignment.RIGHT) { "$${String.format("%.2f", it.price)}" }
            .addColumn("Stock", width = 8, align = Alignment.RIGHT) { it.stock }
            .addColumn("Category", width = 15) { it.category }
            .render(products)
    }

    private fun renderTasksExample(style: TableStyle) {
        data class Task(val id: Int, val title: String, val status: String, val assignee: String?, val priority: String)

        val tasks = listOf(
            Task(1, "Fix auth bug", "In Progress", "Alice", "High"),
            Task(2, "Add logging", "Done", "Bob", "Medium"),
            Task(3, "Update docs", "Todo", null, "Low"),
            Task(4, "Code review", "In Progress", "Carol", "High"),
            Task(5, "Deploy staging", "Blocked", "David", "Critical"),
            Task(6, "Write tests", "Todo", "Eve", "Medium"),
            Task(7, "Performance tuning", "In Progress", "Frank", "High")
        ).take(rowCount)

        println("\nTask Board:")
        println("=" .repeat(60))
        println()

        Table.builder<Task>()
            .withStyle(style)
            .addColumn("ID", width = 4, align = Alignment.RIGHT) { it.id }
            .addColumn("Title", width = 22) { it.title }
            .addColumn("Status", width = 12, align = Alignment.CENTER) { it.status }
            .addColumn("Assignee", width = 10) { it.assignee ?: "Unassigned" }
            .addColumn("Priority", width = 10, align = Alignment.CENTER) { it.priority }
            .render(tasks)
    }

    private fun renderDependenciesExample(style: TableStyle) {
        data class Dependency(val name: String, val version: String, val type: String, val size: String)

        val dependencies = listOf(
            Dependency("picocli", "4.7.5", "runtime", "1.2 MB"),
            Dependency("kotlin-stdlib", "1.9.22", "runtime", "1.7 MB"),
            Dependency("junit-jupiter", "5.10.1", "test", "0.8 MB"),
            Dependency("mockk", "1.13.8", "test", "0.5 MB"),
            Dependency("slf4j-api", "2.0.9", "runtime", "0.2 MB"),
            Dependency("logback-classic", "1.4.14", "runtime", "0.6 MB")
        ).take(rowCount)

        println("\nProject Dependencies:")
        println("=" .repeat(60))
        println()

        Table.builder<Dependency>()
            .withStyle(style)
            .addColumn("Name", width = 20) { it.name }
            .addColumn("Version", width = 12, align = Alignment.CENTER) { it.version }
            .addColumn("Type", width = 10) { it.type }
            .addColumn("Size", width = 10, align = Alignment.RIGHT) { it.size }
            .render(dependencies)
    }

    private fun renderMakefileExample(style: TableStyle) {
        data class MakeTarget(val name: String, val description: String, val status: String)

        val targets = listOf(
            MakeTarget("build", "Build the project", "✓"),
            MakeTarget("test", "Run all tests", "✓"),
            MakeTarget("clean", "Clean build artifacts", "-"),
            MakeTarget("run", "Run the application", "✓"),
            MakeTarget("testProgressBars", "Run progress bar examples", "✓"),
            MakeTarget("testSpinners", "Run spinner examples", "✓"),
            MakeTarget("testTables", "Run table examples", "✓")
        ).take(rowCount)

        println("\nMakefile Targets:")
        println("=" .repeat(60))
        println()

        Table.builder<MakeTarget>()
            .withStyle(style)
            .addColumn("Target", width = 20) { it.name }
            .addColumn("Description", width = 35) { it.description }
            .addColumn("Status", width = 8, align = Alignment.CENTER) { it.status }
            .render(targets)
    }

    private fun renderPipelineExample(style: TableStyle) {
        data class PipelineStage(val stage: String, val status: String, val duration: String, val artifacts: Int)

        val stages = listOf(
            PipelineStage("Checkout", "✓ PASS", "12s", 0),
            PipelineStage("Build", "✓ PASS", "2m 34s", 3),
            PipelineStage("Unit Tests", "✓ PASS", "1m 18s", 1),
            PipelineStage("Integration Tests", "✓ PASS", "3m 45s", 2),
            PipelineStage("Code Coverage", "✓ PASS", "45s", 1),
            PipelineStage("Security Scan", "⧗ Running", "...", 0),
            PipelineStage("Deploy Staging", "○ Pending", "...", 0)
        ).take(rowCount)

        println("\nCI/CD Pipeline Status:")
        println("=" .repeat(60))
        println()

        Table.builder<PipelineStage>()
            .withStyle(style)
            .addColumn("Stage", width = 20) { it.stage }
            .addColumn("Status", width = 12, align = Alignment.CENTER) { it.status }
            .addColumn("Duration", width = 12, align = Alignment.RIGHT) { it.duration }
            .addColumn("Artifacts", width = 10, align = Alignment.RIGHT) { it.artifacts }
            .render(stages)
    }
}
