package com.katonaaron.factcheckercli

import com.katonaaron.config.Config
import com.katonaaron.factchecker.FactCheckerService
import com.katonaaron.onto.OntologyVerbalizer
import kotlinx.cli.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.PrintStream

@OptIn(ExperimentalCli::class)
class Check : Subcommand("check", "Perform fact-checking on a text"), KoinComponent {
    private val factCheckerService by inject<FactCheckerService>()

    private val inputFile by option(
        ArgType.String,
        fullName = "input",
        shortName = "i",
        description = "The path of the input text file. Otherwise STDIN will be used."
    )

    private val outputFile by option(
        ArgType.String,
        fullName = "output",
        shortName = "o",
        description = "The path of the output text file. Otherwise STDOUT will be used."
    )

    private val learnedPath by option(
        ArgType.String,
        fullName = "learned",
        description = "If given, the learned ontology is saved to this location."
    )

    override fun execute() {
        // Process input
        val text = inputFile?.let {
            File(it).readText()
        } ?: System.`in`.bufferedReader().readText()

        outputFile?.let {
            System.setOut(PrintStream(File(it)))
        }

        println("Input text: $text\n")
        println("Knowledge base:")
        factCheckerService.knowledgeBase.printOntologySentences()
        println()


        // Perform fact checking
        val result = factCheckerService.performFactChecking(text)
        println("Learned ontology:")
        result.learnedOntology.sentences.printOntologySentences()
        println()

        print("Result: ")
        printFactCheckerServiceResult(result)

        learnedPath?.let {
            File(it).writeText(result.learnedOntology.xml)
        }

        // Print result
//        println("Learned ontology:\n${verbalizer.verbalizeOntology(result.learnedOntology)}")
//        println("Result:")
//        printFactCheckerResult(verbalizer, result.result)


        // Instantiate classes
//        val man = OWLManager.createOWLOntologyManager()
//        val learner = dummy?.let { DummyOntologyLearner(it) } ?:
//        ProcessedFredOntologyLearner(
//            config.fred.url,
//            config.fred.key,
//            ReasonerFactory()
//        )
//
//        val matcher = com.katonaaron.factchecker.getMatcher(matcherType)
//
//        val fcFactory = DefaultFactCheckerFactory(learner, ReasonerFactory(), matcher)
//
//        // Process Input
//        val text = inputFile?.let {
//            File(it).readText()
//        } ?: System.`in`.bufferedReader().readText()
//
//        println("text = ${text}")
//
//        val kb = man.loadOntologyFromOntologyDocument(File(knowledgeBase))
//        println("kb:\n" + verbalizer.verbalizeOntology(kb))
//
//        // Perform fact checking
//        val fc = fcFactory.createFactChecker(kb)
//        val result = fc.factCheck(text)
//
//        printFactCheckerResult(verbalizer, result)
    }
}
