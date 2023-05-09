package com.katonaaron.factchecker

import com.katonaaron.matcher.DefaultOntologyMatcher
import com.katonaaron.matcher.WordnetOntologyMatcher
import com.katonaaron.onto.*
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultVerbalizedFactCheckerFactory(
    private val verbalizer: OntologyVerbalizer,
    private val factCheckerFactory: FactCheckerFactory
) : VerbalizedFactCheckerFactory {

    constructor(
        reasonerFactory: OWLReasonerFactory,
        learner: OntologyLearner,
        verbalizer: OntologyVerbalizer,
        matcher: OntologyMatcher = DefaultOntologyMatcher()
    ) : this(verbalizer, DefaultFactCheckerFactory(reasonerFactory, learner, matcher))

    override fun createVerbalizedFactChecker(
        knowledgeBase: OWLOntology
    ): VerbalizedFactChecker =
        DefaultVerbalizedFactChecker(
            knowledgeBase,
            factCheckerFactory,
            verbalizer
        )
}
