package com.cartobucket.picocli.components.spinner

import java.io.PrintStream

/**
 * Builder for creating customized Spinner instances
 */
class SpinnerBuilder {
    private var label: String = ""
    private var style: SpinnerStyle = SpinnerStyle.BRAILLE
    private var output: PrintStream = System.err
    private var updateIntervalMs: Long? = null

    /**
     * Set the label/prefix for the spinner
     */
    fun withLabel(label: String): SpinnerBuilder {
        this.label = label
        return this
    }

    /**
     * Set the visual style for the spinner
     */
    fun withStyle(style: SpinnerStyle): SpinnerBuilder {
        this.style = style
        return this
    }

    /**
     * Set the output stream (default: System.err)
     */
    fun withOutput(output: PrintStream): SpinnerBuilder {
        this.output = output
        return this
    }

    /**
     * Set the update interval in milliseconds (default: use style's interval)
     */
    fun withUpdateInterval(intervalMs: Long): SpinnerBuilder {
        this.updateIntervalMs = intervalMs
        return this
    }

    /**
     * Build the Spinner instance
     */
    fun build(): Spinner {
        val interval = updateIntervalMs ?: style.interval
        return Spinner(
            label = label,
            style = style,
            output = output,
            updateIntervalMs = interval
        )
    }
}
