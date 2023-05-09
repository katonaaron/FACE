package com.katonaaron.factchecker

import com.katonaaron.onto.*
import org.semanticweb.owlapi.model.OWLOntology

private fun Axiom.verbalize(verbalizer: OntologyVerbalizer): VerbalizedAxiom = VerbalizedAxiom(
    axiom,
    verbalizer.verbalizeAxiom(axiom),
    sources
)

private fun Explanation.verbalize(verbalizer: OntologyVerbalizer): VerbalizedExplanation = VerbalizedExplanation(
    axioms.map { it.verbalize(verbalizer) }.toSet()
)


private fun AxiomExplanation.verbalize(verbalizer: OntologyVerbalizer): VerbalizedAxiomExplanation =
    VerbalizedAxiomExplanation(
        axiom.verbalize(verbalizer),
        justifications.map { it.verbalize(verbalizer) }.toSet()
    )

private fun ConflictExplanation.verbalize(verbalizer: OntologyVerbalizer): VerbalizedConflictExplanation =
    VerbalizedConflictExplanation(
        inputAxioms.map { it.verbalize(verbalizer) }.toSet(),
        trustedAxioms.map { it.verbalize(verbalizer) }.toSet()
    )

private fun ClassConflictExplanation.verbalize(verbalizer: OntologyVerbalizer): VerbalizedClassConflictExplanation =
    VerbalizedClassConflictExplanation(
        clazz,
        justifications.map { it.verbalize(verbalizer) }.toSet()
    )

private fun Conflict.verbalize(verbalizer: OntologyVerbalizer): VerbalizedConflict = when (this) {
    is Inconsistency -> VerbalizedConflict.Inconsistency(this.explanations.map { it.verbalize(verbalizer) }.toSet())
    is Incoherence -> VerbalizedConflict.Incoherence(this.explanations.map { it.verbalize(verbalizer) }.toSet())
}

private fun Entailment.verbalize(verbalizer: OntologyVerbalizer): VerbalizedEntailment = VerbalizedEntailment(
    entailedAxioms.map { it.verbalize(verbalizer) }.toSet()
)

private fun FactCheckerResult.verbalize(verbalizer: OntologyVerbalizer): VerbalizedFactCheckerResult = result.run {
    when (this) {
        is OntologyFactCheckerResult.True -> VerbalizedFactCheckerResult.True(
            entailment.verbalize(verbalizer),
            learnedOntology,
            alignedOntology
        )

        is OntologyFactCheckerResult.False -> VerbalizedFactCheckerResult.False(
            reason.verbalize(verbalizer),
            entailment.verbalize(verbalizer),
            learnedOntology,
            alignedOntology
        )

        is OntologyFactCheckerResult.Unknown -> VerbalizedFactCheckerResult.Unknown(
            entailment.verbalize(verbalizer),
            learnedOntology,
            alignedOntology
        )
    }
}


class DefaultVerbalizedFactChecker(
    val factChecker: FactChecker,
    val verbalizer: OntologyVerbalizer
) : VerbalizedFactChecker {

    constructor(
        knowledgeBase: OWLOntology,
        factCheckerFactory: FactCheckerFactory,
        verbalizer: OntologyVerbalizer
    ) : this(factCheckerFactory.createFactChecker(knowledgeBase), verbalizer)

    override val knowledgeBase: VerbalizedOntology
        get() = factChecker.knowledgeBase.let {
            VerbalizedOntology(
                it,
                verbalizer.verbalizeOntology(it)
            )
        }


    override fun factCheck(text: String): VerbalizedFactCheckerResult {
        return factChecker.factCheck(text).verbalize(verbalizer)
    }
}
