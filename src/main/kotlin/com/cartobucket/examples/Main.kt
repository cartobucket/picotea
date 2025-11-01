package com.cartobucket.examples

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import kotlin.system.exitProcess

@Command(
    name = "pico-tea",
    mixinStandardHelpOptions = true,
    version = ["pico-tea 1.0"],
    description = ["A picocli component library demonstration"],
    subcommands = [
        ProgressBarCommand::class,
        SpinnerCommand::class,
        TableCommand::class
    ]
)
class Main : Runnable {
    override fun run() {
        println("pico-tea - picocli component library")
        println()
        println("Available commands:")
        println("  progressBar  - Demonstrate customizable progress bars")
        println("  spinner      - Demonstrate customizable spinners")
        println("  table        - Demonstrate customizable tables")
        println()
        println("Use 'pico-tea <command> --help' for more information about a command.")
    }
}

fun main(args: Array<String>) {
    exitProcess(CommandLine(Main()).execute(*args))
}
