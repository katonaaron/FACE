package com.katonaaron.factchecker

import com.katonaaron.onto.*
import com.katonaaron.replacer.IRIReplacerImpl
import org.semanticweb.owlapi.model.OWLOntology

class DefaultOntologyFactCheckerFactory(
    private val matcher: OntologyMatcher,
    private val merger: OntologyMerger,
    private val conflictDetector: ConflictDetector,
    private val entailmentDetector: EntailmentDetector,
    private val iriReplacer: IRIReplacer = IRIReplacerImpl()
) : OntologyFactCheckerFactory {
    override fun createOntologyFactChecker(
        knowledgeBase: OWLOntology
    ): OntologyFactChecker =
        DefaultOntologyFactChecker(knowledgeBase, matcher, conflictDetector, entailmentDetector, merger, iriReplacer)
}
