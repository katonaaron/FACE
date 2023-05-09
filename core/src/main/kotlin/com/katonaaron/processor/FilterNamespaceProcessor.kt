package com.katonaaron.processor

import com.katonaaron.commons.filterEntity
import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.model.OWLOntology

class FilterNamespaceProcessor(
    private val namespaceFilter: String
) : OntologyProcessor {
    override fun processOntology(onto: OWLOntology) {
        onto.filterEntity { !it.isBuiltIn && it.iri.namespace != namespaceFilter }
    }
}
