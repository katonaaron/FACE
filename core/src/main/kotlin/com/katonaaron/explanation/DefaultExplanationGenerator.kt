package com.katonaaron.explanation

import com.katonaaron.onto.Explanation
import com.katonaaron.onto.ExplanationGenerator
import com.katonaaron.provenance.toAxiomWithSource
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory
import org.semanticweb.owl.explanation.api.ExplanationManager
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLDataFactory
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultExplanationGenerator(
    reasonerFactory: OWLReasonerFactory
) : ExplanationGenerator {
    companion object {
        private const val TIMEOUT = 1000L
    }

    private val genFacInconsistency = InconsistentOntologyExplanationGeneratorFactory(reasonerFactory, TIMEOUT)
    private val genFacDefault = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory)

    override fun explain(ontology: OWLOntology, axiomGen: (OWLDataFactory) -> OWLAxiom): Set<Explanation> =
        explainInternal(genFacDefault, ontology, axiomGen)

    override fun explain(ontology: OWLOntology, axiom: OWLAxiom): Set<Explanation> =
        explainInternal(genFacDefault, ontology) { axiom }

    override fun explainInconsistency(ontology: OWLOntology): Set<Explanation> =
        explainInternal(genFacInconsistency, ontology) { df ->
            df.getOWLSubClassOfAxiom(df.owlThing, df.owlNothing)
        }

    private fun explainInternal(
        genFac: ExplanationGeneratorFactory<OWLAxiom>,
        ontology: OWLOntology,
        axiomGen: (OWLDataFactory) -> OWLAxiom
    ): Set<Explanation> {
        val dataFactory = ontology.owlOntologyManager.owlDataFactory
        val gen = genFac.createExplanationGenerator(ontology)

        return gen.getExplanations(axiomGen(dataFactory))
            .map { explanation -> Explanation(explanation.axioms.map { it.toAxiomWithSource(dataFactory) }.toSet()) }
            .toSet()
    }
}
