package com.katonaaron.onto

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

interface OntologyExplanationGeneratorFactory {
    fun createExplanationGenerator(reasonerFactory: OWLReasonerFactory): OntologyExplanationGenerator
}
