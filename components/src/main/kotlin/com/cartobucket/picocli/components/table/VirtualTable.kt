package com.cartobucket.picocli.components.table

import com.cartobucket.picocli.components.input.InputHandler
import com.cartobucket.picocli.components.input.KeyEvent
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * An interactive, scrollable table component for navigating large datasets
 */
class VirtualTable<T>(
    private val dataSource: DataSource<T>,
    private val columns: List<Table.Column<T>>,
    private val style: VirtualTableStyle,
    private val height: Int,
    private val output: PrintStream,
    private val onSelect: ((T) -> Unit)?
) : AutoCloseable {

    private val state: VirtualTableState<T> = VirtualTableState(dataSource, height)
    private val renderer: VirtualTableRenderer<T> = VirtualTableRenderer(columns, style, output)
    private val inputHandler: InputHandler = InputHandler()
    private val lock = ReentrantLock()

    @Volatile
    private var running = false

    @Volatile
    private var needsRender = true

    companion object {
        /**
         * Create a new VirtualTable builder
         */
        fun <T> builder(): VirtualTableBuilder<T> = VirtualTableBuilder()
    }

    /**
     * Start the interactive table (blocking)
     * This will take over the terminal until the user exits
     */
    fun start() {
        lock.withLock {
            if (running) {
                throw IllegalStateException("VirtualTable is already running")
            }
            running = true
        }

        try {
            // Hide cursor and clear screen
            renderer.hideCursor()
            renderer.clearScreen()

            // Initial render
            lock.withLock {
                renderer.renderFrame(state)
                needsRender = false
            }

            // Start input handling
            inputHandler.start { keyEvent ->
                handleKeyEvent(keyEvent)
            }

            // Render loop
            while (running) {
                lock.withLock {
                    if (needsRender) {
                        renderer.renderFrame(state)
                        needsRender = false
                    }
                }

                Thread.sleep(50) // 20 FPS
            }
        } finally {
            cleanup()
        }
    }

    /**
     * Stop the interactive table
     */
    fun stop() {
        lock.withLock {
            running = false
        }
    }

    /**
     * Handle keyboard input
     */
    private fun handleKeyEvent(event: KeyEvent) {
        lock.withLock {
            when (event) {
                is KeyEvent.UP -> {
                    state.moveUp()
                    needsRender = true
                }
                is KeyEvent.DOWN -> {
                    state.moveDown()
                    needsRender = true
                }
                is KeyEvent.PAGE_UP -> {
                    state.movePageUp()
                    needsRender = true
                }
                is KeyEvent.PAGE_DOWN -> {
                    state.movePageDown()
                    needsRender = true
                }
                is KeyEvent.HOME -> {
                    state.moveHome()
                    needsRender = true
                }
                is KeyEvent.END -> {
                    state.moveEnd()
                    needsRender = true
                }
                is KeyEvent.ENTER -> {
                    // Trigger selection callback
                    state.getCurrentSelection()?.let { selectedRow ->
                        onSelect?.invoke(selectedRow)
                    }
                }
                is KeyEvent.Char -> {
                    when (event.char) {
                        'q', 'Q' -> {
                            running = false
                        }
                        ' ' -> {
                            // Toggle selection (for future multi-select)
                            state.toggleSelection()
                            needsRender = true
                        }
                    }
                }
                is KeyEvent.ESCAPE -> {
                    running = false
                }
                else -> {
                    // Ignore other keys
                }
            }
        }
    }

    /**
     * Get the currently highlighted row
     */
    fun getCurrentSelection(): T? {
        return lock.withLock {
            state.getCurrentSelection()
        }
    }

    /**
     * Get all selected rows
     */
    fun getSelectedRows(): List<T> {
        return lock.withLock {
            state.getSelectedRows()
        }
    }

    /**
     * Force a re-render
     */
    fun refresh() {
        lock.withLock {
            needsRender = true
        }
    }

    /**
     * Cleanup resources
     */
    private fun cleanup() {
        inputHandler.stop()
        renderer.showCursor()
        renderer.clearScreen()
    }

    /**
     * Clean up resources
     */
    override fun close() {
        stop()
        cleanup()
    }
}
