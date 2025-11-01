package com.cartobucket.picocli.components

/**
 * Defines the visual style of a progress bar
 */
data class ProgressBarStyle(
    val leftBracket: String = "[",
    val rightBracket: String = "]",
    val filledChar: String = "█",
    val unfilledChar: String = "░",
    val indeterminateChars: List<String> = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
) {
    companion object {
        /**
         * Unicode block characters for smooth progress bars
         */
        val UNICODE = ProgressBarStyle(
            leftBracket = "[",
            rightBracket = "]",
            filledChar = "█",
            unfilledChar = "░"
        )

        /**
         * ASCII-only style for maximum compatibility
         */
        val ASCII = ProgressBarStyle(
            leftBracket = "[",
            rightBracket = "]",
            filledChar = "#",
            unfilledChar = "-",
            indeterminateChars = listOf("|", "/", "-", "\\")
        )

        /**
         * Minimal style with simple equals signs
         */
        val MINIMAL = ProgressBarStyle(
            leftBracket = "",
            rightBracket = "",
            filledChar = "=",
            unfilledChar = " "
        )

        /**
         * Dots style
         */
        val DOTS = ProgressBarStyle(
            leftBracket = "[",
            rightBracket = "]",
            filledChar = "●",
            unfilledChar = "○"
        )
    }
}
