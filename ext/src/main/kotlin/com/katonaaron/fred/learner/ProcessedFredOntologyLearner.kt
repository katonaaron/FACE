package com.katonaaron.fred.learner

import com.katonaaron.fred.processor.FredOntologyProcessor
import com.katonaaron.learner.ProcessedOntologyLearner
import com.katonaaron.onto.OntologyLearner
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class ProcessedFredOntologyLearner(
    fredURL: String,
    fredKey: String,
    reasonerFactory: OWLReasonerFactory
) : OntologyLearner {
    private val learner = ProcessedOntologyLearner(
        FredOntologyLearner(fredURL, fredKey),
        FredOntologyProcessor(reasonerFactory)
    )

    override fun learnOntologyFromText(text: String): OWLOntology = learner.learnOntologyFromText(text)
}
