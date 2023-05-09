package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface FactChecker {
    val knowledgeBase: OWLOntology

    fun factCheck(text: String): FactCheckerResult
}
