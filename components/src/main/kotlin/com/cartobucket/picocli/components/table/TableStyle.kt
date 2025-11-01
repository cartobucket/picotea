package com.cartobucket.picocli.components.table

/**
 * Border characters for table rendering
 */
data class BorderChars(
    val topLeft: String,
    val topRight: String,
    val bottomLeft: String,
    val bottomRight: String,
    val horizontal: String,
    val vertical: String,
    val headerLeft: String,
    val headerRight: String,
    val headerCross: String,
    val leftCross: String,
    val rightCross: String,
    val cross: String,
    val topCross: String,
    val bottomCross: String
) {
    companion object {
        /**
         * Unicode box-drawing characters
         */
        val UNICODE = BorderChars(
            topLeft = "┌",
            topRight = "┐",
            bottomLeft = "└",
            bottomRight = "┘",
            horizontal = "─",
            vertical = "│",
            headerLeft = "├",
            headerRight = "┤",
            headerCross = "┼",
            leftCross = "├",
            rightCross = "┤",
            cross = "┼",
            topCross = "┬",
            bottomCross = "┴"
        )

        /**
         * ASCII-only characters for maximum compatibility
         */
        val ASCII = BorderChars(
            topLeft = "+",
            topRight = "+",
            bottomLeft = "+",
            bottomRight = "+",
            horizontal = "-",
            vertical = "|",
            headerLeft = "+",
            headerRight = "+",
            headerCross = "+",
            leftCross = "+",
            rightCross = "+",
            cross = "+",
            topCross = "+",
            bottomCross = "+"
        )

        /**
         * Minimal style with no borders
         */
        val MINIMAL = BorderChars(
            topLeft = "",
            topRight = "",
            bottomLeft = "",
            bottomRight = "",
            horizontal = "",
            vertical = " ",
            headerLeft = "",
            headerRight = "",
            headerCross = "",
            leftCross = "",
            rightCross = "",
            cross = " ",
            topCross = "",
            bottomCross = ""
        )

        /**
         * Markdown-style table
         */
        val MARKDOWN = BorderChars(
            topLeft = "|",
            topRight = "|",
            bottomLeft = "|",
            bottomRight = "|",
            horizontal = "-",
            vertical = "|",
            headerLeft = "|",
            headerRight = "|",
            headerCross = "|",
            leftCross = "|",
            rightCross = "|",
            cross = "|",
            topCross = "|",
            bottomCross = "|"
        )
    }
}

/**
 * Defines the visual style of a table
 */
data class TableStyle(
    val borderChars: BorderChars = BorderChars.UNICODE,
    val showBorders: Boolean = true,
    val showHeaderSeparator: Boolean = true,
    val padding: Int = 1,
    val compactMode: Boolean = false
) {
    companion object {
        /**
         * Unicode box-drawing style
         */
        val UNICODE = TableStyle(
            borderChars = BorderChars.UNICODE,
            showBorders = true,
            showHeaderSeparator = true,
            padding = 1
        )

        /**
         * ASCII-only style for maximum compatibility
         */
        val ASCII = TableStyle(
            borderChars = BorderChars.ASCII,
            showBorders = true,
            showHeaderSeparator = true,
            padding = 1
        )

        /**
         * Minimal style with no borders
         */
        val MINIMAL = TableStyle(
            borderChars = BorderChars.MINIMAL,
            showBorders = false,
            showHeaderSeparator = false,
            padding = 2
        )

        /**
         * Markdown-compatible table style
         */
        val MARKDOWN = TableStyle(
            borderChars = BorderChars.MARKDOWN,
            showBorders = true,
            showHeaderSeparator = true,
            padding = 1
        )

        /**
         * Compact style with minimal spacing
         */
        val COMPACT = TableStyle(
            borderChars = BorderChars.UNICODE,
            showBorders = true,
            showHeaderSeparator = true,
            padding = 0,
            compactMode = true
        )
    }
}
