package com.katonaaron.merge

import org.junit.Test
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntologyManager
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class OwlApiOntologyMergerTest {
    private val ior: IRI = IRI.create("http://com.katonaaron/test")

    private val merger = OwlApiOntologyMerger()

    @Test
    fun `test merge two ontologies`() {
        val man: OWLOntologyManager = OWLManager.createOWLOntologyManager()
        val df = man.owlDataFactory

        val ior1 = "${ior}1"
        val ior2 = "${ior}2"

        val o1 = man.createOntology(IRI.create(ior1))

        // Concepts
        val virus = df.getOWLClass(IRI.create("$ior1#Virus"))

        // Instances
        val sarsCov2 = df.getOWLNamedIndividual(IRI.create("$ior1#SARS-CoV-2"))

        // Axioms
        man.addAxioms(
            o1,
            setOf(
                df.getOWLClassAssertionAxiom(virus, sarsCov2)
            )
        )

        val o2 = man.createOntology(IRI.create(ior2))

        // Concepts
        val bacteria2 = df.getOWLClass(IRI.create("$ior2#Bacteria"))

        // Instances
        val sarsCov22 = df.getOWLNamedIndividual(IRI.create("$ior2#SARS-CoV-2"))

        // Axioms
        man.addAxioms(
            o2,
            setOf(
                df.getOWLClassAssertionAxiom(bacteria2, sarsCov22)
            )
        )

        val resultIRI = IRI.create("$ior-result")
        val merged = merger.merge(resultIRI, o1, o2)

        assertEquals(resultIRI, merged.ontologyID.ontologyIRI.get())

        val rf: OWLReasonerFactory = ReasonerFactory()
        val r = rf.createReasoner(merged)

        println(o1)
        println(o2)
        println(merged)

        assertTrue(r.isEntailed(o1.axioms + o2.axioms))
    }
}
