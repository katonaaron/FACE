package com.katonaaron.processor

import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.parameters.ChangeApplied
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import org.semanticweb.owlapi.util.OWLEntityRemover

class RemoveClassAndItsInstancesProcessor(
    private val reasonerFactory: OWLReasonerFactory,
    private vararg val classes: OWLClass
) : OntologyProcessor {

    override fun processOntology(onto: OWLOntology) {
        classes.forEach {
            removeClassAndItsInstances(onto, it)
        }
    }

    private fun removeClassAndItsInstances(onto: OWLOntology, clazz: OWLClass) {
        val reasoner = reasonerFactory.createReasoner(onto)
        val remover = OWLEntityRemover(setOf(onto))

        reasoner.getInstances(clazz, true)
            .nodes
            .flatMap { it.entities }
            .forEach { it.accept(remover) }

        clazz.accept(remover)

        val changes = remover.changes
        println("changes = $changes")
        val status = onto.owlOntologyManager.applyChanges(changes)
        if (status == ChangeApplied.UNSUCCESSFULLY) {
            throw RuntimeException("Could not perform change")
        }
    }
}
