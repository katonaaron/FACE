package com.katonaaron.learner

import com.katonaaron.onto.OntologyLearner
import com.katonaaron.onto.OntologyProcessor
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model.OWLOntology
import java.io.FileOutputStream

class ProcessedOntologyLearner(
    private val learner: OntologyLearner,
    private val processor: OntologyProcessor
) : OntologyLearner {
    override fun learnOntologyFromText(text: String): OWLOntology {
        // 1. Learn ontology from text
        val o = learner.learnOntologyFromText(text)

        // DEBUG: Save unprocessed ontology
//        o.saveOntology(OWLXMLDocumentFormat(), FileOutputStream("unprocessed.owl"))

        // 2. Process the ontology
        processor.processOntology(o)

        return o
    }
}
