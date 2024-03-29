package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLOntology

interface OntologyVerbalizer {
    fun verbalizeOntology(ontology: OWLOntology): List<String>
    fun verbalizeAxiom(axiom: OWLAxiom): String
}
