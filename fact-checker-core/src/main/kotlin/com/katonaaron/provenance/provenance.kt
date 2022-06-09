package com.katonaaron.provenance

import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.onto.Axiom
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLDataFactory
import org.semanticweb.owlapi.model.OWLOntology

val PROVENANCE_IRI_ANNOTATION = IRI.create("http://www.w3.org/ns/prov#wasDerivedFrom")

val PROVENANCE_IRI_WORDNET = IRI.create("https://wordnet.princeton.edu/")

const val PROVENANCE_IRI_INPUT_VALUE = "http://katonaaron.com/input"
val PROVENANCE_IRI_INPUT = IRI.create(PROVENANCE_IRI_INPUT_VALUE)

fun annotateProvenance(onto: OWLOntology, source: IRI): OWLOntology {
    val df = onto.owlOntologyManager.owlDataFactory
    val annotation = df.getOWLAnnotation(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION), source)
    return onto.axioms.map {
        it.getAnnotatedAxiom(setOf(annotation))
    }.let {
        axiomsToOntology(it)
    }
}

private val df = OWLManager.createOWLOntologyManager().owlDataFactory

val OWLAxiom.sources: Set<IRI>
    get() = getSources(df)

fun OWLAxiom.getSources(df: OWLDataFactory): Set<IRI> =
    this.getAnnotations(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION))
        .mapNotNull { it.value.asIRI().orNull() }
        .toSet()

fun OWLAxiom.toAxiomWithSource(): Axiom = Axiom(this, sources)
fun OWLAxiom.toAxiomWithSource(df: OWLDataFactory): Axiom = Axiom(this, getSources(df))

