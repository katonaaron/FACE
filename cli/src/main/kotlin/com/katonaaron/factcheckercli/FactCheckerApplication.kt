@file:OptIn(ExperimentalCli::class)

package com.katonaaron.factcheckercli

import com.katonaaron.config.getFaceModule
import com.katonaaron.config.getFaceModuleFromConfig
import com.katonaaron.config.loadConfiguration
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger

const val PROGRAM_NAME = "factcheck"


@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {

    startKoin {
        // use Koin logger
        slf4jLogger()
        // declare modules
        modules(getFaceModuleFromConfig())
    }

    val parser = ArgParser(PROGRAM_NAME)
    parser.subcommands(
        Match(),
        Verbalize(),
        CheckOnto(),
        LearnOnto(),
        Check(),
        KnowledgeBase()
    )

    parser.parse(args.ifEmpty { arrayOf("-h") })
}
