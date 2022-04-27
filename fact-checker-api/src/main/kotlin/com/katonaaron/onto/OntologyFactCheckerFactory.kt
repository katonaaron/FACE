package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyFactCheckerFactory {
    fun createOntologyFactChecker(
        knowledgeBase: OWLOntology
    ): OntologyFactChecker
}
