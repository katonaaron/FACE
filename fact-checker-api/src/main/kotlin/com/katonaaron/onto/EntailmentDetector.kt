package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface EntailmentDetector {
    fun detectEntailment(ontology: OWLOntology, knowledgeBase: OWLOntology): EntailmentDetectionResult
}
