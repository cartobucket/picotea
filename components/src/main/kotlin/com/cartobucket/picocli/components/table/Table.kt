package com.cartobucket.picocli.components.table

import com.cartobucket.picocli.components.ProgressRenderer
import java.io.PrintStream
import kotlin.math.max
import kotlin.math.min

/**
 * Text alignment options for table columns
 */
enum class Alignment {
    LEFT, CENTER, RIGHT
}

/**
 * A customizable table component for CLI applications
 */
class Table<T>(
    private val data: List<T>,
    private val columns: List<Column<T>>,
    private val style: TableStyle,
    private val fixedWidth: Int?,
    private val output: PrintStream,
    private val truncateOverflow: Boolean
) : AutoCloseable {

    private val renderer = ProgressRenderer(output, autoDetectWidth = fixedWidth == null)

    /**
     * Column definition
     */
    data class Column<T>(
        val header: String,
        val accessor: (T) -> Any?,
        val width: Int? = null,
        val align: Alignment = Alignment.LEFT,
        val truncate: Boolean = true
    )

    companion object {
        /**
         * Create a new Table builder
         */
        fun <T> builder(): TableBuilder<T> = TableBuilder()

        private const val MIN_COLUMN_WIDTH = 3
        private const val ELLIPSIS = "..."
    }

    /**
     * Render the table to the output stream
     */
    fun render() {
        val terminalWidth = fixedWidth ?: renderer.getTerminalWidth()
        val columnWidths = calculateColumnWidths(terminalWidth)

        if (style.showBorders) {
            renderTopBorder(columnWidths)
        }

        renderHeader(columnWidths)

        if (style.showHeaderSeparator) {
            renderHeaderSeparator(columnWidths)
        }

        data.forEach { row ->
            renderRow(row, columnWidths)
        }

        if (style.showBorders) {
            renderBottomBorder(columnWidths)
        }

        output.flush()
    }

    /**
     * Calculate the width for each column based on content and constraints
     */
    private fun calculateColumnWidths(terminalWidth: Int): List<Int> {
        val borderOverhead = if (style.showBorders) {
            // Left border + right border + (n-1) internal borders
            2 + (columns.size - 1)
        } else {
            // Just spacing between columns
            columns.size - 1
        }

        val paddingOverhead = columns.size * (style.padding * 2)
        val availableWidth = terminalWidth - borderOverhead - paddingOverhead

        // Start with fixed-width columns
        val widths = columns.map { column ->
            column.width ?: 0
        }.toMutableList()

        val fixedTotal = widths.sum()
        var remainingWidth = availableWidth - fixedTotal

        // Calculate minimum widths based on headers
        columns.forEachIndexed { index, column ->
            if (column.width == null) {
                val headerWidth = column.header.length
                val contentWidth = data.maxOfOrNull { row ->
                    val value = column.accessor(row)
                    value?.toString()?.length ?: 0
                } ?: 0
                val minWidth = max(headerWidth, contentWidth)
                widths[index] = max(MIN_COLUMN_WIDTH, minWidth)
            }
        }

        // If we have fixed widths, distribute remaining space to auto-sized columns
        val autoSizedIndices = columns.indices.filter { columns[it].width == null }

        if (autoSizedIndices.isNotEmpty() && truncateOverflow) {
            val totalAutoWidth = autoSizedIndices.sumOf { widths[it] }

            if (totalAutoWidth > remainingWidth) {
                // Need to shrink columns proportionally
                val ratio = remainingWidth.toDouble() / totalAutoWidth
                autoSizedIndices.forEach { index ->
                    widths[index] = max(MIN_COLUMN_WIDTH, (widths[index] * ratio).toInt())
                }
            }
        }

        return widths.map { max(MIN_COLUMN_WIDTH, it) }
    }

    /**
     * Render the top border
     */
    private fun renderTopBorder(columnWidths: List<Int>) {
        val b = style.borderChars
        val line = buildString {
            append(b.topLeft)
            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.topCross)
                }
            }
            append(b.topRight)
        }
        output.println(line)
    }

    /**
     * Render the header row
     */
    private fun renderHeader(columnWidths: List<Int>) {
        val line = buildString {
            if (style.showBorders) {
                append(style.borderChars.vertical)
            }

            columns.forEachIndexed { index, column ->
                val paddedContent = padCell(column.header, columnWidths[index], Alignment.CENTER)
                append(paddedContent)

                if (index < columns.size - 1) {
                    append(style.borderChars.vertical)
                } else if (style.showBorders) {
                    append(style.borderChars.vertical)
                }
            }
        }
        output.println(line)
    }

    /**
     * Render the separator between header and data
     */
    private fun renderHeaderSeparator(columnWidths: List<Int>) {
        val b = style.borderChars
        val line = buildString {
            if (style.showBorders) {
                append(b.headerLeft)
            }

            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.headerCross)
                }
            }

            if (style.showBorders) {
                append(b.headerRight)
            }
        }
        output.println(line)
    }

    /**
     * Render a data row
     */
    private fun renderRow(row: T, columnWidths: List<Int>) {
        val line = buildString {
            if (style.showBorders) {
                append(style.borderChars.vertical)
            }

            columns.forEachIndexed { index, column ->
                val value = column.accessor(row)?.toString() ?: ""
                val truncated = if (column.truncate && value.length > columnWidths[index]) {
                    value.take(columnWidths[index] - ELLIPSIS.length) + ELLIPSIS
                } else {
                    value
                }
                val paddedContent = padCell(truncated, columnWidths[index], column.align)
                append(paddedContent)

                if (index < columns.size - 1) {
                    append(style.borderChars.vertical)
                } else if (style.showBorders) {
                    append(style.borderChars.vertical)
                }
            }
        }
        output.println(line)
    }

    /**
     * Render the bottom border
     */
    private fun renderBottomBorder(columnWidths: List<Int>) {
        val b = style.borderChars
        val line = buildString {
            append(b.bottomLeft)
            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.bottomCross)
                }
            }
            append(b.bottomRight)
        }
        output.println(line)
    }

    /**
     * Pad a cell with the appropriate spacing and alignment
     */
    private fun padCell(content: String, width: Int, alignment: Alignment): String {
        val actualContent = if (content.length > width) {
            content.take(width)
        } else {
            content
        }

        val totalPadding = width - actualContent.length
        val padding = " ".repeat(style.padding)

        return when (alignment) {
            Alignment.LEFT -> {
                padding + actualContent + " ".repeat(totalPadding) + padding
            }
            Alignment.RIGHT -> {
                padding + " ".repeat(totalPadding) + actualContent + padding
            }
            Alignment.CENTER -> {
                val leftPad = totalPadding / 2
                val rightPad = totalPadding - leftPad
                padding + " ".repeat(leftPad) + actualContent + " ".repeat(rightPad) + padding
            }
        }
    }

    /**
     * Clean up resources
     */
    override fun close() {
        // Nothing to clean up for tables (unlike progress bars)
    }
}
