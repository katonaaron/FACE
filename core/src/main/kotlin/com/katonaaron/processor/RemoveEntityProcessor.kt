package com.katonaaron.processor

import com.katonaaron.commons.removeEntity
import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology

class RemoveEntityProcessor(
    private vararg val entities: OWLEntity
) : OntologyProcessor {

    override fun processOntology(onto: OWLOntology) {
        removeEntity(onto, *entities)
    }
}
