package com.cartobucket.picocli.components.table

import kotlin.math.max
import kotlin.math.min

/**
 * Manages the state of a VirtualTable including navigation and selection
 */
class VirtualTableState<T>(
    private val dataSource: DataSource<T>,
    val visibleHeight: Int
) {
    /**
     * Current highlighted row index (0-based)
     */
    var currentRowIndex: Int = 0
        private set

    /**
     * Top visible row index (for scrolling)
     */
    var scrollOffset: Int = 0
        private set

    /**
     * Selected row indices (for multi-select in future)
     */
    private val selectedIndices = mutableSetOf<Int>()

    /**
     * Total number of rows in the data source
     */
    val totalRows: Int
        get() = dataSource.size

    /**
     * Move up one row
     */
    fun moveUp() {
        if (currentRowIndex > 0) {
            currentRowIndex--
            ensureVisible()
        }
    }

    /**
     * Move down one row
     */
    fun moveDown() {
        if (currentRowIndex < totalRows - 1) {
            currentRowIndex++
            ensureVisible()
        }
    }

    /**
     * Move to the first row
     */
    fun moveHome() {
        currentRowIndex = 0
        ensureVisible()
    }

    /**
     * Move to the last row
     */
    fun moveEnd() {
        currentRowIndex = max(0, totalRows - 1)
        ensureVisible()
    }

    /**
     * Move up by one page
     */
    fun movePageUp() {
        currentRowIndex = max(0, currentRowIndex - visibleHeight)
        ensureVisible()
    }

    /**
     * Move down by one page
     */
    fun movePageDown() {
        currentRowIndex = min(totalRows - 1, currentRowIndex + visibleHeight)
        ensureVisible()
    }

    /**
     * Get the range of visible rows
     */
    fun getVisibleRange(): IntRange {
        val start = scrollOffset
        val end = min(scrollOffset + visibleHeight - 1, totalRows - 1)
        return start..end
    }

    /**
     * Get the visible rows from the data source
     */
    fun getVisibleRows(): List<T> {
        if (totalRows == 0) return emptyList()
        return dataSource.getRange(scrollOffset, visibleHeight)
    }

    /**
     * Check if a row is currently highlighted
     */
    fun isHighlighted(index: Int): Boolean {
        return index == currentRowIndex
    }

    /**
     * Check if a row is selected
     */
    fun isSelected(index: Int): Boolean {
        return index in selectedIndices
    }

    /**
     * Toggle selection of current row
     */
    fun toggleSelection() {
        if (currentRowIndex in selectedIndices) {
            selectedIndices.remove(currentRowIndex)
        } else {
            selectedIndices.add(currentRowIndex)
        }
    }

    /**
     * Get currently selected row
     */
    fun getCurrentSelection(): T? {
        return if (totalRows > 0 && currentRowIndex in 0 until totalRows) {
            dataSource.get(currentRowIndex)
        } else {
            null
        }
    }

    /**
     * Get all selected rows
     */
    fun getSelectedRows(): List<T> {
        return selectedIndices.sorted().mapNotNull { index ->
            if (index in 0 until totalRows) {
                dataSource.get(index)
            } else {
                null
            }
        }
    }

    /**
     * Clear all selections
     */
    fun clearSelection() {
        selectedIndices.clear()
    }

    /**
     * Ensure the current row is visible by adjusting scroll offset
     */
    private fun ensureVisible() {
        // If current row is above visible area, scroll up
        if (currentRowIndex < scrollOffset) {
            scrollOffset = currentRowIndex
        }

        // If current row is below visible area, scroll down
        if (currentRowIndex >= scrollOffset + visibleHeight) {
            scrollOffset = currentRowIndex - visibleHeight + 1
        }

        // Ensure scroll offset is within bounds
        scrollOffset = scrollOffset.coerceIn(0, max(0, totalRows - visibleHeight))
    }

    /**
     * Get the relative position of the current row within the visible area
     * Returns null if current row is not visible
     */
    fun getCurrentRowRelativePosition(): Int? {
        val visibleRange = getVisibleRange()
        return if (currentRowIndex in visibleRange) {
            currentRowIndex - scrollOffset
        } else {
            null
        }
    }
}
