package com.cartobucket.examples

import com.cartobucket.picocli.components.ProgressBar
import com.cartobucket.picocli.components.ProgressBarStyle
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "progressBar",
    description = ["Demonstrate a progress bar with customizable options"]
)
class ProgressBarCommand : Runnable {

    @Option(
        names = ["--start"],
        description = ["Starting value (default: 0)"],
        defaultValue = "0"
    )
    private var start: Long = 0

    @Option(
        names = ["--end"],
        description = ["Ending value (default: 100)"],
        defaultValue = "100"
    )
    private var end: Long = 100

    @Option(
        names = ["--step-size"],
        description = ["Step increment size (default: 1)"],
        defaultValue = "1"
    )
    private var stepSize: Long = 1

    @Option(
        names = ["--delay"],
        description = ["Delay between steps in milliseconds (default: 50)"],
        defaultValue = "50"
    )
    private var delayMs: Long = 50

    @Option(
        names = ["--label"],
        description = ["Label/prefix for the progress bar"],
        defaultValue = "Processing"
    )
    private var label: String = "Processing"

    @Option(
        names = ["--style"],
        description = ["Visual style: UNICODE, ASCII, MINIMAL, DOTS (default: UNICODE)"],
        defaultValue = "UNICODE"
    )
    private var styleName: String = "UNICODE"

    @Option(
        names = ["--show-rate"],
        description = ["Show processing rate"]
    )
    private var showRate: Boolean = false

    @Option(
        names = ["--show-eta"],
        description = ["Show estimated time to completion"]
    )
    private var showEta: Boolean = false

    @Option(
        names = ["--no-percentage"],
        description = ["Hide percentage display"]
    )
    private var noPercentage: Boolean = false

    @Option(
        names = ["--no-count"],
        description = ["Hide count display"]
    )
    private var noCount: Boolean = false

    @Option(
        names = ["--unit"],
        description = ["Unit name for rate display (default: items)"],
        defaultValue = "items"
    )
    private var unit: String = "items"

    @Option(
        names = ["--indeterminate"],
        description = ["Use indeterminate mode (spinner)"]
    )
    private var indeterminate: Boolean = false

    @Option(
        names = ["--with-messages"],
        description = ["Show changing status messages"]
    )
    private var withMessages: Boolean = false

    override fun run() {
        val style = when (styleName.uppercase()) {
            "ASCII" -> ProgressBarStyle.ASCII
            "MINIMAL" -> ProgressBarStyle.MINIMAL
            "DOTS" -> ProgressBarStyle.DOTS
            else -> ProgressBarStyle.UNICODE
        }

        val total = end - start

        val progressBuilder = ProgressBar.builder()
            .withTotal(total)
            .withLabel(label)
            .withStyle(style)
            .withUnit(unit)
            .showPercentage(!noPercentage)
            .showCount(!noCount)

        if (showRate) {
            progressBuilder.showRate()
        }

        if (showEta) {
            progressBuilder.showEta()
        }

        if (indeterminate) {
            progressBuilder.indeterminate()
        }

        val progress = progressBuilder.build()

        var current = start
        var messageIndex = 0
        val messages = listOf(
            "Initializing...",
            "Loading data...",
            "Processing items...",
            "Calculating results...",
            "Finalizing...",
            "Almost done..."
        )

        while (current < end) {
            if (withMessages && current % (total / 6 + 1) == 0L) {
                progress.message(messages[messageIndex % messages.size])
                messageIndex++
            }

            Thread.sleep(delayMs)

            val nextStep = (current + stepSize).coerceAtMost(end)
            progress.stepTo(nextStep - start)
            current = nextStep
        }

        progress.finish()
    }
}
