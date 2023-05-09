package com.katonaaron.factchecker

import com.katonaaron.commons.logger
import com.katonaaron.onto.*
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntology
import java.io.File

class FactCheckerService(
    knowledgeBasePath: String,
    factCheckerFactory: FactCheckerFactory,
    private val verbalizer: OntologyVerbalizer
) {
    private val kb: OWLOntology
    private val factChecker: FactChecker

    init {
        val man = OWLManager.createOWLOntologyManager()
        kb = man.loadOntologyFromOntologyDocument(File(knowledgeBasePath))
        factChecker = factCheckerFactory.createFactChecker(kb)
    }

    val knowledgeBase: List<String> = verbalizer.verbalizeOntology(kb)

    fun performFactChecking(text: String): FactCheckerServiceResult {
        logger.trace("Input text: $text")
        logger.trace("Knowledge base:\n$knowledgeBase")

        val result = factChecker.factCheck(text)

        return result.toDto(verbalizer)
    }
}
