package com.cartobucket.examples

import com.cartobucket.picocli.components.spinner.Spinner
import com.cartobucket.picocli.components.spinner.SpinnerStyle
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "spinner",
    description = ["Demonstrate a spinner with customizable options"]
)
class SpinnerCommand : Runnable {

    @Option(
        names = ["--duration"],
        description = ["Duration in seconds (default: 3)"],
        defaultValue = "3"
    )
    private var durationSeconds: Int = 3

    @Option(
        names = ["--label"],
        description = ["Label/prefix for the spinner"],
        defaultValue = "Loading"
    )
    private var label: String = "Loading"

    @Option(
        names = ["--style"],
        description = ["Visual style: BRAILLE, DOTS, LINE, ARC, ARROW, BOX, BOUNCE, CIRCLE, GROWING_DOTS, ELLIPSIS, CLOCK, BAR (default: BRAILLE)"],
        defaultValue = "BRAILLE"
    )
    private var styleName: String = "BRAILLE"

    @Option(
        names = ["--with-messages"],
        description = ["Show changing status messages"]
    )
    private var withMessages: Boolean = false

    @Option(
        names = ["--interval"],
        description = ["Update interval in milliseconds (overrides style default)"]
    )
    private var intervalMs: Long? = null

    override fun run() {
        val style = when (styleName.uppercase()) {
            "DOTS" -> SpinnerStyle.DOTS
            "LINE" -> SpinnerStyle.LINE
            "ARC" -> SpinnerStyle.ARC
            "ARROW" -> SpinnerStyle.ARROW
            "BOX" -> SpinnerStyle.BOX
            "BOUNCE" -> SpinnerStyle.BOUNCE
            "CIRCLE" -> SpinnerStyle.CIRCLE
            "GROWING_DOTS" -> SpinnerStyle.GROWING_DOTS
            "ELLIPSIS" -> SpinnerStyle.ELLIPSIS
            "CLOCK" -> SpinnerStyle.CLOCK
            "BAR" -> SpinnerStyle.BAR
            else -> SpinnerStyle.BRAILLE
        }

        val spinnerBuilder = Spinner.builder()
            .withLabel(label)
            .withStyle(style)

        if (intervalMs != null) {
            spinnerBuilder.withUpdateInterval(intervalMs!!)
        }

        val spinner = spinnerBuilder.build()

        val messages = listOf(
            "Initializing...",
            "Connecting to server...",
            "Loading resources...",
            "Processing data...",
            "Finalizing...",
            "Almost done..."
        )

        val startTime = System.currentTimeMillis()
        var messageIndex = 0

        while (System.currentTimeMillis() - startTime < durationSeconds * 1000L) {
            if (withMessages) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                val messageInterval = maxOf(1, durationSeconds / messages.size)
                val newIndex = (elapsed / messageInterval).toInt().coerceAtMost(messages.size - 1)

                if (newIndex != messageIndex) {
                    messageIndex = newIndex
                    spinner.message(messages[messageIndex])
                }
            }

            spinner.update()
            Thread.sleep(style.interval)
        }

        val finalMessage = if (withMessages) "Complete!" else ""
        spinner.finish(finalMessage)
    }
}
