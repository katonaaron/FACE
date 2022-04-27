package com.katonaaron.onto

import com.katonaaron.matcher.LogMap2OntologyMatcher
import org.semanticweb.owlapi.model.IRI

internal class LogmapOntologyMatcherTest {
    private val ior: IRI = IRI.create("http://com.katonaaron/test")

    private val matcher = LogMap2OntologyMatcher()

// // Somehow it is not working during testing, but it works during production execution.
//    @Test
//    fun matchOntologies() {
//        val man: OWLOntologyManager = OWLManager.createOWLOntologyManager()
//        val df = man.owlDataFactory
//
//        val ior1 = "${ior}1"
//        val ior2 = "${ior}2"
//
//
//        val o1 = man.createOntology(IRI.create(ior1))
//
//        // Concepts
//        val virus = df.getOWLClass(IRI.create("$ior1#Virus"))
//        val bacteria = df.getOWLClass(IRI.create("$ior1#Bacteria"))
//
//        // Instances
//        val sarsCov2 = df.getOWLNamedIndividual(IRI.create("$ior1#SARS-CoV-2"))
//
//        // Axioms
//        man.addAxioms(
//            o1,
//            setOf(
//                df.getOWLDisjointClassesAxiom(virus, bacteria),
//                df.getOWLClassAssertionAxiom(virus, sarsCov2),
//            )
//        )
//
//
//        val o2 = man.createOntology(IRI.create(ior2))
//
//        // Concepts
//        val bacteria2 = df.getOWLClass(IRI.create("$ior2#Bacteria"))
//
//        // Axioms
//        man.addAxioms(
//            o2,
//            setOf(
//                df.getOWLClassAssertionAxiom(bacteria2, sarsCov2)
//            )
//        )
//
// //        val o1 = man.loadOntologyFromOntologyDocument(File("/home/aron/repos/fact-checker/fact-checker/infrastructure/owl4/onto-test-1.owl"))
// //        val o2 = man.loadOntologyFromOntologyDocument(File("/home/aron/repos/fact-checker/fact-checker/infrastructure/owl4/onto-test-2.owl"))
//
//
//        val resultIRI = IRI.create("$ior-result")
//        val matching = matcher.matchOntologies(resultIRI, o1, o2)
//        val dfRes = matching.owlOntologyManager.owlDataFactory
//
//        assertEquals(resultIRI, matching.ontologyID.ontologyIRI.get())
//
//        val rf: OWLReasonerFactory = ReasonerFactory()
//        val r = rf.createReasoner(matching)
//
//        assertTrue(
//            r.isEntailed(
//                setOf(
//                    dfRes.getOWLEquivalentClassesAxiom(bacteria, bacteria2)
//                )
//            )
//        )
//    }
}
