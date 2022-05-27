package com.katonaaron.factchecker

import com.katonaaron.matcher.WordnetOntologyMatcher
import com.katonaaron.onto.*
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultFactCheckerFactory(
    private val learner: OntologyLearner,
    private val ontologyFactCheckerFactory: OntologyFactCheckerFactory
) : FactCheckerFactory {

    constructor(
        reasonerFactory: OWLReasonerFactory,
        learner: OntologyLearner,
        matcher: OntologyMatcher = WordnetOntologyMatcher()
    ) : this(learner, DefaultOntologyFactCheckerFactory(reasonerFactory, matcher))

    override fun createFactChecker(
        knowledgeBase: OWLOntology
    ): FactChecker =
        DefaultFactChecker(
            knowledgeBase,
            ontologyFactCheckerFactory,
            learner
        )
}
