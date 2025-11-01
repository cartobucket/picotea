package com.cartobucket.picocli.components.spinner

/**
 * Defines the visual style and animation frames of a spinner
 */
data class SpinnerStyle(
    val frames: List<String>,
    val interval: Long = 80
) {
    companion object {
        /**
         * Braille dots spinner - smooth and elegant
         */
        val BRAILLE = SpinnerStyle(
            frames = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"),
            interval = 80
        )

        /**
         * Simple dots animation
         */
        val DOTS = SpinnerStyle(
            frames = listOf("⠋", "⠙", "⠚", "⠞", "⠖", "⠦", "⠴", "⠲", "⠳", "⠓"),
            interval = 80
        )

        /**
         * Line spinner - ASCII compatible
         */
        val LINE = SpinnerStyle(
            frames = listOf("-", "\\", "|", "/"),
            interval = 100
        )

        /**
         * Arc spinner
         */
        val ARC = SpinnerStyle(
            frames = listOf("◜", "◠", "◝", "◞", "◡", "◟"),
            interval = 100
        )

        /**
         * Arrow spinner
         */
        val ARROW = SpinnerStyle(
            frames = listOf("←", "↖", "↑", "↗", "→", "↘", "↓", "↙"),
            interval = 100
        )

        /**
         * Box drawing spinner
         */
        val BOX = SpinnerStyle(
            frames = listOf("▖", "▘", "▝", "▗"),
            interval = 100
        )

        /**
         * Bounce animation
         */
        val BOUNCE = SpinnerStyle(
            frames = listOf("⠁", "⠂", "⠄", "⡀", "⢀", "⠠", "⠐", "⠈"),
            interval = 80
        )

        /**
         * Circle spinner
         */
        val CIRCLE = SpinnerStyle(
            frames = listOf("◐", "◓", "◑", "◒"),
            interval = 120
        )

        /**
         * Growing dots
         */
        val GROWING_DOTS = SpinnerStyle(
            frames = listOf("⣾", "⣽", "⣻", "⢿", "⡿", "⣟", "⣯", "⣷"),
            interval = 80
        )

        /**
         * Simple ellipsis animation
         */
        val ELLIPSIS = SpinnerStyle(
            frames = listOf(".  ", ".. ", "...", " ..", "  .", "   "),
            interval = 200
        )

        /**
         * Clock spinner
         */
        val CLOCK = SpinnerStyle(
            frames = listOf("🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛"),
            interval = 100
        )

        /**
         * Simple bar spinner - most compatible
         */
        val BAR = SpinnerStyle(
            frames = listOf("[    ]", "[=   ]", "[==  ]", "[=== ]", "[ ===]", "[  ==]", "[   =]", "[    ]"),
            interval = 100
        )
    }
}
