package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

interface OntologyMerger {
    fun merge(resultIri: IRI, vararg ontologies: OWLOntology): OWLOntology
}
