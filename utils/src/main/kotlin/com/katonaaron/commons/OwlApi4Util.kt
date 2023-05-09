package com.katonaaron.commons

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.model.parameters.OntologyCopy
import org.semanticweb.owlapi.reasoner.OWLReasoner
import java.io.ByteArrayOutputStream

fun OWLOntology.add(vararg axioms: OWLAxiom) {
    owlOntologyManager.addAxioms(this, axioms.toSet())
}

fun OWLOntology.add(axioms: Set<OWLAxiom>) {
    owlOntologyManager.addAxioms(this, axioms)
}

fun OWLOntology.add(axioms: Collection<OWLAxiom>) {
    owlOntologyManager.addAxioms(this, axioms.toSet())
}

fun OWLOntology.remove(vararg axioms: OWLAxiom) {
    owlOntologyManager.removeAxioms(this, axioms.toSet())
}

fun OWLOntology.remove(axioms: Set<OWLAxiom>) {
    owlOntologyManager.removeAxioms(this, axioms)
}

fun OWLDataFactory.getOWLClass(str: String): OWLClass = getOWLClass(IRI.create(str))

fun OWLDataFactory.getOWLNamedIndividual(str: String): OWLNamedIndividual = getOWLNamedIndividual(IRI.create(str))

fun OWLDataFactory.getOWLObjectProperty(str: String): OWLObjectProperty = getOWLObjectProperty(IRI.create(str))

fun OWLOntology.toFormattedString(format: OWLDocumentFormat): String {
    val baos = ByteArrayOutputStream()
    saveOntology(format, baos)
    return baos.toString()
}

fun OWLOntology.toFormattedString(): String {
    val baos = ByteArrayOutputStream()
    saveOntology(baos)
    return baos.toString()
}

fun OWLOntology.clone(man: OWLOntologyManager): OWLOntology {
    return man.copyOntology(this, OntologyCopy.DEEP)
}

fun OWLOntology.clone(): OWLOntology {
    return clone(OWLManager.createOWLOntologyManager())
}

fun axiomsToOntology(vararg axioms: OWLAxiom): OWLOntology {
    return axiomsToOntology(axioms.toSet())
}

fun axiomsToOntology(axioms: Collection<OWLAxiom>): OWLOntology {
    val man = OWLManager.createOWLOntologyManager()
    return man.createOntology(axioms.toSet())
}

data class EntityType(val isOWLClass: Boolean, val isOWLObjectProperty: Boolean, val isOWLNamedIndividual: Boolean) {
    companion object {
        val FALSE = EntityType(isOWLClass = false, isOWLObjectProperty = false, isOWLNamedIndividual = false)
    }
}

// NOTE: Can contain two different types of entities with the same IRI
fun getEntityType(onto: OWLOntology, iri: IRI): EntityType? {
    val entities = onto.signature
        .filter { it.iri == iri }
    if (entities.isEmpty()) {
        return null
    }
    return entities.fold(EntityType.FALSE) { acc: EntityType, entity ->
        when {
            entity.isOWLClass -> acc.copy(isOWLClass = true)
            entity.isOWLObjectProperty -> acc.copy(isOWLObjectProperty = true)
            entity.isOWLNamedIndividual -> acc.copy(isOWLNamedIndividual = true)
            else -> acc
        }
    }
}

val OWLReasoner.isCoherent: Boolean
    get() = unsatisfiableClasses.size == 1 // bottom
