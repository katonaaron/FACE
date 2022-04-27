package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

fun interface OntologyProcessor {
    fun processOntology(onto: OWLOntology)

    fun next(after: OntologyProcessor): OntologyProcessor =
        OntologyProcessor { o -> processOntology(o); after.processOntology(o) }
}
