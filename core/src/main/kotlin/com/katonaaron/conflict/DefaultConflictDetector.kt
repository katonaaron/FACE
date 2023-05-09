package com.katonaaron.conflict

import com.katonaaron.onto.*
import com.katonaaron.provenance.PROVENANCE_IRI_INPUT
import org.semanticweb.owlapi.model.OWLClassExpression
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultConflictDetector(
    private val reasonerFactory: OWLReasonerFactory,
    private val explanationGenerator: ExplanationGenerator,
) : ConflictDetector {

    private fun Set<Explanation>.toConflictExplanations(): Set<ConflictExplanation> {
        return map { it.toConflictExplanation() }.toSet()
    }

    private fun Explanation.toConflictExplanation(): ConflictExplanation {
        val (input, trusted) = splitInputAndTrustedAxioms(axioms)
        return ConflictExplanation(input.map { it.copy(sources = emptySet()) }.toSet(), trusted)
    }

    private fun splitInputAndTrustedAxioms(axioms: Collection<Axiom>): Pair<Set<Axiom>, Set<Axiom>> {
        return axioms.partition {
            it.sources.contains(PROVENANCE_IRI_INPUT)
                // Check if input axiom does not have any other source
                .also { res -> if (res) assert(it.sources.size == 1) }
        }
            .run { first.toSet() to second.toSet() }
    }

    override fun detectConflict(ontology: OWLOntology): Conflict? {
        val reasoner = reasonerFactory.createReasoner(ontology)

        if (!reasoner.isConsistent) {
            return Inconsistency(explanationGenerator.explainInconsistency(ontology).toConflictExplanations())
        }

        val coherent = reasoner.unsatisfiableClasses.size == 1 // Bottom
        if (!coherent) {
            return reasoner.unsatisfiableClasses.entities
                .filter { !it.isOWLNothing }
                .map { unsatisfiableClass ->
                    ClassConflictExplanation(
                        unsatisfiableClass,
                        explainUnsatisfiableClass(unsatisfiableClass, ontology)
                    )
                }.let { Incoherence(it.toSet()) }
        }

        return null
    }

    private fun explainUnsatisfiableClass(
        classExpression: OWLClassExpression,
        ontology: OWLOntology
    ): Set<ConflictExplanation> =
        explanationGenerator.explain(ontology) { df ->
            df.getOWLSubClassOfAxiom(classExpression, df.owlNothing)
        }.toConflictExplanations()
}
