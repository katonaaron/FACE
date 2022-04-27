package com.katonaaron.conflict

import com.katonaaron.onto.*
import org.semanticweb.owlapi.model.OWLClassExpression
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultConflictDetector(
    private val reasonerFactory: OWLReasonerFactory,
    private val explanationGenerator: OntologyExplanationGenerator
) : ConflictDetector {

    override fun detectConflict(ontology: OWLOntology): Conflict {
        val reasoner = reasonerFactory.createReasoner(ontology)

        if (!reasoner.isConsistent) {
            return Inconsistency(explanationGenerator.explainInconsistency(ontology))
        }

        val coherent = reasoner.unsatisfiableClasses.size == 1 // Bottom
        if (!coherent) {
            return reasoner.unsatisfiableClasses.entities
                .filter { !it.isOWLNothing }
                .map { unsatisfiableClass ->
                    ClassExplanation(
                        unsatisfiableClass,
                        explainUnsatisfiableClass(unsatisfiableClass, ontology)
                    )
                }.let { Incoherence(it.toSet()) }
        }

        return NoConflict
    }

    private fun explainUnsatisfiableClass(
        classExpression: OWLClassExpression,
        ontology: OWLOntology
    ): Set<Explanation> =
        explanationGenerator.explain(ontology) { df ->
            df.getOWLSubClassOfAxiom(classExpression, df.owlNothing)
        }
}
