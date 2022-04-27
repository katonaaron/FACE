package com.katonaaron.conflict

import com.katonaaron.onto.ConflictDetector
import com.katonaaron.onto.ConflictDetectorFactory
import com.katonaaron.onto.OntologyExplanationGenerator
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultConflictDetectorFactory : ConflictDetectorFactory {
    override fun createConflictDetector(
        reasonerFactory: OWLReasonerFactory,
        explanationGenerator: OntologyExplanationGenerator
    ): ConflictDetector =
        DefaultConflictDetector(reasonerFactory, explanationGenerator)
}
