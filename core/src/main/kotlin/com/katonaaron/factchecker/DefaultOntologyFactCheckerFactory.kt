package com.katonaaron.factchecker

import com.katonaaron.aligner.DefaultOntologyAligner
import com.katonaaron.conflict.DefaultConflictDetector
import com.katonaaron.entailment.DefaultEntailmentDetector
import com.katonaaron.explanation.DefaultExplanationGenerator
import com.katonaaron.matcher.DefaultOntologyMatcher
import com.katonaaron.merge.DefaultOntologyMerger
import com.katonaaron.onto.OntologyFactChecker
import com.katonaaron.onto.OntologyFactCheckerFactory
import com.katonaaron.onto.OntologyMatcher
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultOntologyFactCheckerFactory(
    private val reasonerFactory: OWLReasonerFactory,
    private val matcher: OntologyMatcher = DefaultOntologyMatcher()
) : OntologyFactCheckerFactory {
    override fun createOntologyFactChecker(
        knowledgeBase: OWLOntology
    ): OntologyFactChecker =
        DefaultOntologyFactChecker(
            knowledgeBase,
            DefaultOntologyAligner(matcher),
            DefaultConflictDetector(reasonerFactory, DefaultExplanationGenerator(reasonerFactory)),
            DefaultEntailmentDetector(DefaultExplanationGenerator(reasonerFactory)),
            DefaultOntologyMerger()
        )
}
