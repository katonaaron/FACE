package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyLearner {
    fun learnOntologyFromText(text: String): OWLOntology
}
