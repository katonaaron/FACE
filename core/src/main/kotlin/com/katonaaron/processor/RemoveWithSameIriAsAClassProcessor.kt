package com.katonaaron.processor

import com.katonaaron.commons.logger
import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.ChangeApplied
import org.semanticweb.owlapi.util.OWLEntityRemover

// Removes entities that are not concepts if they have the same IRIs as concepts
class RemoveWithSameIriAsAClassProcessor(
    private val namespaceFilter: String? = null
) : OntologyProcessor {
    override fun processOntology(onto: OWLOntology) {
        val remover = OWLEntityRemover(setOf(onto))

        val signature = with(onto.signature) {
            namespaceFilter?.let { nsFilter ->
                this.filter { it.iri.namespace == nsFilter }
            } ?: this
        }

        val (concepts, notConcepts) = signature.partition { it.isOWLClass }
        val notConceptsMap = notConcepts.groupBy { it.iri }

        concepts.forEach { concept ->
            val sameIris = notConceptsMap[concept.iri] ?: return@forEach

            sameIris.forEach { entity ->
                entity.accept(remover)
            }
        }

        logger.trace("changes = ${remover.changes}")

        val status = onto.owlOntologyManager.applyChanges(remover.changes)
        if (status == ChangeApplied.UNSUCCESSFULLY) {
            throw RuntimeException("Could not perform change")
        }
    }
}
