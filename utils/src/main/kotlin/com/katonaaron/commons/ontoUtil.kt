package com.katonaaron.commons

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.ChangeApplied
import org.semanticweb.owlapi.util.OWLEntityRemover
import org.semanticweb.owlapi.util.OWLEntityRenamer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("OntoUtilKt")

fun removeEntity(onto: OWLOntology, vararg entities: OWLEntity) {
    removeEntity(onto, entities.toList())
}

fun removeEntity(onto: OWLOntology, entities: Iterable<OWLEntity>) {
    val remover = OWLEntityRemover(setOf(onto))

    entities.forEach {
        it.accept(remover)
    }

    val changes = remover.changes
    logger.trace("changes = $changes")
    val status = onto.owlOntologyManager.applyChanges(changes)
    if (status == ChangeApplied.UNSUCCESSFULLY) {
        throw RuntimeException("Could not perform change")
    }
}

fun OWLOntology.filterEntity(predicate: (OWLEntity) -> Boolean) {
    removeEntity(
        this,
        signature.filter { !predicate(it) }
    )
}

fun replaceIRI(onto: OWLOntology, matching: Map<IRI, IRI>) {
    val man = OWLManager.createOWLOntologyManager()

    val renamer = OWLEntityRenamer(man, setOf(onto))
    val entities: Map<IRI, OWLEntity> = onto.signature.associateBy { it.iri }

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
}
