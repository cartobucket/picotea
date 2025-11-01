package com.cartobucket.picocli.components

import java.io.PrintStream
import kotlin.math.max

/**
 * Handles terminal output and rendering for progress bars
 */
class ProgressRenderer(
    private val output: PrintStream = System.err,
    private val autoDetectWidth: Boolean = true
) {
    private var lastLineLength = 0
    private var isInteractive = System.console() != null && System.getenv("TERM") != null

    companion object {
        private const val DEFAULT_WIDTH = 80
        private const val MIN_BAR_WIDTH = 10
        private const val ANSI_CLEAR_LINE = "\r\u001b[K"
        private const val ANSI_HIDE_CURSOR = "\u001b[?25l"
        private const val ANSI_SHOW_CURSOR = "\u001b[?25h"
    }

    /**
     * Get the terminal width, or default if unavailable
     */
    fun getTerminalWidth(): Int {
        if (!autoDetectWidth) return DEFAULT_WIDTH

        return try {
            // Try to get terminal width using tput
            val process = ProcessBuilder("tput", "cols").start()
            val result = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            result.toIntOrNull() ?: DEFAULT_WIDTH
        } catch (e: Exception) {
            DEFAULT_WIDTH
        }
    }

    /**
     * Calculate the width available for the actual progress bar
     */
    fun calculateBarWidth(
        terminalWidth: Int,
        label: String,
        suffixLength: Int,
        bracketLength: Int
    ): Int {
        val reservedSpace = label.length + suffixLength + bracketLength + 2 // spaces
        return max(MIN_BAR_WIDTH, terminalWidth - reservedSpace)
    }

    /**
     * Render a progress bar line
     */
    fun render(line: String) {
        if (!isInteractive) {
            // In non-interactive mode, just print newlines
            output.println(line)
            return
        }

        // Clear current line and print new content
        output.print(ANSI_CLEAR_LINE)
        output.print(line)
        output.flush()
        lastLineLength = line.length
    }

    /**
     * Build the visual progress bar
     */
    fun buildBar(
        current: Long,
        total: Long,
        barWidth: Int,
        style: ProgressBarStyle,
        indeterminate: Boolean = false,
        spinnerFrame: Int = 0
    ): String {
        if (indeterminate) {
            return buildIndeterminateBar(barWidth, style, spinnerFrame)
        }

        val ratio = if (total > 0) current.toDouble() / total.toDouble() else 0.0
        val filledWidth = (barWidth * ratio).toInt().coerceIn(0, barWidth)
        val unfilledWidth = barWidth - filledWidth

        return buildString {
            append(style.leftBracket)
            repeat(filledWidth) { append(style.filledChar) }
            repeat(unfilledWidth) { append(style.unfilledChar) }
            append(style.rightBracket)
        }
    }

    /**
     * Build an indeterminate progress bar (spinner style)
     */
    private fun buildIndeterminateBar(barWidth: Int, style: ProgressBarStyle, frame: Int): String {
        val spinnerChar = style.indeterminateChars[frame % style.indeterminateChars.size]
        val position = frame % (barWidth + 1)

        return buildString {
            append(style.leftBracket)
            repeat(barWidth) { i ->
                if (i == position) {
                    append(spinnerChar)
                } else {
                    append(style.unfilledChar)
                }
            }
            append(style.rightBracket)
        }
    }

    /**
     * Format time duration in human-readable format
     */
    fun formatDuration(seconds: Long): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> {
                val mins = seconds / 60
                val secs = seconds % 60
                "${mins}m ${secs}s"
            }
            else -> {
                val hours = seconds / 3600
                val mins = (seconds % 3600) / 60
                "${hours}h ${mins}m"
            }
        }
    }

    /**
     * Format rate (items per second)
     */
    fun formatRate(itemsPerSecond: Double, unit: String = "items"): String {
        return when {
            itemsPerSecond < 1.0 -> String.format("%.2f %s/s", itemsPerSecond, unit)
            itemsPerSecond < 1000.0 -> String.format("%.1f %s/s", itemsPerSecond, unit)
            else -> String.format("%.0f %s/s", itemsPerSecond, unit)
        }
    }

    /**
     * Hide the cursor (for cleaner rendering)
     */
    fun hideCursor() {
        if (isInteractive) {
            output.print(ANSI_HIDE_CURSOR)
            output.flush()
        }
    }

    /**
     * Show the cursor (cleanup)
     */
    fun showCursor() {
        if (isInteractive) {
            output.print(ANSI_SHOW_CURSOR)
            output.flush()
        }
    }

    /**
     * Clear the current line
     */
    fun clear() {
        if (isInteractive) {
            output.print(ANSI_CLEAR_LINE)
            output.flush()
        }
    }

    /**
     * Print a newline (finish rendering)
     */
    fun finish() {
        if (isInteractive) {
            output.println()
        }
    }
}
