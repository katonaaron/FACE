package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLOntology

data class Axiom(val axiom: OWLAxiom, val source: IRI?)

data class Explanation(val axioms: Set<Axiom>)
data class AxiomExplanation(val axiom: Axiom, val justifications: Set<Explanation>)
data class ClassExplanation(val clazz: OWLClass, val justifications: Set<Explanation>)

sealed class Conflict
object NoConflict : Conflict()
data class Inconsistency(val explanations: Set<Explanation>) : Conflict()
data class Incoherence(val explanations: Set<ClassExplanation>) : Conflict()

data class Entailment(val entailedAxioms: Set<AxiomExplanation>) {
    val isEmpty
        get() = entailedAxioms.isEmpty()
}


data class EntailmentDetectionResult(val entailment: Entailment, val isTotalEntailment: Boolean)

sealed class OntologyFactCheckerResult {
    abstract val entailment: Entailment
    abstract val alignedOntology: OWLOntology

    data class True(override val entailment: Entailment, override val alignedOntology: OWLOntology) :
        OntologyFactCheckerResult()

    data class False(
        val reason: Conflict,
        override val entailment: Entailment,
        override val alignedOntology: OWLOntology
    ) : OntologyFactCheckerResult()

    data class Unknown(override val entailment: Entailment, override val alignedOntology: OWLOntology) :
        OntologyFactCheckerResult()
}

data class FactCheckerResult(val result: OntologyFactCheckerResult, val learnedOntology: OWLOntology)
