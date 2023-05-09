package com.katonaaron.onto

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLOntology

data class Axiom(val axiom: OWLAxiom, val sources: Set<IRI>)

data class Explanation(val axioms: Set<Axiom>)
data class AxiomExplanation(val axiom: Axiom, val justifications: Set<Explanation>)

data class ConflictExplanation(val inputAxioms: Set<Axiom>, val trustedAxioms: Set<Axiom>)
data class ClassConflictExplanation(val clazz: OWLClass, val justifications: Set<ConflictExplanation>)


sealed class Conflict
data class Inconsistency(val explanations: Set<ConflictExplanation>) : Conflict()
data class Incoherence(val explanations: Set<ClassConflictExplanation>) : Conflict()

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

data class VerbalizedOntology(val onto: OWLOntology, val sentences: List<String>)

data class VerbalizedAxiom(val axiom: OWLAxiom, val sentence: String, val sources: Set<IRI>)

data class VerbalizedExplanation(val axioms: Set<VerbalizedAxiom>)
data class VerbalizedAxiomExplanation(val axiom: VerbalizedAxiom, val justifications: Set<VerbalizedExplanation>)

data class VerbalizedConflictExplanation(val inputAxioms: Set<VerbalizedAxiom>, val trustedAxioms: Set<VerbalizedAxiom>)
data class VerbalizedClassConflictExplanation(
    val clazz: OWLClass,
    val justifications: Set<VerbalizedConflictExplanation>
)


sealed class VerbalizedConflict {
    data class Inconsistency(val explanations: Set<VerbalizedConflictExplanation>) : VerbalizedConflict()
    data class Incoherence(val explanations: Set<VerbalizedClassConflictExplanation>) : VerbalizedConflict()
}

data class VerbalizedEntailment(val entailedAxioms: Set<VerbalizedAxiomExplanation>) {
    val isEmpty
        get() = entailedAxioms.isEmpty()
}

sealed class VerbalizedFactCheckerResult {
    abstract val entailment: VerbalizedEntailment
    abstract val learnedOntology: OWLOntology
    abstract val alignedOntology: OWLOntology

    data class True(
        override val entailment: VerbalizedEntailment,
        override val learnedOntology: OWLOntology,
        override val alignedOntology: OWLOntology
    ) :
        VerbalizedFactCheckerResult()

    data class False(
        val reason: VerbalizedConflict,
        override val entailment: VerbalizedEntailment,
        override val learnedOntology: OWLOntology,
        override val alignedOntology: OWLOntology
    ) : VerbalizedFactCheckerResult()

    data class Unknown(
        override val entailment: VerbalizedEntailment,
        override val learnedOntology: OWLOntology,
        override val alignedOntology: OWLOntology
    ) :
        VerbalizedFactCheckerResult()
}
