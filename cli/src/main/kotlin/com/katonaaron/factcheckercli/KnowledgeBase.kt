package com.katonaaron.factcheckercli

import com.katonaaron.factchecker.FactCheckerService
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

@OptIn(ExperimentalCli::class)
class KnowledgeBase : Subcommand("kb", "Verbalize the knowledge base"), KoinComponent {
    private val factCheckerService by inject<FactCheckerService>()

    override fun execute() {
        factCheckerService.knowledgeBase.printOntologySentences()
    }
}
