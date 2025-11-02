package com.cartobucket.picocli.components.table

import java.io.PrintStream

/**
 * Builder for configuring and creating VirtualTable instances
 */
class VirtualTableBuilder<T> {
    private val columns = mutableListOf<Table.Column<T>>()
    private var dataSource: DataSource<T>? = null
    private var style: VirtualTableStyle = VirtualTableStyle.DEFAULT
    private var height: Int = 10
    private var output: PrintStream = System.out
    private var onSelect: ((T) -> Unit)? = null

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
     * Set the data source (in-memory list)
     */
    fun withData(data: List<T>) = apply {
        this.dataSource = InMemoryDataSource(data)
    }

    /**
     * Set the data source (custom implementation)
     */
    fun withDataSource(dataSource: DataSource<T>) = apply {
        this.dataSource = dataSource
    }

    /**
     * Set the visual style
     */
    fun withStyle(style: VirtualTableStyle) = apply {
        this.style = style
    }

    /**
     * Set the number of visible rows (excluding header and footer)
     */
    fun withHeight(height: Int) = apply {
        require(height > 0) { "Height must be positive" }
        this.height = height
    }

    /**
     * Set the output stream (default: System.out)
     */
    fun withOutput(output: PrintStream) = apply {
        this.output = output
    }

    /**
     * Set a callback for when user presses Enter on a row
     */
    fun onSelect(callback: (T) -> Unit) = apply {
        this.onSelect = callback
    }

    /**
     * Build the VirtualTable instance
     */
    fun build(): VirtualTable<T> {
        require(columns.isNotEmpty()) { "Table must have at least one column" }
        require(dataSource != null) { "Data source must be provided" }

        return VirtualTable(
            dataSource = dataSource!!,
            columns = columns.toList(),
            style = style,
            height = height,
            output = output,
            onSelect = onSelect
        )
    }
}
