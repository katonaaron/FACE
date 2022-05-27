package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface OntologyAligner {

    fun alignOntologies(onto: OWLOntology, base: OWLOntology): Pair<OWLOntology, OWLOntology>
}
