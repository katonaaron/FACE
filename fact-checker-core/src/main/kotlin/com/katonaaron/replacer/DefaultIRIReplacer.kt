package com.katonaaron.replacer

import com.katonaaron.commons.logger
import com.katonaaron.onto.IRIReplacer
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.ChangeApplied
import org.semanticweb.owlapi.model.parameters.OntologyCopy
import org.semanticweb.owlapi.util.OWLEntityRenamer

class DefaultIRIReplacer : IRIReplacer {
    override fun replaceIRI(onto: OWLOntology, matching: Map<IRI, IRI>): OWLOntology {
        val man = OWLManager.createOWLOntologyManager()
        val o = man.copyOntology(onto, OntologyCopy.DEEP)

        val renamer = OWLEntityRenamer(man, setOf(o))
        val entities: Map<IRI, OWLEntity> = o.signature.associateBy { it.iri }

        val entity2IRIMap = matching
            .mapKeys { iri ->
                entities[iri.key]
            }
            .filterKeys { it != null }

        logger.trace("entity2IRIMap = $entity2IRIMap")

        val changes = renamer.changeIRI(entity2IRIMap)

        logger.trace("changes = $changes")

        val status = onto.owlOntologyManager.applyChanges(changes)

        if (status == ChangeApplied.UNSUCCESSFULLY) {
            throw RuntimeException("Could not perform change")
        }

        return o
    }
}
