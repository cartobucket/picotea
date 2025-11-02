package com.cartobucket.picocli.components.input

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * High-level input handler that manages terminal raw mode and provides callback-based API
 */
class InputHandler(
    private val keyboardInput: KeyboardInput = KeyboardInput()
) : AutoCloseable {
    private val running = AtomicBoolean(false)
    private var inputThread: Thread? = null
    private var callback: ((KeyEvent) -> Unit)? = null

    init {
        // Add shutdown hook to ensure terminal is restored
        Runtime.getRuntime().addShutdownHook(Thread {
            stop()
        })
    }

    /**
     * Start reading input and call the callback for each key event
     */
    fun start(onKey: (KeyEvent) -> Unit) {
        if (running.get()) {
            throw IllegalStateException("InputHandler is already running")
        }

        this.callback = onKey

        if (!keyboardInput.enableRawMode()) {
            throw IllegalStateException("Failed to enable raw terminal mode")
        }

        running.set(true)
        inputThread = thread(name = "InputHandler", isDaemon = false) {
            runInputLoop()
        }
    }

    /**
     * Stop reading input and restore terminal
     */
    fun stop() {
        if (!running.get()) return

        running.set(false)
        inputThread?.interrupt()
        inputThread?.join(1000) // Wait up to 1 second
        keyboardInput.disableRawMode()
    }

    /**
     * Input reading loop
     */
    private fun runInputLoop() {
        while (running.get() && !Thread.currentThread().isInterrupted) {
            try {
                val key = keyboardInput.readKey()
                if (key != null && key != KeyEvent.Unknown) {
                    callback?.invoke(key)
                }

                // Small sleep to avoid busy-waiting
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                break
            } catch (e: Exception) {
                // Log error but continue
                System.err.println("Input error: ${e.message}")
            }
        }
    }

    /**
     * Check if the input handler is currently running
     */
    fun isRunning(): Boolean = running.get()

    /**
     * Clean up resources
     */
    override fun close() {
        stop()
    }
}
