package com.katonaaron.replacer

import com.katonaaron.onto.IRIReplacer
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.OntologyCopy

class DefaultIRIReplacer : IRIReplacer {
    override fun replaceIRI(onto: OWLOntology, matching: Map<IRI, IRI>): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.copyOntology(onto, OntologyCopy.DEEP)

        com.katonaaron.commons.replaceIRI(o, matching)

        return o
    }
}
