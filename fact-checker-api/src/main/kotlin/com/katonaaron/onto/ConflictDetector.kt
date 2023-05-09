package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLOntology

interface ConflictDetector {
    fun detectConflict(ontology: OWLOntology): Conflict?
}
