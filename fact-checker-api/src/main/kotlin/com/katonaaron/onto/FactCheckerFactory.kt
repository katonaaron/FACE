package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface FactCheckerFactory {
    fun createFactChecker(
        knowledgeBase: OWLOntology
    ): FactChecker
}
