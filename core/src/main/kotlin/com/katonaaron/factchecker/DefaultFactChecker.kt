package com.katonaaron.factchecker

import com.katonaaron.onto.FactChecker
import com.katonaaron.onto.FactCheckerResult
import com.katonaaron.onto.OntologyFactCheckerFactory
import com.katonaaron.onto.OntologyLearner
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.OWLOntology
import java.io.FileOutputStream

class DefaultFactChecker(
    knowledgeBase: OWLOntology,
    ontologyFactCheckerFactory: OntologyFactCheckerFactory,
    private val learner: OntologyLearner
) : FactChecker {
    private val ontologyFactChecker = ontologyFactCheckerFactory.createOntologyFactChecker(knowledgeBase)

    override val knowledgeBase: OWLOntology
        get() = ontologyFactChecker.knowledgeBase

    override fun factCheck(text: String): FactCheckerResult {
        // 1. Learn ontology from text
        val o = learner.learnOntologyFromText(text)

        // DEBUG: Saved learned ontology
//        o.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("learned.owl"))

        // 2. Perform fact checking on the learned ontology
        return FactCheckerResult(
            ontologyFactChecker.factCheck(o),
            o
        )
    }

}
