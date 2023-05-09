package com.katonaaron.conflict

import com.katonaaron.explanation.DefaultExplanationGenerator
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntologyManager

internal class DefaultConflictDetectorTest {
    private val reasonerFactory = ReasonerFactory()
    private val explanationGenerator = DefaultExplanationGenerator(reasonerFactory)
    private val conflictDetector = DefaultConflictDetector(reasonerFactory, explanationGenerator)

    private val ior: IRI = IRI.create("http://com.katonaaron/test")
    private val manager: OWLOntologyManager = OWLManager.createOWLOntologyManager()

//    @Test
//    fun `test inconsistent ontology`() {
//        val o = manager.createOntology(ior)
//        val df = o.owlOntologyManager.owlDataFactory
//
//        // Concepts
//        val virus = df.getOWLClass("$ior#Virus")
//        val bacteria = df.getOWLClass("$ior#Bacteria")
//
//        // Instances
//        val sarsCov2 = df.getOWLNamedIndividual("$ior#SARS-CoV-2")
//
//        // Axioms
//        o.add(
//            df.getOWLDisjointClassesAxiom(virus, bacteria),
//            df.getOWLClassAssertionAxiom(virus, sarsCov2),
//            df.getOWLClassAssertionAxiom(bacteria, sarsCov2),
//        )
//
//        val result = conflictDetector.detectConflict(o)
//
//        assertIs<Inconsistency>(result)
//
//        val explanations = result.explanations
//        assertEquals(1, explanations.size)
//        assertEquals(o.axioms, explanations.first().axioms.map { it.axiom }.toSet())
//
//        println(explanations)
//    }
//
//    @Test
//    fun `test incoherent ontology`() {
//        val o = manager.createOntology(ior)
//        val df = o.owlOntologyManager.owlDataFactory
//
//        // Concepts
//        val virus = df.getOWLClass("$ior#Virus")
//        val bacteria = df.getOWLClass("$ior#Bacteria")
//
//        // Axioms
//        o.add(
//            df.getOWLDisjointClassesAxiom(virus, bacteria),
//            df.getOWLSubClassOfAxiom(virus, bacteria),
//        )
//
//        val result = conflictDetector.detectConflict(o)
//
//        assertIs<Incoherence>(result)
//
//        val unsatisfiableClassExplanations = result.explanations
//        assertEquals(1, unsatisfiableClassExplanations.size)
//        val unsatisfiableClassExplanation = unsatisfiableClassExplanations.first()
//        assertEquals(virus, unsatisfiableClassExplanation.clazz)
//        val explanations = unsatisfiableClassExplanation.justifications
//        assertEquals(1, explanations.size)
//        assertEquals(o.axioms, explanations.first().axioms.map { it.axiom }.toSet())
//
//        println(unsatisfiableClassExplanations)
//    }
//
//    @Test
//    fun `test consistent and coherent ontology`() {
//        val o = manager.createOntology(ior)
//        val df = o.owlOntologyManager.owlDataFactory
//
//        // Concepts
//        val virus = df.getOWLClass("$ior#Virus")
//        val bacteria = df.getOWLClass("$ior#Bacteria")
//
//        // Instances
//        val sarsCov2 = df.getOWLNamedIndividual("$ior#SARS-CoV-2")
//
//        // Axioms
//        o.add(
//            df.getOWLDisjointClassesAxiom(virus, bacteria),
//            df.getOWLClassAssertionAxiom(virus, sarsCov2)
//        )
//
//        val result = conflictDetector.detectConflict(o)
//
//        assertIs<NoConflict>(result)
//    }
//
//    @Test
//    fun `test inconsistent ontology explanation containing relevant axioms`() {
//        val o = manager.createOntology(ior)
//        val df = o.owlOntologyManager.owlDataFactory
//
//        // Concepts
//        val virus = df.getOWLClass("$ior#Virus")
//        val bacteria = df.getOWLClass("$ior#Bacteria")
//
//        // Instances
//        val sarsCov2 = df.getOWLNamedIndividual("$ior#SARS-CoV-2")
//        val streptococcus = df.getOWLNamedIndividual("$ior#Streptococcus")
//
//        // Axioms
//        val relevantAxioms = setOf(
//            df.getOWLDisjointClassesAxiom(virus, bacteria),
//            df.getOWLClassAssertionAxiom(virus, sarsCov2),
//            df.getOWLClassAssertionAxiom(bacteria, sarsCov2)
//        )
//        o.add(relevantAxioms)
//        o.add(
//            df.getOWLClassAssertionAxiom(bacteria, streptococcus)
//        )
//
//        val result = conflictDetector.detectConflict(o)
//
//        assertIs<Inconsistency>(result)
//
//        val explanations = result.explanations
//        assertEquals(1, explanations.size)
//        assertEquals(relevantAxioms, explanations.first().axioms.map { it.axiom }.toSet())
//
//        println(explanations)
//    }
}
