package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyFactChecker {
    fun factCheck(onto: OWLOntology): FactCheckerResult
}
