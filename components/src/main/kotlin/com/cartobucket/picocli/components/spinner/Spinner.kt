package com.cartobucket.picocli.components.spinner

import com.cartobucket.picocli.components.ProgressRenderer
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A customizable spinner for CLI applications to indicate ongoing operations
 */
class Spinner(
    private val label: String,
    private val style: SpinnerStyle,
    output: PrintStream,
    private val updateIntervalMs: Long
) : AutoCloseable {

    private val renderer = ProgressRenderer(output, autoDetectWidth = false)
    private val lock = ReentrantLock()

    @Volatile
    private var message: String = ""

    @Volatile
    private var closed = false

    private var frame = 0
    private var lastUpdateTime = 0L
    private val startTime = System.currentTimeMillis()

    init {
        renderer.hideCursor()
        render()
    }

    companion object {
        /**
         * Create a new Spinner builder
         */
        fun builder(): SpinnerBuilder = SpinnerBuilder()
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
     * Manually update the spinner (useful for long-running operations without automatic updates)
     */
    fun update() {
        lock.withLock {
            if (closed) return
            maybeRender()
        }
    }

    /**
     * Stop the spinner and clean up
     */
    fun finish(finalMessage: String = "") {
        lock.withLock {
            if (closed) return
            if (finalMessage.isNotEmpty()) {
                message = finalMessage
            }
            render(force = true)
            renderer.finish()
            renderer.showCursor()
            closed = true
        }
    }

    /**
     * Render the spinner (with rate limiting)
     */
    private fun maybeRender() {
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime >= updateIntervalMs) {
            render(force = true)
        }
    }

    /**
     * Force render the spinner
     */
    private fun render(force: Boolean = false) {
        if (closed) return

        val now = System.currentTimeMillis()
        if (!force && now - lastUpdateTime < updateIntervalMs) {
            return
        }

        lastUpdateTime = now
        frame++

        val line = buildSpinnerLine()
        renderer.render(line)
    }

    /**
     * Build the complete spinner line
     */
    private fun buildSpinnerLine(): String {
        val currentFrame = style.frames[frame % style.frames.size]

        return buildString {
            append(currentFrame)
            if (label.isNotEmpty()) {
                append(" ")
                append(label)
            }
            if (message.isNotEmpty()) {
                append(" ")
                append(message)
            }
        }
    }

    /**
     * Get the elapsed time since spinner started
     */
    fun getElapsedSeconds(): Long {
        return (System.currentTimeMillis() - startTime) / 1000
    }

    /**
     * Clean up resources
     */
    override fun close() {
        finish()
    }
}
