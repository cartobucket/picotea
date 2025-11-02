package com.cartobucket.picocli.components.input

/**
 * Represents a keyboard event
 */
sealed class KeyEvent {
    // Special keys
    data object UP : KeyEvent()
    data object DOWN : KeyEvent()
    data object LEFT : KeyEvent()
    data object RIGHT : KeyEvent()
    data object PAGE_UP : KeyEvent()
    data object PAGE_DOWN : KeyEvent()
    data object HOME : KeyEvent()
    data object END : KeyEvent()
    data object ENTER : KeyEvent()
    data object ESCAPE : KeyEvent()
    data object TAB : KeyEvent()
    data object BACKSPACE : KeyEvent()
    data object DELETE : KeyEvent()

    // Character keys
    data class Char(val char: kotlin.Char) : KeyEvent()

    // Control keys
    data class Ctrl(val char: kotlin.Char) : KeyEvent()

    // Unknown/unsupported
    data object Unknown : KeyEvent()

    companion object {
        /**
         * Parse a sequence of bytes into a KeyEvent
         */
        fun parse(bytes: ByteArray): KeyEvent {
            if (bytes.isEmpty()) return Unknown

            return when {
                // Single byte characters
                bytes.size == 1 -> parseSingleByte(bytes[0])

                // ANSI escape sequences (start with ESC [)
                bytes.size >= 3 && bytes[0].toInt() == 27 && bytes[1].toInt() == '['.code -> {
                    parseEscapeSequence(bytes)
                }

                // Control sequences (Ctrl+key)
                bytes.size == 1 && bytes[0].toInt() in 1..26 -> {
                    Ctrl((bytes[0].toInt() + 'a'.code - 1).toChar())
                }

                else -> Unknown
            }
        }

        private fun parseSingleByte(byte: Byte): KeyEvent {
            return when (val code = byte.toInt()) {
                27 -> ESCAPE
                13, 10 -> ENTER
                9 -> TAB
                127 -> BACKSPACE
                in 32..126 -> Char(code.toChar())
                else -> Unknown
            }
        }

        private fun parseEscapeSequence(bytes: ByteArray): KeyEvent {
            // ESC [ X format (common arrow keys, etc.)
            if (bytes.size == 3 && bytes[1].toInt() == '['.code) {
                return when (bytes[2].toInt()) {
                    'A'.code -> UP
                    'B'.code -> DOWN
                    'C'.code -> RIGHT
                    'D'.code -> LEFT
                    'H'.code -> HOME
                    'F'.code -> END
                    else -> Unknown
                }
            }

            // ESC [ N ~ format (Page Up/Down, Delete, etc.)
            if (bytes.size >= 4 && bytes[1].toInt() == '['.code && bytes[bytes.size - 1].toInt() == '~'.code) {
                val number = String(bytes.sliceArray(2 until bytes.size - 1)).toIntOrNull()
                return when (number) {
                    1 -> HOME
                    3 -> DELETE
                    4 -> END
                    5 -> PAGE_UP
                    6 -> PAGE_DOWN
                    else -> Unknown
                }
            }

            return Unknown
        }
    }
}
