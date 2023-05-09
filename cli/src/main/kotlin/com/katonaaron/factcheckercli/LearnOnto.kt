package com.katonaaron.factcheckercli

import com.katonaaron.config.Config
import com.katonaaron.fred.learner.FredOntologyLearner
import com.katonaaron.fred.learner.ProcessedFredOntologyLearner
import com.katonaaron.learner.DummyOntologyLearner
import com.katonaaron.onto.OntologyLearner
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCli::class)
class LearnOnto : Subcommand("learn-onto", "Perform ontology learning"), KoinComponent {
    private val learner by inject<OntologyLearner>()

    private val inputFile by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The input text. Otherwise STDIN will be used."
    )

    private val outputFile by option(
        ArgType.String,
        fullName = "output",
        shortName = "o",
        description = "The filename of the learned ontology. Otherwise STDOUT will be used."
    )

    override fun execute() {
        val text = inputFile?.let {
            File(it).readText()
        } ?: System.`in`.bufferedReader().readText()

        println("Input text: $text")

        val o = learner.learnOntologyFromText(text)

        o.saveOntology(
            OWLXMLDocumentFormat(),
            outputFile?.let {
                FileOutputStream(it)
            } ?: System.out
        )
    }
}
