package com.katonaaron.onto

import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass

sealed class Conflict
object NoConflict : Conflict()
data class Inconsistency(val explanations: Set<Explanation>) : Conflict()
data class Incoherence(val explanations: Set<ClassExplanation>) : Conflict()

sealed class FactCheckerResult {
    abstract val entailment: Entailment
}

data class True(override val entailment: Entailment) : FactCheckerResult()
data class False(val reason: Conflict, override val entailment: Entailment) : FactCheckerResult()
data class Unknown(override val entailment: Entailment) : FactCheckerResult()

data class EntailmentDetectionResult(val entailment: Entailment, val isTotalEntailment: Boolean)

data class Entailment(val entailedAxioms: Set<AxiomExplanation>) {
    val isEmpty
        get() = entailedAxioms.isEmpty()
}

data class Explanation(val axioms: Set<OWLAxiom>)
data class AxiomExplanation(val axiom: OWLAxiom, val justifications: Set<Explanation>)
data class ClassExplanation(val clazz: OWLClass, val justifications: Set<Explanation>)
