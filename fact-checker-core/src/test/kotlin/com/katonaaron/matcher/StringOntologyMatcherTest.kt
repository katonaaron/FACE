package com.katonaaron.matcher

import com.katonaaron.commons.add
import com.katonaaron.commons.getOWLClass
import com.katonaaron.commons.getOWLNamedIndividual
import com.katonaaron.commons.getOWLObjectProperty
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntologyManager
import kotlin.test.Test

internal class StringOntologyMatcherTest {

    private val matcher = StringOntologyMatcher()

    private val ior: IRI = IRI.create("http://com.katonaaron/test")
    private val iorKb: IRI = IRI.create("$ior-kb")
    private val iorResult: IRI = IRI.create("$ior-result")

    @Test
    fun matchOntologies() {
        val manager: OWLOntologyManager = OWLManager.createOWLOntologyManager()
        val df = manager.owlDataFactory

        // / Initialize knowledge base
        val kb = manager.createOntology(iorKb)

        // Concepts
        val kbAntibiotic = df.getOWLClass("$iorKb#Antibiotic")
        val kbVirus = df.getOWLClass("$iorKb#Virus")
        val kbBacteria = df.getOWLClass("$iorKb#Bacteria")
        val kbSarsCov2 = df.getOWLNamedIndividual("$iorKb#SARS-CoV-2")

        // Relations
        val kbKill = df.getOWLObjectProperty("$iorKb#Kill")

        // Axioms
        kb.add(
            df.getOWLDisjointClassesAxiom(kbVirus, kbBacteria),
            df.getOWLSubClassOfAxiom(kbAntibiotic, df.getOWLObjectAllValuesFrom(kbKill, kbBacteria)),
            df.getOWLClassAssertionAxiom(kbVirus, kbSarsCov2)
        )

        // / Initialize test ontology
        val o = manager.createOntology(ior)

        // Concepts
        val oAntibiotic = df.getOWLClass("$ior#Antibiotic")
        val oVirus = df.getOWLClass("$ior#Virus")

        // Relations
        val oKill = df.getOWLObjectProperty("$ior#Kill")

        // Individuals
        val oSarsCov2 = df.getOWLNamedIndividual("$ior#sars-cov-2")

        // Axioms
        o.add(
            df.getOWLSubClassOfAxiom(oAntibiotic, df.getOWLObjectSomeValuesFrom(oKill, oVirus)),
            df.getOWLClassAssertionAxiom(oAntibiotic, oSarsCov2)
        )

      /*  // / Perform matching
        val actualPairs = matcher.matchOntologiesToPairs(iorResult, o, kb)

        val expectedPairs = listOf(
            oAntibiotic to kbAntibiotic,
            oVirus to kbVirus,
            oSarsCov2 to kbSarsCov2,
            oKill to kbKill
        ).map {
            Pair(
                it.first.iri,
                it.second.iri
            )
        }

        assertEquals(expectedPairs.toMap(), actualPairs.toMap())*/
    }
}
