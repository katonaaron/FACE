package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyVerbalizer {
    fun verbalizeOntology(ontology: OWLOntology): String
}
