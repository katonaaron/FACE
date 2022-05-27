package com.katonaaron.provenance

import com.katonaaron.commons.axiomsToOntology
import com.katonaaron.onto.Axiom
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLDataFactory
import org.semanticweb.owlapi.model.OWLOntology

val PROVENANCE_IRI_ANNOTATION = IRI.create("http://katonaaron.com/onto#Source")

val PROVENANCE_IRI_WORDNET = IRI.create("https://wordnet.princeton.edu/")

val PROVENANCE_IRI_INPUT = IRI.create("http://katonaaron.com/input")

fun annotateProvenance(onto: OWLOntology, source: IRI): OWLOntology {
    val df = onto.owlOntologyManager.owlDataFactory
    val annotation = df.getOWLAnnotation(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION), source)
    return onto.axioms.map {
        it.getAnnotatedAxiom(setOf(annotation))
    }.let {
        axiomsToOntology(it)
    }
}

val OWLAxiom.source: IRI?
    get() {
        val o = axiomsToOntology(listOf(this))
        val df = o.owlOntologyManager.owlDataFactory
        return getSource(df)
    }

fun OWLAxiom.getSource(df: OWLDataFactory): IRI? =
    this.getAnnotations(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION))
        .firstOrNull()?.value?.asIRI()?.orNull()

fun OWLAxiom.toAxiomWithSource(): Axiom = Axiom(this, source)
fun OWLAxiom.toAxiomWithSource(df: OWLDataFactory): Axiom = Axiom(this, getSource(df))

