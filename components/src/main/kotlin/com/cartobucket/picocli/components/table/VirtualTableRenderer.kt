package com.cartobucket.picocli.components.table

import com.cartobucket.picocli.components.ProgressRenderer
import java.io.PrintStream
import kotlin.math.max

/**
 * Renders a VirtualTable with only visible rows
 */
class VirtualTableRenderer<T>(
    private val columns: List<Table.Column<T>>,
    private val style: VirtualTableStyle,
    private val output: PrintStream
) {
    private val progressRenderer = ProgressRenderer(output, autoDetectWidth = true)
    private val columnWidths: List<Int>

    init {
        // Calculate column widths once at initialization
        columnWidths = calculateColumnWidths()
    }

    /**
     * Render the complete virtual table frame
     */
    fun renderFrame(state: VirtualTableState<T>) {
        val frame = buildFrame(state)

        // Clear screen and move cursor to home
        output.print("\u001b[2J\u001b[H")
        output.print(frame)
        output.flush()
    }

    /**
     * Build the complete frame as a string
     */
    private fun buildFrame(state: VirtualTableState<T>): String {
        return buildString {
            // Top border
            append(renderTopBorder())
            append("\n")

            // Header
            append(renderHeader())
            append("\n")

            // Header separator
            append(renderHeaderSeparator())
            append("\n")

            // Visible rows
            val visibleRows = state.getVisibleRows()
            val visibleRange = state.getVisibleRange()

            visibleRows.forEachIndexed { index, row ->
                val globalIndex = visibleRange.first + index
                val isHighlighted = state.isHighlighted(globalIndex)
                val isSelected = state.isSelected(globalIndex)

                append(renderRow(row, isHighlighted, isSelected))
                append("\n")
            }

            // Fill empty rows if needed
            val emptyRows = state.visibleHeight - visibleRows.size
            repeat(emptyRows) {
                append(renderEmptyRow())
                append("\n")
            }

            // Bottom border
            append(renderBottomBorder())
            append("\n")

            // Footer/status line
            if (style.showFooter) {
                append(renderFooter(state))
                append("\n")
            }
        }
    }

    /**
     * Calculate column widths based on headers and available space
     */
    private fun calculateColumnWidths(): List<Int> {
        val terminalWidth = progressRenderer.getTerminalWidth()
        val b = style.baseStyle.borderChars

        val borderOverhead = if (style.baseStyle.showBorders) {
            b.vertical.length * (columns.size + 1)
        } else {
            columns.size - 1
        }

        val paddingOverhead = columns.size * (style.baseStyle.padding * 2)
        val availableWidth = terminalWidth - borderOverhead - paddingOverhead - 10 // Reserve some space

        // Use fixed widths if specified, otherwise distribute evenly
        val widths = columns.map { column ->
            column.width ?: (availableWidth / columns.size).coerceAtLeast(10)
        }

        return widths
    }

    /**
     * Render top border
     */
    private fun renderTopBorder(): String {
        val b = style.baseStyle.borderChars
        return buildString {
            append(b.topLeft)
            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.baseStyle.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.topCross)
                }
            }
            append(b.topRight)
        }
    }

    /**
     * Render header row
     */
    private fun renderHeader(): String {
        val b = style.baseStyle.borderChars
        val line = buildString {
            if (style.baseStyle.showBorders) {
                append(b.vertical)
            }

            columns.forEachIndexed { index, column ->
                val paddedContent = padCell(column.header, columnWidths[index], Alignment.CENTER)
                val styledContent = style.headerStyle.apply(paddedContent)
                append(styledContent)

                if (index < columns.size - 1) {
                    append(b.vertical)
                } else if (style.baseStyle.showBorders) {
                    append(b.vertical)
                }
            }
        }
        return line
    }

    /**
     * Render header separator
     */
    private fun renderHeaderSeparator(): String {
        val b = style.baseStyle.borderChars
        return buildString {
            if (style.baseStyle.showBorders) {
                append(b.headerLeft)
            }

            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.baseStyle.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.headerCross)
                }
            }

            if (style.baseStyle.showBorders) {
                append(b.headerRight)
            }
        }
    }

    /**
     * Render a data row
     */
    private fun renderRow(row: T, isHighlighted: Boolean, isSelected: Boolean): String {
        val b = style.baseStyle.borderChars
        return buildString {
            if (style.baseStyle.showBorders) {
                append(b.vertical)
            }

            columns.forEachIndexed { index, column ->
                val value = column.accessor(row)?.toString() ?: ""
                val truncated = if (column.truncate && value.length > columnWidths[index]) {
                    value.take(columnWidths[index] - 3) + "..."
                } else {
                    value
                }

                val paddedContent = padCell(truncated, columnWidths[index], column.align)

                // Apply styling
                val styledContent = when {
                    isHighlighted -> style.highlightStyle.apply(paddedContent)
                    isSelected -> style.selectionStyle.apply(paddedContent)
                    else -> paddedContent
                }

                append(styledContent)

                if (index < columns.size - 1) {
                    append(b.vertical)
                } else if (style.baseStyle.showBorders) {
                    append(b.vertical)
                }
            }
        }
    }

    /**
     * Render an empty row (for padding)
     */
    private fun renderEmptyRow(): String {
        val b = style.baseStyle.borderChars
        return buildString {
            if (style.baseStyle.showBorders) {
                append(b.vertical)
            }

            columnWidths.forEachIndexed { index, width ->
                val paddedContent = padCell("", width, Alignment.LEFT)
                append(paddedContent)

                if (index < columnWidths.size - 1) {
                    append(b.vertical)
                } else if (style.baseStyle.showBorders) {
                    append(b.vertical)
                }
            }
        }
    }

    /**
     * Render bottom border
     */
    private fun renderBottomBorder(): String {
        val b = style.baseStyle.borderChars
        return buildString {
            append(b.bottomLeft)
            columnWidths.forEachIndexed { index, width ->
                repeat(width + style.baseStyle.padding * 2) { append(b.horizontal) }
                if (index < columnWidths.size - 1) {
                    append(b.bottomCross)
                }
            }
            append(b.bottomRight)
        }
    }

    /**
     * Render footer/status line
     */
    private fun renderFooter(state: VirtualTableState<T>): String {
        val parts = mutableListOf<String>()

        // Row position
        if (state.totalRows > 0) {
            parts.add("Row ${state.currentRowIndex + 1} of ${state.totalRows}")
        } else {
            parts.add("No data")
        }

        // Selection count
        val selectedCount = state.getSelectedRows().size
        if (selectedCount > 0) {
            parts.add("$selectedCount selected")
        }

        // Key hints
        if (style.showKeyHints) {
            parts.add("↑↓:Navigate  q:Quit")
        }

        val footer = parts.joinToString(" | ")
        return style.footerStyle.apply(" $footer ")
    }

    /**
     * Pad a cell with appropriate spacing and alignment
     */
    private fun padCell(content: String, width: Int, alignment: Alignment): String {
        val actualContent = if (content.length > width) {
            content.take(width)
        } else {
            content
        }

        val totalPadding = width - actualContent.length
        val padding = " ".repeat(style.baseStyle.padding)

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
     * Hide cursor
     */
    fun hideCursor() {
        progressRenderer.hideCursor()
    }

    /**
     * Show cursor
     */
    fun showCursor() {
        progressRenderer.showCursor()
    }

    /**
     * Clear screen
     */
    fun clearScreen() {
        output.print("\u001b[2J\u001b[H")
        output.flush()
    }
}
