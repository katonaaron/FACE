package com.katonaaron.provenance

import com.katonaaron.commons.axiomsToOntology
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology

val PROVENANCE_IRI_ANNOTATION = IRI.create("http://katonaaron.com/onto#Source")

val PROVENANCE_IRI_WORDNET = IRI.create("https://wordnet.princeton.edu/")

val PROVENANCE_IRI_INPUT = IRI.create("http://katonaaron.com/input")

fun annotateProvenance(onto: OWLOntology, iri: IRI): OWLOntology {
    val df = onto.owlOntologyManager.owlDataFactory
    val annotation = df.getOWLAnnotation(df.getOWLAnnotationProperty(PROVENANCE_IRI_ANNOTATION), iri)
    return onto.axioms.map {
        it.getAnnotatedAxiom(setOf(annotation))
    }.let {
        axiomsToOntology(it)
    }
}
