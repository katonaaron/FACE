package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface AMROntologyLearner {
    fun learnOntologyFromAmr(amr: String): OWLOntology
}
