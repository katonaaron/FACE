package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyFactChecker {
    val knowledgeBase: OWLOntology

    fun factCheck(onto: OWLOntology): OntologyFactCheckerResult
}
