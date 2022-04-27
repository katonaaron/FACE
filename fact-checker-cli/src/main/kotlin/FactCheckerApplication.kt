@file:OptIn(ExperimentalCli::class)

import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

const val PROGRAM_NAME = "factcheck"

fun main(args: Array<String>) {
    val parser = ArgParser(PROGRAM_NAME)

    parser.subcommands(
        Match(),
        Verbalize(),
        CheckOnto(),
        LearnOnto(),
        ProcessFred(),
        Check()
    )
    parser.parse(args)
}
