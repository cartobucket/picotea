package com.cartobucket.picocli.components

import java.io.PrintStream
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A customizable progress bar for CLI applications
 */
class ProgressBar(
    private val total: Long,
    private val label: String,
    private val fixedWidth: Int?,
    private val style: ProgressBarStyle,
    private val indeterminate: Boolean,
    private val showRate: Boolean,
    private val showEta: Boolean,
    private val showPercentage: Boolean,
    private val showCount: Boolean,
    private val unit: String,
    output: PrintStream,
    private val updateIntervalMs: Long
) : AutoCloseable {

    private val renderer = ProgressRenderer(output, autoDetectWidth = fixedWidth == null)
    private val lock = ReentrantLock()

    @Volatile
    private var current: Long = 0

    @Volatile
    private var message: String = ""

    @Volatile
    private var closed = false

    private val startTime = System.currentTimeMillis()
    private var lastUpdateTime = 0L
    private var spinnerFrame = 0

    init {
        renderer.hideCursor()
        render()
    }

    companion object {
        /**
         * Create a new ProgressBar builder
         */
        fun builder(): ProgressBarBuilder = ProgressBarBuilder()
    }

    /**
     * Increment progress by the specified amount
     */
    fun step(amount: Long = 1) {
        lock.withLock {
            if (closed) return
            current = (current + amount).coerceAtMost(total)
            maybeRender()
        }
    }

    /**
     * Set progress to an absolute value
     */
    fun stepTo(value: Long) {
        lock.withLock {
            if (closed) return
            current = value.coerceIn(0, total)
            maybeRender()
        }
    }

    /**
     * Update the status message
     */
    fun message(text: String) {
        lock.withLock {
            if (closed) return
            message = text
            maybeRender()
        }
    }

    /**
     * Mark the progress as complete
     */
    fun finish() {
        lock.withLock {
            if (closed) return
            if (!indeterminate) {
                current = total
            }
            render(force = true)
            renderer.finish()
            renderer.showCursor()
            closed = true
        }
    }

    /**
     * Render the progress bar (with rate limiting)
     */
    private fun maybeRender() {
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime >= updateIntervalMs) {
            render(force = true)
        }
    }

    /**
     * Force render the progress bar
     */
    private fun render(force: Boolean = false) {
        if (closed) return

        val now = System.currentTimeMillis()
        if (!force && now - lastUpdateTime < updateIntervalMs) {
            return
        }

        lastUpdateTime = now
        if (indeterminate) {
            spinnerFrame++
        }

        val line = buildProgressLine()
        renderer.render(line)
    }

    /**
     * Build the complete progress line
     */
    private fun buildProgressLine(): String {
        val terminalWidth = fixedWidth ?: renderer.getTerminalWidth()

        // Build suffix (percentage, count, rate, eta, message)
        val suffix = buildSuffix()

        // Calculate available space for the bar
        val barWidth = renderer.calculateBarWidth(
            terminalWidth = terminalWidth,
            label = label,
            suffixLength = suffix.length,
            bracketLength = style.leftBracket.length + style.rightBracket.length
        )

        // Build the bar
        val bar = renderer.buildBar(
            current = current,
            total = total,
            barWidth = barWidth,
            style = style,
            indeterminate = indeterminate,
            spinnerFrame = spinnerFrame
        )

        // Combine everything
        return buildString {
            if (label.isNotEmpty()) {
                append(label)
                append(" ")
            }
            append(bar)
            if (suffix.isNotEmpty()) {
                append(" ")
                append(suffix)
            }
        }
    }

    /**
     * Build the suffix portion (stats, message, etc.)
     */
    private fun buildSuffix(): String {
        val parts = mutableListOf<String>()

        // Percentage
        if (showPercentage && !indeterminate) {
            val percentage = if (total > 0) (current * 100 / total) else 0
            parts.add(String.format("%3d%%", percentage))
        }

        // Count
        if (showCount && !indeterminate) {
            parts.add("$current/$total")
        }

        // Rate
        if (showRate) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            if (elapsed > 0) {
                val rate = current / elapsed
                parts.add(renderer.formatRate(rate, unit))
            }
        }

        // ETA
        if (showEta && !indeterminate && current > 0) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            val rate = current / elapsed
            if (rate > 0) {
                val remaining = ((total - current) / rate).toLong()
                parts.add("ETA: ${renderer.formatDuration(remaining)}")
            }
        }

        // Message
        if (message.isNotEmpty()) {
            parts.add(message)
        }

        return parts.joinToString(" | ")
    }

    /**
     * Clean up resources
     */
    override fun close() {
        finish()
    }
}
