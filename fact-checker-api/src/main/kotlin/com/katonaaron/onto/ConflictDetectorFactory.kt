package com.katonaaron.onto

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

interface ConflictDetectorFactory {
    fun createConflictDetector(
        reasonerFactory: OWLReasonerFactory,
        explanationGenerator: OntologyExplanationGenerator
    ): ConflictDetector
}
