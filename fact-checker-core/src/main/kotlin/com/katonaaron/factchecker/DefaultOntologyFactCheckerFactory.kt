package com.katonaaron.factchecker

import com.katonaaron.aligner.DefaultOntologyAligner
import com.katonaaron.conflict.DefaultConflictDetector
import com.katonaaron.entailment.DefaultEntailmentDetector
import com.katonaaron.explanation.OwlExplanationGenerator
import com.katonaaron.matcher.WordnetOntologyMatcher
import com.katonaaron.merge.DefaultOntologyMerger
import com.katonaaron.onto.OntologyFactChecker
import com.katonaaron.onto.OntologyFactCheckerFactory
import com.katonaaron.onto.OntologyMatcher
import com.katonaaron.replacer.DefaultIRIReplacer
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory

class DefaultOntologyFactCheckerFactory(
    private val reasonerFactory: OWLReasonerFactory,
    private val matcher: OntologyMatcher = WordnetOntologyMatcher()
) : OntologyFactCheckerFactory {
    override fun createOntologyFactChecker(
        knowledgeBase: OWLOntology
    ): OntologyFactChecker =
        DefaultOntologyFactChecker(
            knowledgeBase,
            DefaultOntologyAligner(matcher, DefaultIRIReplacer()),
            DefaultConflictDetector(reasonerFactory, OwlExplanationGenerator(reasonerFactory)),
            DefaultEntailmentDetector(OwlExplanationGenerator(reasonerFactory)),
            DefaultOntologyMerger()
        )
}
