package com.katonaaron.merge

import com.katonaaron.onto.OntologyMerger
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.OntologyCopy
import org.semanticweb.owlapi.util.OWLOntologyMerger

class DefaultOntologyMerger : OntologyMerger {
    override fun merge(resultIri: IRI, vararg ontologies: OWLOntology): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()

        ontologies.forEach { man.copyOntology(it, OntologyCopy.DEEP) }

        val merger = OWLOntologyMerger(man, false)

        return merger.createMergedOntology(man, resultIri)
    }
}
