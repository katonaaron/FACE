package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface VerbalizedFactCheckerFactory {
    fun createVerbalizedFactChecker(
        knowledgeBase: OWLOntology
    ): VerbalizedFactChecker
}
