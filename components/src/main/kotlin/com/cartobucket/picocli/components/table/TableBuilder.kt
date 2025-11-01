package com.cartobucket.picocli.components.table

import java.io.PrintStream

/**
 * Builder for configuring and creating Table instances
 */
class TableBuilder<T> {
    private val columns = mutableListOf<Table.Column<T>>()
    private var style: TableStyle = TableStyle.UNICODE
    private var width: Int? = null
    private var output: PrintStream = System.out
    private var truncateOverflow: Boolean = true

    /**
     * Add a column to the table
     */
    fun addColumn(
        header: String,
        width: Int? = null,
        align: Alignment = Alignment.LEFT,
        truncate: Boolean = true,
        accessor: (T) -> Any?
    ) = apply {
        columns.add(
            Table.Column(
                header = header,
                accessor = accessor,
                width = width,
                align = align,
                truncate = truncate
            )
        )
    }

    /**
     * Add a column with just header and accessor (most common use case)
     */
    fun addColumn(header: String, accessor: (T) -> Any?) = apply {
        addColumn(header, null, Alignment.LEFT, true, accessor)
    }

    /**
     * Set the visual style
     */
    fun withStyle(style: TableStyle) = apply {
        this.style = style
    }

    /**
     * Set a fixed width for the table (null = auto-detect terminal width)
     */
    fun withWidth(width: Int?) = apply {
        this.width = width
    }

    /**
     * Set the output stream (default: System.out)
     */
    fun withOutput(output: PrintStream) = apply {
        this.output = output
    }

    /**
     * Enable or disable truncation of overflowing content
     */
    fun withTruncateOverflow(truncate: Boolean) = apply {
        this.truncateOverflow = truncate
    }

    /**
     * Build the Table instance with data
     */
    fun build(data: List<T>): Table<T> {
        require(columns.isNotEmpty()) { "Table must have at least one column" }

        return Table(
            data = data,
            columns = columns.toList(),
            style = style,
            fixedWidth = width,
            output = output,
            truncateOverflow = truncateOverflow
        )
    }

    /**
     * Build and immediately render the table with data
     */
    fun render(data: List<T>) {
        build(data).use { table ->
            table.render()
        }
    }
}
