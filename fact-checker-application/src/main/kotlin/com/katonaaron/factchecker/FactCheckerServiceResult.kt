package com.katonaaron.factchecker

import com.katonaaron.commons.toFormattedString
import com.katonaaron.onto.*

data class Axiom(val sentence: String, val sources: Set<String>)

data class Explanation(val axioms: Set<Axiom>)

data class AxiomExplanation(val axiom: Axiom, val justifications: Set<Explanation>)

data class ConflictExplanation(val inputAxioms: Set<Axiom>, val trustedAxioms: Set<Axiom>)

data class ClassConflictExplanation(val clazz: String, val justifications: Set<ConflictExplanation>)

data class Ontology(val sentences: List<String>, val xml: String)

enum class Decision {
    TRUE,
    FALSE,
    UNKNOWN
}

//@Serializable
data class FactCheckerServiceResult(
    val decision: Decision,
    val inconsistency: Set<ConflictExplanation>,
    val unsatisfiableClasses: Set<ClassConflictExplanation>,
    val entailedAxioms: Set<AxiomExplanation>,
    val learnedOntology: Ontology
)

fun com.katonaaron.onto.Axiom.toDto(verbalizer: OntologyVerbalizer): Axiom = Axiom(
    verbalizer.verbalizeAxiom(this.axiom),
    this.sources.map { it.toString() }.toSet()
)

fun com.katonaaron.onto.Explanation.toDto(verbalizer: OntologyVerbalizer): Explanation = Explanation(
    axioms.map { it.toDto(verbalizer) }.toSet()
)

fun com.katonaaron.onto.AxiomExplanation.toDto(verbalizer: OntologyVerbalizer): AxiomExplanation = AxiomExplanation(
    axiom.toDto(verbalizer),
    justifications.map { it.toDto(verbalizer) }.toSet()
)

fun com.katonaaron.onto.ConflictExplanation.toDto(verbalizer: OntologyVerbalizer): ConflictExplanation =
    ConflictExplanation(
        inputAxioms.map { it.toDto(verbalizer) }.toSet(),
        trustedAxioms.map { it.toDto(verbalizer) }.toSet()
    )

fun com.katonaaron.onto.ClassConflictExplanation.toDto(verbalizer: OntologyVerbalizer): ClassConflictExplanation =
    ClassConflictExplanation(
        clazz.iri.remainder.get()!!,
        justifications.map { it.toDto(verbalizer) }.toSet()
    )

fun FactCheckerResult.toDto(verbalizer: OntologyVerbalizer): FactCheckerServiceResult {
    val decision: Decision
    var inconsistency = setOf<ConflictExplanation>()
    var unsatisfiableClasses = setOf<ClassConflictExplanation>()
    val entailedAxioms = result.entailment.entailedAxioms.map { it.toDto(verbalizer) }.toSet()

    val learnedOntology = Ontology(
        verbalizer.verbalizeOntology(result.alignedOntology),
        result.alignedOntology.toFormattedString()
    )

    when (val res = result) {
        is OntologyFactCheckerResult.True -> {
            decision = Decision.TRUE
        }

        is OntologyFactCheckerResult.False -> {
            decision = Decision.FALSE

            when (val reason = res.reason) {
                is Inconsistency -> inconsistency = reason.explanations.map { it.toDto(verbalizer) }.toSet()
                is Incoherence -> unsatisfiableClasses = reason.explanations.map { it.toDto(verbalizer) }.toSet()
            }
        }

        is OntologyFactCheckerResult.Unknown -> {
            decision = Decision.UNKNOWN
        }
    }

    return FactCheckerServiceResult(decision, inconsistency, unsatisfiableClasses, entailedAxioms, learnedOntology)
}


////@Serializable
//data class Axiom(val sentence: String, val source: String)
//
////@Serializable
//data class Explanation(val axioms: Set<Axiom>)
//
////@Serializable
//data class AxiomExplanation(val axiom: Axiom, val justifications: Set<Explanation>)
//
////@Serializable
//data class ClassExplanation(val clazz: String, val justifications: Set<Explanation>)
//
////@Serializable
//sealed class Conflict
////@Serializable
//object NoConflict : Conflict()
////@Serializable
//data class Inconsistency(val explanations: Set<Explanation>) : Conflict()
////@Serializable
//data class Incoherence(val explanations: Set<ClassExplanation>) : Conflict()
//
////@Serializable
//data class Entailment(val entailedAxioms: Set<AxiomExplanation>)
//
////@Serializable
//sealed class FactCheckerServiceResult {
//    abstract val entailment: Entailment
//
////    @Serializable
//    data class True(override val entailment: Entailment) : FactCheckerServiceResult()
////    @Serializable
//    data class False(val reason: Conflict, override val entailment: Entailment) : FactCheckerServiceResult()
////    @Serializable
//    data class Unknown(override val entailment: Entailment) : FactCheckerServiceResult()
//}
//
//fun com.katonaaron.onto.Axiom.toDto(verbalizer: OntologyVerbalizer): Axiom = Axiom(
//    verbalizer.verbalizeAxiom(this.axiom),
//    this.source.toString()
//)
//
//fun com.katonaaron.onto.Explanation.toDto(verbalizer: OntologyVerbalizer): Explanation = Explanation(
//    axioms.map { it.toDto(verbalizer) }.toSet()
//)
//
//fun com.katonaaron.onto.AxiomExplanation.toDto(verbalizer: OntologyVerbalizer): AxiomExplanation = AxiomExplanation(
//    axiom.toDto(verbalizer),
//    justifications.map { it.toDto(verbalizer) }.toSet()
//)
//
//fun com.katonaaron.onto.ClassExplanation.toDto(verbalizer: OntologyVerbalizer): ClassExplanation = ClassExplanation(
//    clazz.iri.remainder.get()!!,
//    justifications.map { it.toDto(verbalizer) }.toSet()
//)
//
//fun com.katonaaron.onto.Conflict.toDto(verbalizer: OntologyVerbalizer): Conflict = when (this) {
//    is com.katonaaron.onto.NoConflict -> NoConflict
//    is com.katonaaron.onto.Inconsistency -> Inconsistency(this.explanations.map { it.toDto(verbalizer) }.toSet())
//    is com.katonaaron.onto.Incoherence -> Incoherence(this.explanations.map { it.toDto(verbalizer) }.toSet())
//}
//
//fun com.katonaaron.onto.Entailment.toDto(verbalizer: OntologyVerbalizer): Entailment = Entailment(
//    entailedAxioms.map { it.toDto(verbalizer) }.toSet()
//)
//
//fun OntologyFactCheckerResult.toDto(verbalizer: OntologyVerbalizer): FactCheckerServiceResult = when(this) {
//    is OntologyFactCheckerResult.True -> FactCheckerServiceResult.True(entailment.toDto(verbalizer))
//    is OntologyFactCheckerResult.False -> FactCheckerServiceResult.False(reason.toDto(verbalizer), entailment.toDto(verbalizer))
//    is OntologyFactCheckerResult.Unknown -> FactCheckerServiceResult.Unknown(entailment.toDto(verbalizer))
//}
//
//fun FactCheckerResult.toDto(verbalizer: OntologyVerbalizer): FactCheckerServiceResult = result.toDto(verbalizer)
