package com.katonaaron.entailment

import com.katonaaron.onto.*
import org.semanticweb.owl.explanation.impl.blackbox.checker.SatisfiabilityEntailmentChecker.UnsupportedAxiomTypeException
import org.semanticweb.owlapi.model.OWLOntology

class OwlApiEntailmentDetector(
    private val explanationGenerator: OntologyExplanationGenerator
) : EntailmentDetector {

    override fun detectEntailment(ontology: OWLOntology, knowledgeBase: OWLOntology): EntailmentDetectionResult {
        val axiomExplanations = ontology.logicalAxioms.mapNotNull { axiom ->
            try {
                val explanations = explanationGenerator.explain(knowledgeBase, axiom)
                if (explanations.isEmpty()) {
                    null
                } else {
                    AxiomExplanation(
                        axiom,
                        explanations
                    )
                }
            } catch (e: UnsupportedAxiomTypeException) {
                null
            }
        }.toSet()

        val totalEntailment = ontology.logicalAxioms.size == axiomExplanations.size

        return EntailmentDetectionResult(
            Entailment(axiomExplanations),
            totalEntailment
        )
    }
}
