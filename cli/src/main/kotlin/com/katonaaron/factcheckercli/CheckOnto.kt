package com.katonaaron.factcheckercli


import com.katonaaron.onto.OntologyFactChecker
import com.katonaaron.onto.OntologyVerbalizer
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.required
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.semanticweb.owlapi.apibinding.OWLManager
import java.io.File

@OptIn(ExperimentalCli::class)
class CheckOnto : Subcommand("check-onto", "Perform fact-checking on an ontology"), KoinComponent {
    private val factChecker by inject<OntologyFactChecker>()
    private val verbalizer by inject<OntologyVerbalizer>()

    private val onto by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The path of the ontology to be checked against the knowledge base"
    ).required()

    override fun execute() {
        val o = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(File(onto))

        // Procedure
        println("kb")
        verbalizer.verbalizeOntology(factChecker.knowledgeBase).printOntologySentences()
        println("o")
        verbalizer.verbalizeOntology(o).printOntologySentences()

        val result = factChecker.factCheck(o)
        printOntologyFactCheckerResult(verbalizer, result)
    }
}
