package com.katonaaron.factchecker

import com.katonaaron.commons.add
import com.katonaaron.commons.getOWLClass
import com.katonaaron.commons.getOWLObjectProperty
import com.katonaaron.conflict.DefaultConflictDetector
import com.katonaaron.entailment.OwlApiEntailmentDetector
import com.katonaaron.explanation.OwlExplanationGenerator
import com.katonaaron.matcher.LogMap2OntologyMatcher
import com.katonaaron.merge.OwlApiOntologyMerger
import com.katonaaron.onto.False
import com.katonaaron.onto.Incoherence
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntologyManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class DefaultOntologyFactCheckerIntegrationTest {
    private val reasonerFactory = ReasonerFactory()
    private val explanationGenerator = OwlExplanationGenerator(reasonerFactory)
    private val factCheckerFactory = DefaultOntologyFactCheckerFactory(
        LogMap2OntologyMatcher(),
        OwlApiOntologyMerger(),
        DefaultConflictDetector(reasonerFactory, explanationGenerator),
        OwlApiEntailmentDetector(explanationGenerator)
    )

    private val ior: IRI = IRI.create("http://com.katonaaron/test")
    private val iorKb: IRI = IRI.create("$ior-kb")

    @Test
    fun `test incoherence`() {
        val manager: OWLOntologyManager = OWLManager.createOWLOntologyManager()
        val df = manager.owlDataFactory

        // / Initialize knowledge base
        val kb = manager.createOntology(iorKb)

        // Concepts
        val kbAntibiotics = df.getOWLClass("$iorKb#kbAntibiotics")
        val kbVirus = df.getOWLClass("$iorKb#Virus")
        val kbBacteria = df.getOWLClass("$iorKb#Bacteria")

        // Relations
        val kbKill = df.getOWLObjectProperty("$iorKb#Kill")

        // Axioms
        kb.add(
            df.getOWLDisjointClassesAxiom(kbVirus, kbBacteria),
            df.getOWLSubClassOfAxiom(kbAntibiotics, df.getOWLObjectAllValuesFrom(kbKill, kbBacteria)),
        )

        // / Initialize test ontology
        val o = manager.createOntology(ior)

        // Concepts
        val oAntibiotics = df.getOWLClass("$ior#kbAntibiotics")
        val oVirus = df.getOWLClass("$ior#Virus")

        // Relations
        val oKill = df.getOWLObjectProperty("$ior#Kill")

        // Axioms
        kb.add(
            df.getOWLSubClassOfAxiom(oAntibiotics, df.getOWLObjectSomeValuesFrom(oKill, oVirus)),
        )

        // / Perform fact checking
        val fc = factCheckerFactory.createOntologyFactChecker(kb)

        val result = fc.factCheck(o)

        assertIs<False>(result)
        assertTrue(result.entailment.isEmpty)

        val conflict = result.reason
        assertIs<Incoherence>(conflict)

        val classExplanations = conflict.explanations
        assertEquals(1, classExplanations.size)

        val classExplanation = classExplanations.first()
        assertEquals(oAntibiotics.iri.remainder, classExplanation.clazz.iri.remainder)

        val justifications = classExplanation.justifications
        assertEquals(1, justifications.size)

        val justification = justifications.first()
        assertEquals(kb.axioms + o.axioms, justification.axioms)
    }
}
