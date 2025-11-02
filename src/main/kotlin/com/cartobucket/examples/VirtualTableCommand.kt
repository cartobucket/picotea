package com.cartobucket.examples

import com.cartobucket.picocli.components.table.VirtualTable
import com.cartobucket.picocli.components.table.VirtualTableStyle
import com.cartobucket.picocli.components.table.Alignment
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "virtualTable",
    description = ["Demonstrate an interactive virtual table with keyboard navigation"]
)
class VirtualTableCommand : Runnable {

    @Option(
        names = ["--style"],
        description = ["Visual style: DEFAULT, ASCII, MINIMAL, COMPACT"],
        defaultValue = "DEFAULT"
    )
    private var styleName: String = "DEFAULT"

    @Option(
        names = ["--rows"],
        description = ["Number of data rows to generate"],
        defaultValue = "100"
    )
    private var rowCount: Int = 100

    @Option(
        names = ["--height"],
        description = ["Number of visible rows in the table"],
        defaultValue = "10"
    )
    private var height: Int = 10

    @Option(
        names = ["--example"],
        description = ["Example dataset: users, products, logs"],
        defaultValue = "users"
    )
    private var exampleName: String = "users"

    override fun run() {
        val style = when (styleName.uppercase()) {
            "ASCII" -> VirtualTableStyle.ASCII
            "MINIMAL" -> VirtualTableStyle.MINIMAL
            "COMPACT" -> VirtualTableStyle.COMPACT
            else -> VirtualTableStyle.DEFAULT
        }

        when (exampleName.lowercase()) {
            "users" -> runUsersExample(style)
            "products" -> runProductsExample(style)
            "logs" -> runLogsExample(style)
            else -> runUsersExample(style)
        }
    }

    private fun runUsersExample(style: VirtualTableStyle) {
        data class User(
            val id: Int,
            val name: String,
            val email: String,
            val role: String,
            val status: String
        )

        val roles = listOf("Admin", "Developer", "Designer", "Manager", "QA", "DevOps")
        val statuses = listOf("Active", "Away", "Offline")

        val users = (1..rowCount).map { i ->
            User(
                id = i,
                name = "User $i",
                email = "user$i@example.com",
                role = roles[(i - 1) % roles.size],
                status = statuses[(i - 1) % statuses.size]
            )
        }

        println("Interactive User Directory")
        println("Use arrow keys to navigate, Enter to select, 'q' to quit")
        println()

        val table = VirtualTable.builder<User>()
            .withData(users)
            .addColumn("ID", width = 6, align = Alignment.RIGHT) { it.id }
            .addColumn("Name", width = 20) { it.name }
            .addColumn("Email", width = 30) { it.email }
            .addColumn("Role", width = 12) { it.role }
            .addColumn("Status", width = 10, align = Alignment.CENTER) { it.status }
            .withHeight(height)
            .withStyle(style)
            .onSelect { user ->
                // This will be called when user presses Enter
                // For now, we'll just track it internally
            }
            .build()

        table.use { t ->
            t.start()

            // After exiting, show selected user
            val selected = t.getCurrentSelection()
            if (selected != null) {
                println("\nYou were viewing: ${selected.name} (${selected.email})")
            }
        }
    }

    private fun runProductsExample(style: VirtualTableStyle) {
        data class Product(
            val id: Int,
            val name: String,
            val category: String,
            val price: Double,
            val stock: Int
        )

        val categories = listOf("Electronics", "Accessories", "Office", "Furniture")

        val products = (1..rowCount).map { i ->
            Product(
                id = i,
                name = "Product $i",
                category = categories[(i - 1) % categories.size],
                price = (10..1000).random() + (0..99).random() / 100.0,
                stock = (0..500).random()
            )
        }

        println("Interactive Product Inventory")
        println("Use arrow keys to navigate, Enter to select, 'q' to quit")
        println()

        val table = VirtualTable.builder<Product>()
            .withData(products)
            .addColumn("ID", width = 6, align = Alignment.RIGHT) { it.id }
            .addColumn("Product", width = 25) { it.name }
            .addColumn("Category", width = 15) { it.category }
            .addColumn("Price", width = 12, align = Alignment.RIGHT) {
                "$${String.format("%.2f", it.price)}"
            }
            .addColumn("Stock", width = 8, align = Alignment.RIGHT) { it.stock }
            .withHeight(height)
            .withStyle(style)
            .build()

        table.use { it.start() }
    }

    private fun runLogsExample(style: VirtualTableStyle) {
        data class LogEntry(
            val id: Int,
            val timestamp: String,
            val level: String,
            val message: String
        )

        val levels = listOf("INFO", "WARN", "ERROR", "DEBUG")
        val messages = listOf(
            "Application started successfully",
            "Database connection established",
            "User authentication failed",
            "API request received",
            "Cache invalidated",
            "Background job completed",
            "Configuration reloaded",
            "Memory usage: high",
            "Network timeout occurred",
            "File upload processed"
        )

        val logs = (1..rowCount).map { i ->
            LogEntry(
                id = i,
                timestamp = "2025-01-${(i % 31) + 1} ${String.format("%02d", i % 24)}:${String.format("%02d", (i * 7) % 60)}:${String.format("%02d", (i * 13) % 60)}",
                level = levels[(i - 1) % levels.size],
                message = messages[(i - 1) % messages.size]
            )
        }

        println("Interactive Log Viewer")
        println("Use arrow keys to navigate, Enter to select, 'q' to quit")
        println()

        val table = VirtualTable.builder<LogEntry>()
            .withData(logs)
            .addColumn("#", width = 6, align = Alignment.RIGHT) { it.id }
            .addColumn("Timestamp", width = 20) { it.timestamp }
            .addColumn("Level", width = 8, align = Alignment.CENTER) { it.level }
            .addColumn("Message", width = 40) { it.message }
            .withHeight(height)
            .withStyle(style)
            .build()

        table.use { it.start() }
    }
}
