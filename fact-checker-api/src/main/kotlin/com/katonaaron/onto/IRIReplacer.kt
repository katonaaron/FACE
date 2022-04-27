package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

interface IRIReplacer {
    fun replaceIRI(onto: OWLOntology, matching: Map<IRI, IRI>): OWLOntology
}
