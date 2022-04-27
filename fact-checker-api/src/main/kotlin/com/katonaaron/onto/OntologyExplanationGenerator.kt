package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLDataFactory
import org.semanticweb.owlapi.model.OWLOntology

interface OntologyExplanationGenerator {
    fun explain(ontology: OWLOntology, axiomGen: (OWLDataFactory) -> OWLAxiom): Set<Explanation>

    fun explain(ontology: OWLOntology, axiom: OWLAxiom): Set<Explanation>

    fun explainInconsistency(ontology: OWLOntology): Set<Explanation>
}
