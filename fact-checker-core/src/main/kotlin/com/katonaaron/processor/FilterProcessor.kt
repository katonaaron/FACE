package com.katonaaron.processor

import com.katonaaron.commons.filter
import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

class FilterProcessor(
    private val predicate: (OWLEntity) -> Boolean
) : OntologyProcessor {
    override fun processOntology(onto: OWLOntology) {
        onto.filter(predicate)
    }
}
