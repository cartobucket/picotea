package com.cartobucket.picocli.components

import java.io.PrintStream

/**
 * Builder for configuring and creating ProgressBar instances
 */
class ProgressBarBuilder {
    private var total: Long = 100
    private var label: String = ""
    private var width: Int? = null
    private var style: ProgressBarStyle = ProgressBarStyle.UNICODE
    private var indeterminate: Boolean = false
    private var showRate: Boolean = false
    private var showEta: Boolean = false
    private var showPercentage: Boolean = true
    private var showCount: Boolean = true
    private var unit: String = "items"
    private var output: PrintStream = System.err
    private var updateIntervalMs: Long = 100

    /**
     * Set the total number of items to process
     */
    fun withTotal(total: Long) = apply {
        this.total = total
        this.indeterminate = false
    }

    /**
     * Set a label/prefix for the progress bar
     */
    fun withLabel(label: String) = apply {
        this.label = label
    }

    /**
     * Set a fixed width for the progress bar (null = auto-detect)
     */
    fun withWidth(width: Int?) = apply {
        this.width = width
    }

    /**
     * Set the visual style
     */
    fun withStyle(style: ProgressBarStyle) = apply {
        this.style = style
    }

    /**
     * Enable indeterminate mode (for unknown totals)
     */
    fun indeterminate() = apply {
        this.indeterminate = true
    }

    /**
     * Show the processing rate (items/sec)
     */
    fun showRate() = apply {
        this.showRate = true
    }

    /**
     * Show estimated time to completion
     */
    fun showEta() = apply {
        this.showEta = true
    }

    /**
     * Show/hide percentage display
     */
    fun showPercentage(show: Boolean) = apply {
        this.showPercentage = show
    }

    /**
     * Show/hide count (current/total)
     */
    fun showCount(show: Boolean) = apply {
        this.showCount = show
    }

    /**
     * Set the unit name for rate display
     */
    fun withUnit(unit: String) = apply {
        this.unit = unit
    }

    /**
     * Set the output stream (default: System.err)
     */
    fun withOutput(output: PrintStream) = apply {
        this.output = output
    }

    /**
     * Set the minimum interval between updates in milliseconds
     */
    fun withUpdateInterval(intervalMs: Long) = apply {
        this.updateIntervalMs = intervalMs
    }

    /**
     * Build the ProgressBar instance
     */
    fun build(): ProgressBar {
        return ProgressBar(
            total = total,
            label = label,
            fixedWidth = width,
            style = style,
            indeterminate = indeterminate,
            showRate = showRate,
            showEta = showEta,
            showPercentage = showPercentage,
            showCount = showCount,
            unit = unit,
            output = output,
            updateIntervalMs = updateIntervalMs
        )
    }
}
