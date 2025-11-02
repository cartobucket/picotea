package com.cartobucket.picocli.components.input

import org.jline.terminal.Attributes
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Low-level keyboard input handler using JLine3 for proper terminal control
 */
class KeyboardInput {
    private var terminal: Terminal? = null
    private var reader: NonBlockingReader? = null
    private var originalAttributes: Attributes? = null
    private val rawModeEnabled = AtomicBoolean(false)

    companion object {
        private const val READ_TIMEOUT_MS = 100L
    }

    /**
     * Enable raw terminal mode using JLine3
     */
    fun enableRawMode(): Boolean {
        return try {
            // Build terminal with system defaults
            terminal = TerminalBuilder.builder()
                .system(true)
                .build()

            // Save original attributes
            originalAttributes = terminal?.attributes

            // Enter raw mode
            terminal?.enterRawMode()

            // Get non-blocking reader
            reader = terminal?.reader()

            rawModeEnabled.set(true)
            true
        } catch (e: Exception) {
            System.err.println("Failed to enable raw mode: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Disable raw mode and restore terminal settings
     */
    fun disableRawMode() {
        if (!rawModeEnabled.get()) return

        try {
            // Restore original attributes
            originalAttributes?.let { attrs ->
                terminal?.attributes = attrs
            }

            // Close terminal
            terminal?.close()

            rawModeEnabled.set(false)
        } catch (e: Exception) {
            System.err.println("Error restoring terminal: ${e.message}")
        } finally {
            terminal = null
            reader = null
            originalAttributes = null
        }
    }

    /**
     * Read a single key event (non-blocking with timeout)
     * Returns null if no key is available within the timeout
     */
    fun readKey(): KeyEvent? {
        if (!rawModeEnabled.get() || reader == null) {
            return null
        }

        return try {
            val c = reader!!.read(READ_TIMEOUT_MS)

            when {
                c == NonBlockingReader.READ_EXPIRED -> null // Timeout
                c == NonBlockingReader.EOF -> null          // EOF
                c == 27 -> parseEscapeSequence()            // Escape sequence
                c == 13 || c == 10 -> KeyEvent.ENTER       // Enter/Return
                c == 9 -> KeyEvent.TAB                      // Tab
                c == 127 -> KeyEvent.BACKSPACE              // Backspace
                c in 1..26 -> KeyEvent.Ctrl(('a'.code + c - 1).toChar()) // Ctrl+letter
                c in 32..126 -> KeyEvent.Char(c.toChar())  // Printable character
                else -> KeyEvent.Unknown
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse ANSI escape sequences
     */
    private fun parseEscapeSequence(): KeyEvent {
        val reader = this.reader ?: return KeyEvent.ESCAPE

        // Read next character
        val next = reader.read(READ_TIMEOUT_MS)

        return when {
            next == NonBlockingReader.READ_EXPIRED -> KeyEvent.ESCAPE
            next == '['.code -> parseCsiSequence()
            next == 'O'.code -> parseSs3Sequence()
            else -> KeyEvent.ESCAPE
        }
    }

    /**
     * Parse CSI sequences (ESC [)
     */
    private fun parseCsiSequence(): KeyEvent {
        val reader = this.reader ?: return KeyEvent.Unknown

        val third = reader.read(READ_TIMEOUT_MS)
        if (third == NonBlockingReader.READ_EXPIRED) {
            return KeyEvent.Unknown
        }

        return when (third) {
            'A'.code -> KeyEvent.UP
            'B'.code -> KeyEvent.DOWN
            'C'.code -> KeyEvent.RIGHT
            'D'.code -> KeyEvent.LEFT
            'H'.code -> KeyEvent.HOME
            'F'.code -> KeyEvent.END
            in '0'.code..'9'.code -> parseNumberedSequence(third.toChar())
            else -> KeyEvent.Unknown
        }
    }

    /**
     * Parse SS3 sequences (ESC O) - alternate arrow keys
     */
    private fun parseSs3Sequence(): KeyEvent {
        val reader = this.reader ?: return KeyEvent.Unknown

        val next = reader.read(READ_TIMEOUT_MS)
        if (next == NonBlockingReader.READ_EXPIRED) {
            return KeyEvent.Unknown
        }

        return when (next) {
            'A'.code -> KeyEvent.UP
            'B'.code -> KeyEvent.DOWN
            'C'.code -> KeyEvent.RIGHT
            'D'.code -> KeyEvent.LEFT
            'H'.code -> KeyEvent.HOME
            'F'.code -> KeyEvent.END
            else -> KeyEvent.Unknown
        }
    }

    /**
     * Parse numbered sequences ending with ~
     */
    private fun parseNumberedSequence(first: Char): KeyEvent {
        val reader = this.reader ?: return KeyEvent.Unknown
        val buffer = StringBuilder().append(first)

        // Read until we find ~  or timeout
        while (buffer.length < 5) {
            val c = reader.read(READ_TIMEOUT_MS)

            when {
                c == NonBlockingReader.READ_EXPIRED -> break
                c == '~'.code -> {
                    val num = buffer.toString().toIntOrNull()
                    return when (num) {
                        1 -> KeyEvent.HOME
                        2 -> KeyEvent.Unknown // Insert
                        3 -> KeyEvent.DELETE
                        4 -> KeyEvent.END
                        5 -> KeyEvent.PAGE_UP
                        6 -> KeyEvent.PAGE_DOWN
                        else -> KeyEvent.Unknown
                    }
                }
                c in '0'.code..'9'.code -> buffer.append(c.toChar())
                else -> break
            }
        }

        return KeyEvent.Unknown
    }

    /**
     * Check if raw mode is currently enabled
     */
    fun isRawModeEnabled(): Boolean = rawModeEnabled.get()
}
