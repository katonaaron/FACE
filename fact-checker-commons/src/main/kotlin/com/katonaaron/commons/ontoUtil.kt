package com.katonaaron.commons

import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.ChangeApplied
import org.semanticweb.owlapi.util.OWLEntityRemover
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

fun OWLOntology.filter(predicate: (OWLEntity) -> Boolean) {
    removeEntity(
        this,
        signature.filter { !predicate(it) }
    )
}
