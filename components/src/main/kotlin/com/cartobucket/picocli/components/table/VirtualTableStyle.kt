package com.cartobucket.picocli.components.table

/**
 * Style configuration for selection and highlighting in VirtualTable
 */
data class SelectionStyle(
    val backgroundColor: String = "",
    val foregroundColor: String = "",
    val bold: Boolean = false,
    val reverse: Boolean = false
) {
    /**
     * Apply styling to text
     */
    fun apply(text: String): String {
        if (backgroundColor.isEmpty() && foregroundColor.isEmpty() && !bold && !reverse) {
            return text
        }

        val codes = mutableListOf<String>()
        if (backgroundColor.isNotEmpty()) codes.add(backgroundColor)
        if (foregroundColor.isNotEmpty()) codes.add(foregroundColor)
        if (bold) codes.add("\u001b[1m")
        if (reverse) codes.add("\u001b[7m")

        val prefix = codes.joinToString("")
        val suffix = "\u001b[0m" // Reset

        return "$prefix$text$suffix"
    }

    companion object {
        /**
         * No styling
         */
        val NONE = SelectionStyle()

        /**
         * Default highlight style (reverse video)
         */
        val HIGHLIGHT = SelectionStyle(
            reverse = true
        )

        /**
         * Default selection style (bold with blue background)
         */
        val SELECTION = SelectionStyle(
            backgroundColor = "\u001b[48;5;33m",  // Blue background
            foregroundColor = "\u001b[97m",       // White text
            bold = false
        )

        /**
         * Header style (bold with dark gray background)
         */
        val HEADER = SelectionStyle(
            backgroundColor = "\u001b[48;5;238m", // Dark gray
            foregroundColor = "\u001b[96m",       // Cyan
            bold = true
        )

        /**
         * Footer/status line style
         */
        val FOOTER = SelectionStyle(
            backgroundColor = "\u001b[48;5;236m", // Darker gray
            foregroundColor = "\u001b[37m",       // Light gray
            bold = false
        )
    }
}

/**
 * Complete style configuration for VirtualTable
 */
data class VirtualTableStyle(
    val baseStyle: TableStyle = TableStyle.UNICODE,
    val highlightStyle: SelectionStyle = SelectionStyle.HIGHLIGHT,
    val selectionStyle: SelectionStyle = SelectionStyle.SELECTION,
    val headerStyle: SelectionStyle = SelectionStyle.HEADER,
    val footerStyle: SelectionStyle = SelectionStyle.FOOTER,
    val showFooter: Boolean = true,
    val showKeyHints: Boolean = true,
    val showScrollIndicator: Boolean = true
) {
    companion object {
        /**
         * Default Unicode style with highlighting
         */
        val DEFAULT = VirtualTableStyle()

        /**
         * ASCII style for compatibility
         */
        val ASCII = VirtualTableStyle(
            baseStyle = TableStyle.ASCII
        )

        /**
         * Minimal style with no borders
         */
        val MINIMAL = VirtualTableStyle(
            baseStyle = TableStyle.MINIMAL,
            showFooter = false,
            showKeyHints = false,
            showScrollIndicator = false
        )

        /**
         * Compact style with minimal spacing
         */
        val COMPACT = VirtualTableStyle(
            baseStyle = TableStyle.COMPACT,
            showKeyHints = false
        )
    }
}
